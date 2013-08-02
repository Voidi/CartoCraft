package net.minecraft.world.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class MapData extends WorldSavedData
{
    public int xCenter;
    public int zCenter;
    public int dimension;
    public byte scale;

    /** colours */
    public byte[] colors = new byte[16384];

    /**
     * Holds a reference to the MapInfo of the players who own a copy of the map
     */
    public List playersArrayList = new ArrayList();

    /**
     * Holds a reference to the players who own a copy of the map and a reference to their MapInfo
     */
    private Map playersHashMap = new HashMap();
    public Map playersVisibleOnMap = new LinkedHashMap();
    
    // CartoCraft
    public Map customIcons = new HashMap();         //--- List of the custom map icons
    public boolean customIconsSent = false;
    private static final int customIconPacketId = 3;
    
    

    public MapData(String par1Str)
    {
        super(par1Str);
        
        // TEMP Debug
        // this.customIcons.put("icon|-50|-120", new MapCoord(this, (byte)2, (byte)-50, (byte)-120, (byte)0));
        // this.customIcons.put("icon|0|-10", new MapCoord(this, (byte)4, (byte)0, (byte)-10, (byte)0));
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        //DEBUG
        System.out.println("MapData.readFromNBT() - " + cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide());
        
        
        NBTBase dimension = par1NBTTagCompound.getTag("dimension");

        if (dimension instanceof NBTTagByte)
        {
            this.dimension = ((NBTTagByte)dimension).data;
        }
        else
        {
            this.dimension = ((NBTTagInt)dimension).data;
        }

        this.xCenter = par1NBTTagCompound.getInteger("xCenter");
        this.zCenter = par1NBTTagCompound.getInteger("zCenter");
        this.scale = par1NBTTagCompound.getByte("scale");

        if (this.scale < 0)
        {
            this.scale = 0;
        }

        if (this.scale > 4)
        {
            this.scale = 4;
        }

        short short1 = par1NBTTagCompound.getShort("width");
        short short2 = par1NBTTagCompound.getShort("height");

        if (short1 == 128 && short2 == 128)
        {
            this.colors = par1NBTTagCompound.getByteArray("colors");
        }
        else
        {
            byte[] abyte = par1NBTTagCompound.getByteArray("colors");
            this.colors = new byte[16384];
            int i = (128 - short1) / 2;
            int j = (128 - short2) / 2;

            for (int k = 0; k < short2; ++k)
            {
                int l = k + j;

                if (l >= 0 || l < 128)
                {
                    for (int i1 = 0; i1 < short1; ++i1)
                    {
                        int j1 = i1 + i;

                        if (j1 >= 0 || j1 < 128)
                        {
                            this.colors[j1 + l * 128] = abyte[i1 + k * short1];
                        }
                    }
                }
            }
        }
        
        // CartoCraft - Loads custom icons
        // OBS: This part is in SERVER, the info will need to be sent to clients
        NBTTagList iconTagList = par1NBTTagCompound.getTagList("customIcons");
        if (iconTagList != null) {
            
            System.out.println("Found customIcons TagList! " + iconTagList.tagCount());
            
            for (int i = 0; i < iconTagList.tagCount(); i++)
            {
                NBTTagCompound iconTagCompound = (NBTTagCompound) iconTagList.tagAt(i);
                byte iconNum = iconTagCompound.getByte("icon");
                byte x = iconTagCompound.getByte("xPos");
                byte z = iconTagCompound.getByte("zPos");
                String iconKey = getCustomIconKey(x, z);
                
                System.out.println("    Adding icon [" +iconNum+ "] at [" +x+ "],[" +z+ "] with key [" +iconKey+ "]");
                this.customIcons.put(iconKey, new MapCoord(this, iconNum, x, z, (byte)0));
            }
            
        }

    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        //DEBUG
        System.out.println("MapData.writeToNBT()");

        
        par1NBTTagCompound.setInteger("dimension", this.dimension);
        par1NBTTagCompound.setInteger("xCenter", this.xCenter);
        par1NBTTagCompound.setInteger("zCenter", this.zCenter);
        par1NBTTagCompound.setByte("scale", this.scale);
        par1NBTTagCompound.setShort("width", (short)128);
        par1NBTTagCompound.setShort("height", (short)128);
        par1NBTTagCompound.setByteArray("colors", this.colors);

        
        // CartoCraft - Save custom icons
        if (this.customIcons.size() > 0) {
            NBTTagList iconTagList = new NBTTagList();
            NBTTagCompound iconTagCompound;
            MapCoord mapcoord;
            
            for (Iterator iterator = this.customIcons.values().iterator(); iterator.hasNext();)
            {
                mapcoord = (MapCoord)iterator.next();            
                
                iconTagCompound = new NBTTagCompound();
                iconTagCompound.setByte("icon", mapcoord.iconSize);
                iconTagCompound.setByte("xPos", mapcoord.centerX);
                iconTagCompound.setByte("zPos", mapcoord.centerZ);
                
                iconTagList.appendTag(iconTagCompound);
            }
            par1NBTTagCompound.setTag("customIcons", iconTagList);
        }

    }

    /**
     * Adds the player passed to the list of visible players and checks to see which players are visible
     */
    public void updateVisiblePlayers(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
        if (!this.playersHashMap.containsKey(par1EntityPlayer))
        {
            MapInfo mapinfo = new MapInfo(this, par1EntityPlayer);
            this.playersHashMap.put(par1EntityPlayer, mapinfo);
            this.playersArrayList.add(mapinfo);
        }

        if (!par1EntityPlayer.inventory.hasItemStack(par2ItemStack))
        {
            this.playersVisibleOnMap.remove(par1EntityPlayer.getCommandSenderName());
        }

        for (int i = 0; i < this.playersArrayList.size(); ++i)
        {
            MapInfo mapinfo1 = (MapInfo)this.playersArrayList.get(i);

            if (!mapinfo1.entityplayerObj.isDead && (mapinfo1.entityplayerObj.inventory.hasItemStack(par2ItemStack) || par2ItemStack.isOnItemFrame()))
            {
                if (!par2ItemStack.isOnItemFrame() && mapinfo1.entityplayerObj.dimension == this.dimension)
                {
                    this.func_82567_a(0, mapinfo1.entityplayerObj.worldObj, mapinfo1.entityplayerObj.getCommandSenderName(), mapinfo1.entityplayerObj.posX, mapinfo1.entityplayerObj.posZ, (double)mapinfo1.entityplayerObj.rotationYaw);
                }
            }
            else
            {
                this.playersHashMap.remove(mapinfo1.entityplayerObj);
                this.playersArrayList.remove(mapinfo1);
            }
        }

        if (par2ItemStack.isOnItemFrame())
        {
            this.func_82567_a(1, par1EntityPlayer.worldObj, "frame-" + par2ItemStack.getItemFrame().entityId, (double)par2ItemStack.getItemFrame().xPosition, (double)par2ItemStack.getItemFrame().zPosition, (double)(par2ItemStack.getItemFrame().hangingDirection * 90));
        }
    }

    private void func_82567_a(int par1, World par2World, String par3Str, double par4, double par6, double par8)
    {
        /*
        System.out.println("MapData.func_82567_a()");
        System.out.println("    Icons: " + this.customIcons.size());
        System.out.println("    Scale: " + this.scale);
        */
        
        
        int j = 1 << this.scale;
        float f = (float)(par4 - (double)this.xCenter) / (float)j;
        float f1 = (float)(par6 - (double)this.zCenter) / (float)j;
        byte b0 = (byte)((int)((double)(f * 2.0F) + 0.5D));
        byte b1 = (byte)((int)((double)(f1 * 2.0F) + 0.5D));
        byte b2 = 63;
        byte b3;

        if (f >= (float)(-b2) && f1 >= (float)(-b2) && f <= (float)b2 && f1 <= (float)b2)
        {
            par8 += par8 < 0.0D ? -8.0D : 8.0D;
            b3 = (byte)((int)(par8 * 16.0D / 360.0D));

            if (par2World.provider.shouldMapSpin(par3Str, par4, par6, par8))
            {
                int k = (int)(par2World.getWorldInfo().getWorldTime() / 10L);
                b3 = (byte)(k * k * 34187121 + k * 121 >> 15 & 15);
            }
        }
        else
        {
            if (Math.abs(f) >= 320.0F || Math.abs(f1) >= 320.0F)
            {
                this.playersVisibleOnMap.remove(par3Str);
                return;
            }

            par1 = 6;
            b3 = 0;

            if (f <= (float)(-b2))
            {
                b0 = (byte)((int)((double)(b2 * 2) + 2.5D));
            }

            if (f1 <= (float)(-b2))
            {
                b1 = (byte)((int)((double)(b2 * 2) + 2.5D));
            }

            if (f >= (float)b2)
            {
                b0 = (byte)(b2 * 2 + 1);
            }

            if (f1 >= (float)b2)
            {
                b1 = (byte)(b2 * 2 + 1);
            }
        }


// Debug
/*        
System.out.println("func_82567_a(" +par1+ ", world, " + par3Str + ", " +par4+ ", " +par6+ ", " +par8+ ")");
System.out.println("    playersVisibleOnMap.put | Size: " + par1 +  " - x: " + b0 + " - z: " +b1);
*/


        this.playersVisibleOnMap.put(par3Str, new MapCoord(this, (byte)par1, b0, b1, b3));
    }

    /**
     * Get byte array of packet data to send to players on map for updating map data
     */
    public byte[] getUpdatePacketData(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        byte[] result;

        
        // CartoCraft - Adds the custom icons to the packet data sent to players
        if (!this.customIconsSent && this.customIcons.size() > 0) {
            byte[] ibyte = new byte[this.customIcons.size() * 3 + 1];
            ibyte[0] = customIconPacketId;          // The first byte of the array indicates the type of data returned. (0 = map, 1 = players, 2 = scale)
            
            int j = 0;
            for (Iterator iterator = this.customIcons.values().iterator(); iterator.hasNext(); ++j)
            {
                MapCoord mapcoord = (MapCoord)iterator.next();
                ibyte[j * 3 + 1] = mapcoord.iconSize;
                ibyte[j * 3 + 2] = mapcoord.centerX;
                ibyte[j * 3 + 3] = mapcoord.centerZ;
            }

            result = ibyte;
            this.customIconsSent = true;
        }
        else {
            
            // Vanilla code 
            MapInfo mapinfo = (MapInfo)this.playersHashMap.get(par3EntityPlayer);
            result = mapinfo == null ? null : mapinfo.getPlayersOnMap(par1ItemStack);
            
        }
        

        // Debug
        /*
        System.out.println("MapData.getUpdatePacketData - " + cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide());
        System.out.println("    Icons: " + this.customIcons.size());
        if (result != null) {
            System.out.print("    Result: ");
            for (int i = 0; i < result.length; i++)
            {
                System.out.print("[" + result[i] +  "]");                
            }            
            System.out.println("");
        }
        */
        
        
        return result;
    }

    /**
     * Marks a vertical range of pixels as being modified so they will be resent to clients. Parameters: X, lowest Y,
     * highest Y
     */
    public void setColumnDirty(int par1, int par2, int par3)
    {
        super.markDirty();

        for (int l = 0; l < this.playersArrayList.size(); ++l)
        {
            MapInfo mapinfo = (MapInfo)this.playersArrayList.get(l);

            if (mapinfo.field_76209_b[par1] < 0 || mapinfo.field_76209_b[par1] > par2)
            {
                mapinfo.field_76209_b[par1] = par2;
            }

            if (mapinfo.field_76210_c[par1] < 0 || mapinfo.field_76210_c[par1] < par3)
            {
                mapinfo.field_76210_c[par1] = par3;
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Updates the client's map with information from other players in MP
     */
    public void updateMPMapData(byte[] par1ArrayOfByte)
    {
        int i;

        if (par1ArrayOfByte[0] == 0)
        {
            i = par1ArrayOfByte[1] & 255;
            int j = par1ArrayOfByte[2] & 255;

            for (int k = 0; k < par1ArrayOfByte.length - 3; ++k)
            {
                this.colors[(k + j) * 128 + i] = par1ArrayOfByte[k + 3];
            }

            this.markDirty();
        }
        else if (par1ArrayOfByte[0] == 1)
        {
            this.playersVisibleOnMap.clear();

            for (i = 0; i < (par1ArrayOfByte.length - 1) / 3; ++i)
            {
                byte b0 = (byte)(par1ArrayOfByte[i * 3 + 1] >> 4);
                byte b1 = par1ArrayOfByte[i * 3 + 2];
                byte b2 = par1ArrayOfByte[i * 3 + 3];
                byte b3 = (byte)(par1ArrayOfByte[i * 3 + 1] & 15);
                this.playersVisibleOnMap.put("icon-" + i, new MapCoord(this, b0, b1, b2, b3));
            }
        }
        else if (par1ArrayOfByte[0] == 2)
        {
            this.scale = par1ArrayOfByte[1];
        }


        // System.out.println("MapData.updateMPMapData() - " + cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide());

        // CartoCraft - Loads the custom icons sent from the server
        else if (par1ArrayOfByte[0] == customIconPacketId)
        {
            this.customIcons.clear();
            for (i = 0; i < (par1ArrayOfByte.length - 1) / 3; ++i)
            {
                byte iconNum = par1ArrayOfByte[i * 3 + 1];
                byte x = par1ArrayOfByte[i * 3 + 2];
                byte z = par1ArrayOfByte[i * 3 + 3];
                String iconKey = getCustomIconKey(x, z);

                this.customIcons.put(iconKey, new MapCoord(this, iconNum, x, z, (byte)0));
            }

            // this.customIcons.put("icon|-50|-120", new MapCoord(this, (byte)2, (byte)-50, (byte)-120, (byte)0));
            // this.customIcons.put("icon|0|-10", new MapCoord(this, (byte)4, (byte)0, (byte)-10, (byte)0));
        }
    }

    public MapInfo func_82568_a(EntityPlayer par1EntityPlayer)
    {
        MapInfo mapinfo = (MapInfo)this.playersHashMap.get(par1EntityPlayer);

        if (mapinfo == null)
        {
            mapinfo = new MapInfo(this, par1EntityPlayer);
            this.playersHashMap.put(par1EntityPlayer, mapinfo);
            this.playersArrayList.add(mapinfo);
        }

        return mapinfo;
    }
    
    
    
    
    
    
    
    // CartoCraft
    private String getCustomIconKey(byte x, byte z) {
        return String.format("custom_%s_%s", x, z);
    }
    
    
    
    public void addCustomIcon(byte iconNum, byte x, byte z) {
        String iconKey = getCustomIconKey(x, z);
        
        // Remove old icon (if found one)
        if (this.customIcons.containsKey(iconKey)) {
            this.customIcons.remove(iconKey);
        }
        
        // Adds the new icon
        this.customIcons.put(iconKey, new MapCoord(this, iconNum, x, z, (byte)0));

        // Forces the map update
        this.customIconsSent = false;
        super.markDirty();
    }
}
