package sidben.cartocraft.client;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;



public class PlayerEventHandler {

    /*
     * @ForgeSubscribe
     * public void onLivingUpdateEvent(LivingUpdateEvent event) {
     * 
     * // Check if it is a player
     * if (event.entity instanceof EntityPlayer) {
     * 
     * EntityPlayer player = (EntityPlayer) event.entity;
     * 
     * 
     * if (player.getItemInUse() != null)
     * {
     * ItemStack itemstack = player.getItemInUse();
     * System.out.println("Item in Use: " + itemstack.itemID);
     * 
     * }
     * 
     * 
     * }
     * 
     * }
     */


    @ForgeSubscribe
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        System.out.println("onPlayerInteractEvent() - " + cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide());
        /*
         * System.out.println("onPlayerInteractEvent() - " + FMLCommonHandler.instance().getEffectiveSide());
         * System.out.println("    action: " + event.action);
         * System.out.println("    player: " + event.entityPlayer);
         * System.out.println("    current item: " + event.entityPlayer.getCurrentEquippedItem());
         * System.out.println("    coords: " + event.x + ", " + event.y + ", " + event.z);
         * System.out.println("    block id: " + event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z));
         */

        // Only runs on the server
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {

            // When a player right-click something.
            if (event.action == Action.RIGHT_CLICK_BLOCK) {
                // Get what the player used and what he or she clicked.
                ItemStack usedItem = event.entityPlayer.getCurrentEquippedItem();
                int targetBlockID = event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z);
                int targetMeta = event.entityPlayer.worldObj.getBlockMetadata(event.x, event.y, event.z);

                // Check if the player right-clicked with a map in hand.
                // Process only valid blocks
                if (usedItem != null && usedItem.itemID == Item.map.itemID) {
                    
                    // Defines the custom icon based on the block clicked
                    byte targetIcon = -1;
                    
                    // --- Wool
                    if (targetBlockID == Block.cloth.blockID) {
                        switch (targetMeta) {
                        case 0: targetIcon = 14; break;        // White
                        case 1: targetIcon = 9; break;         // Orange
                        case 2: targetIcon = 8; break;         // Magenta
                        case 3: targetIcon = 6; break;         // Light Blue
                        case 4: targetIcon = 15; break;        // Yellow
                        case 5: targetIcon = 7; break;         // Lime
                        case 6: targetIcon = 10; break;        // Pink
                        case 7: targetIcon = 4; break;         // Gray
                        case 8: targetIcon = 13; break;        // Light Gray
                        case 9: targetIcon = 3; break;         // Cyan
                        case 10: targetIcon = 11; break;       // Purple
                        case 11: targetIcon = 1; break;        // Blue
                        case 12: targetIcon = 2; break;        // Brown
                        case 13: targetIcon = 5; break;        // Green
                        case 14: targetIcon = 12; break;       // Red
                        case 15: targetIcon = 0; break;        // Black
                        }
                    }

                    // --- Mob Spawner
                    else if (targetBlockID == Block.mobSpawner.blockID) {
                        targetIcon = 40;
                    }

                    // --- Beacon
                    else if (targetBlockID == Block.beacon.blockID) {
                        targetIcon = 41;
                    }
                    
                    // --- Chest, Trapped Chest, Ender Chest
                    else if (targetBlockID == Block.chest.blockID || targetBlockID == Block.chestTrapped.blockID || targetBlockID == Block.enderChest.blockID) {
                        targetIcon = 42;
                    }

                    // --- Nether Portal
                    else if (targetBlockID == Block.portal.blockID) {
                        targetIcon = 43;
                    }
                    
                    // --- End Portal
                    else if (targetBlockID == Block.endPortal.blockID || targetBlockID == Block.endPortalFrame.blockID) {
                        targetIcon = 44;
                    }
                    
                    // --- Brewing Stand
                    else if (targetBlockID == Block.brewingStand.blockID) {
                        targetIcon = 45;
                    }
                    
                    // --- Enchantment Table, Bookshelf
                    else if (targetBlockID == Block.enchantmentTable.blockID || targetBlockID == Block.bookShelf.blockID) {
                        targetIcon = 46;
                    }
                    
                    // --- Note Block, Jukebox
                    else if (targetBlockID == Block.music.blockID || targetBlockID == Block.jukebox.blockID) {
                        targetIcon = 47;
                    }
                    
                    // --- Anvil
                    else if (targetBlockID == Block.anvil.blockID) {
                        targetIcon = 52;
                    }

                    
                    // Adds the custom icon
                    if (targetIcon > -1) {
                        MapData mapdata = ((ItemMap)usedItem.getItem()).getMapData(usedItem, event.entityPlayer.worldObj);
                        mapdata.addCustomIcon(targetIcon, event.x, event.z);
                    }

                }
            }
            
        }
        

    }


}
