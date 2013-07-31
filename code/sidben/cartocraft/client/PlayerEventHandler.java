package sidben.cartocraft.client;


import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.FMLCommonHandler;



public class PlayerEventHandler {

    /*
    @ForgeSubscribe
    public void onLivingUpdateEvent(LivingUpdateEvent event) {
        
        // Check if it is a player
        if (event.entity instanceof EntityPlayer) {
            
            EntityPlayer player = (EntityPlayer) event.entity;


            if (player.getItemInUse() != null)
            {
                ItemStack itemstack = player.getItemInUse();
                System.out.println("Item in Use: " + itemstack.itemID);

            }

            
        }
        
    }
    */

    
    @ForgeSubscribe
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        /*
        System.out.println("onPlayerInteractEvent() - " + FMLCommonHandler.instance().getEffectiveSide());
        System.out.println("    action: " + event.action);
        System.out.println("    player: " + event.entityPlayer);
        System.out.println("    current item: " + event.entityPlayer.getCurrentEquippedItem());
        System.out.println("    coords: " + event.x + ", " + event.y + ", " + event.z);
        System.out.println("    block id: " + event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z));
        */
        
        
        // When a player right-click something.
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            // Get what the player used and what he or she clicked.
            ItemStack usedItem = event.entityPlayer.getCurrentEquippedItem();
            int targetBlockID = event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z);
            
            // Check if the player right-clicked with a map in hand.
            // Process only valid blocks
            if (usedItem != null && usedItem.itemID == Item.map.itemID) {
                
                //--- TEMP: Brick ---/
                if (targetBlockID == Block.brick.blockID) {
                    System.out.println("---------------------------------------------------");
                    System.out.println("Valid");
                    System.out.println("---------------------------------------------------");
                }
                
            }
        }
        
    }


}