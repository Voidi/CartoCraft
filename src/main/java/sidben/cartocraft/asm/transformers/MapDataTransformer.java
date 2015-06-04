package sidben.cartocraft.asm.transformers;

import corelibrary.asm.transformerbases.substitution.SubstituteTransformerCoreBase;
import corelibrary.asm.transformerbases.substitution.Substitutes.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.MapData.MapCoord;
import org.objectweb.asm.tree.ClassNode;
import sidben.cartocraft.CartoCraft;
import sidben.cartocraft.common.MapHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Master801 on 6/4/2015 at 9:32 AM.
 * @author Master801
 */
public final class MapDataTransformer extends SubstituteTransformerCoreBase {

    @Override
    protected Class<?> getSubstituteClass() {
        return InternalSubstituteClass.class;
    }

    @Override
    protected ClassName getClassName() {
        return new ClassName("net/minecraft/world/storage/MapData", "ayi");
    }

    @Override
    protected void transformClassPost(ClassNode classNode) {
    }

    @Override
    protected boolean outputClass(ClassName className) {
        return true;
    }

    @SubstituteClass
    private static final class InternalSubstituteClass {

        private byte scale;//Internal field
        private int xCenter, zCenter;//Internal fields

        @SubstituteField
        private List<MapCoord> mapCoords = null;

        @SubstituteMethod
        public void addCustomIcon(byte customIcon, int worldXCoord, int worldZCoord) {
            final int factor = 1 << scale;
            final float xCoordFromCenter = (worldXCoord - xCenter) / factor, zCoordFromCenter = (worldZCoord - zCenter) / factor;
            final byte xCoord = new Float((xCoordFromCenter * 2.0F) + 0.5F).byteValue(), zCoord = new Float((zCoordFromCenter * 2.0F) + 0.5F).byteValue();
            if (mapCoords == null) {
                mapCoords = new ArrayList<MapCoord>();
            }
            mapCoords.add(MapHelper.createNewMapCoord(customIcon, xCoord, zCoord, (byte)0));
            markDirty();
        }

        @SubstituteMethod(addToExisting = true)
        public void readFromNBT(NBTTagCompound nbt) {
            NBTTagList list = nbt.getTagList("custom_icons", 10);
            if (list != null && list.tagCount() > 0) {
                for(int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound nbtTagCompound = list.getCompoundTagAt(i);
                    final byte customIcon = nbtTagCompound.getByte("icon"), xCoord = nbtTagCompound.getByte("xCoord"), zCoord = nbtTagCompound.getByte("zCoord");
                    if (customIcon < 0) {
                        CartoCraft.CARTO_CRAFT_LOGGER.error(String.format("Read invalid custom icon! X: %d, Z: %d", xCoord, zCoord));
                        continue;
                    }
                    if (mapCoords == null) {
                        break;
                    }
                    mapCoords.add(MapHelper.createNewMapCoord(customIcon, xCoord, zCoord, (byte)0));
                }
            }
        }

        @SubstituteMethod(addToExisting = true)
        public void writeToNBT(NBTTagCompound nbt) {
            if (mapCoords != null && !mapCoords.isEmpty()) {
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

        private void markDirty() {//Internal method
        }

    }

}
