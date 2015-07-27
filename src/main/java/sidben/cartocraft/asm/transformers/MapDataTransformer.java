package sidben.cartocraft.asm.transformers;

import corelibrary.asm.transformerbases.substitution.SubstituteTransformerCoreBase;
import corelibrary.asm.transformerbases.substitution.Substitutes.Flag;
import corelibrary.asm.transformerbases.substitution.Substitutes.SubstituteClass;
import corelibrary.asm.transformerbases.substitution.Substitutes.SubstituteField;
import corelibrary.asm.transformerbases.substitution.Substitutes.SubstituteMethod;
import corelibrary.helpers.IterableHelper;
import corelibrary.helpers.RandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapData.MapCoord;
import org.apache.logging.log4j.LogManager;
import sidben.cartocraft.CartoCraft;
import sidben.cartocraft.asm.transformers.MapDataTransformer.InternalSubstituteClass;
import sidben.cartocraft.common.MapHelper;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Master801 on 6/4/2015 at 9:32 AM.
 * @author Master801
 */
public final class MapDataTransformer extends SubstituteTransformerCoreBase<Class<InternalSubstituteClass>> {

    public MapDataTransformer() {
        setLogger(LogManager.getLogger("Carto-Asm"));
    }

    @Override
    protected Class<InternalSubstituteClass> getSubstituteClass(ClassName className) {
        return InternalSubstituteClass.class;
    }

    @Override
    protected ClassName getClassName() {
        return new ClassName("net/minecraft/world/storage/MapData", "ayi");
    }

    @Override
    protected boolean outputClass(ClassName className) {
        return true;
    }

    @SubstituteClass
    static final class InternalSubstituteClass {

        @SubstituteField(flag = Flag.INTERNAL)
        private byte scale;

        @SubstituteField(flag = Flag.INTERNAL)
        private int xCenter, zCenter;

        @SubstituteField(flag = Flag.INTERNAL)
        public Map playersHashMap;

        @SubstituteField(flag = Flag.INSTANCE)
        public MapData instance = null;

        @SubstituteField
        private ArrayList<MapCoord> mapCoords = null;

        @SubstituteField
        private boolean sentPacket = false;

        @SubstituteField
        private static final int PACKET_ID_CUSTOM_ICON = 3;

        @SubstituteMethod
        public void addCustomIcon(byte customIcon, int worldXCoord, int worldZCoord) {
            final int factor = 1 << scale;
            final float xCoordFromCenter = (worldXCoord - xCenter) / factor, zCoordFromCenter = (worldZCoord - zCenter) / factor;
            final byte xCoord = (byte)((xCoordFromCenter * 2.0F) + 0.5F), zCoord = (byte)((zCoordFromCenter * 2.0F) + 0.5F);
            if (mapCoords == null) {
                mapCoords = new ArrayList<MapCoord>();
            }
            mapCoords.add(MapHelper.createNewMapCoord(customIcon, xCoord, zCoord, (byte)0));
            markDirty();
        }

        @SubstituteMethod(flag = Flag.ADD_TO_EXISTING)
        public void readFromNBT(NBTTagCompound nbt) {
            NBTTagList list = nbt.getTagList("custom_icons", 10);
            if (list != null && list.tagCount() > 0) {
                for(int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound nbtTagCompound = list.getCompoundTagAt(i);
                    final byte customIcon = nbtTagCompound.getByte("icon"), xCoord = nbtTagCompound.getByte("xCoord"), zCoord = nbtTagCompound.getByte("zCoord");
                    if (customIcon < 0) {
                        CartoCraft.CARTO_CRAFT_LOGGER.error("Read invalid custom icon! X: {}, Z: {}", xCoord, zCoord);
                        continue;
                    }
                    if (mapCoords == null) {
                        mapCoords = new ArrayList<MapCoord>();
                    }
                    mapCoords.add(MapHelper.createNewMapCoord(customIcon, xCoord, zCoord, (byte)0));
                }
            }
        }

        @SubstituteMethod(flag = Flag.ADD_TO_EXISTING)
        public void writeToNBT(NBTTagCompound nbt) {
            if (!IterableHelper.isNullOrEmpty(mapCoords)) {
                NBTTagList nbtTagList = new NBTTagList();
                for(MapCoord mapCoord : mapCoords) {
                    NBTTagCompound nbtTagCompound = new NBTTagCompound();
                    nbtTagCompound.setByte("icon", mapCoord.iconSize);
                    nbtTagCompound.setByte("xCoord", mapCoord.centerX);
                    nbtTagCompound.setByte("zCoord", mapCoord.centerZ);
                    nbtTagList.appendTag(nbtTagCompound);
                }
                nbt.setTag("custom_icons", nbtTagList);
            }
        }

        @SubstituteMethod(flag = Flag.REPLACE)
        public byte[] getUpdatePacketData(ItemStack stack, World world, EntityPlayer player) {
            byte[] data = null;
            if (!sentPacket && !IterableHelper.isNullOrEmpty(mapCoords)) {
                data = new byte[mapCoords.size() * 3 + 1];
                data[0] = InternalSubstituteClass.PACKET_ID_CUSTOM_ICON; // The first byte of the array indicates the type of data returned. (0 = map, 1 = players, 2 = scale)
                for(int i = 0; i < mapCoords.size(); i++) {
                    MapCoord mapCoord = mapCoords.get(i);
                    if (mapCoord == null) {
                        continue;
                    }
                    data[i * 3 + 1] = mapCoord.iconSize;
                    data[i * 3 + 2] = mapCoord.centerX;
                    data[i * 3 + 3] = mapCoord.centerZ;
                }
                sentPacket = true;
            } else {
                MapData.MapInfo mapInfo = (MapData.MapInfo)playersHashMap.get(player);
                if (mapInfo != null) {
                    data = mapInfo.getPlayersOnMap(stack);
                }
            }
            return data;
        }

        @SubstituteMethod(flag = Flag.ADD_TO_EXISTING)
        @SideOnly(Side.CLIENT)
        public void updateMPMapData(byte[] packetData) {
            if (!RandomHelper.isNullOrEmpty(packetData)) {
                switch (packetData[0]) {
                    case InternalSubstituteClass.PACKET_ID_CUSTOM_ICON:
                        mapCoords.clear();
                        for (int i = 0; i < (packetData.length - 1) / 3; i++) {
                            final byte iconNum = packetData[i * 3 + 1], x = packetData[i * 3 + 2], z = packetData[i * 3 + 3];
//                        mapCoords.add(SubstituteTransformerCoreBase.constructInnerClass(MapCoord.class, new Class[] { byte.class, byte.class, byte.class, byte.class }, new Object[] { iconNum, x, z, (byte)0 }));
                            doesStuff(instance);//Call to test method, remove later.
                        }
                        break;
                }
            }
        }

        @SubstituteMethod(flag = Flag.INTERNAL)
        private void markDirty() {//Internal method
        }

        @SubstituteMethod
        private void doesStuff(MapData mapData) {//Test method
        }

    }

}
