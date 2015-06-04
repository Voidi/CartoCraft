package sidben.cartocraft.common;

import corelibrary.helpers.PlayerHelper;
import corelibrary.helpers.WorldHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public final class PlayerEventHandler {

    public static final Object INSTANCE = new PlayerEventHandler();

    private PlayerEventHandler() {
    }

    @SubscribeEvent
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.entityPlayer == null || event.world == null || event.isCanceled()) {
            return;
        }

        // Only runs on the server
        if (WorldHelper.isServer(event.world)) {
            // When a player shift and right-clicks a block.
            if (event.action == Action.RIGHT_CLICK_BLOCK && PlayerHelper.isPlayerSneaking(event.entityPlayer)) {
                // Get what the player used and what he or she clicked.
                ItemStack usedItem = event.entityPlayer.getCurrentEquippedItem();
                Block targetBlock = event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z);
                int targetMeta = event.entityPlayer.worldObj.getBlockMetadata(event.x, event.y, event.z);
                // Check if the player right-clicked with a map in hand.
                // Process only valid blocks
                if (usedItem != null && usedItem.getItem() instanceof ItemMap && targetBlock != null && targetMeta > -1) {
                    // Defines the custom icon based on the block clicked
                    byte targetIcon = -1;

                    // --- Wool
                    if (targetBlock == Blocks.wool) {
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
                    if (targetBlock == Blocks.mob_spawner) {
                        targetIcon = 40;
                    }
                    // --- Beacon
                    if (targetBlock == Blocks.beacon) {
                        targetIcon = 41;
                    }
                    // --- Chest, Trapped Chest, Ender Chest
                    if (targetBlock == Blocks.chest | targetBlock == Blocks.trapped_chest | targetBlock == Blocks.ender_chest) {
                        targetIcon = 42;
                    }
                    // --- Nether Portal
                    if (targetBlock == Blocks.portal) {
                        targetIcon = 43;
                    }
                    // --- End Portal
                    if (targetBlock == Blocks.end_portal | targetBlock == Blocks.end_portal_frame) {
                        targetIcon = 44;
                    }
                    // --- Brewing Stand
                    if (targetBlock == Blocks.brewing_stand) {
                        targetIcon = 45;
                    }
                    // --- Enchantment Table, Bookshelf
                    if (targetBlock == Blocks.enchanting_table | targetBlock == Blocks.bookshelf) {
                        targetIcon = 46;
                    }
                    // --- Note Block, Jukebox
                    if (targetBlock == Blocks.noteblock | targetBlock == Blocks.jukebox) {
                        targetIcon = 47;
                    }
                    // --- Anvil
                    if (targetBlock == Blocks.anvil) {
                        targetIcon = 52;
                    }

                    if (targetIcon > -1) {
                        MapHelper.addCustomIcon(((ItemMap)usedItem.getItem()).getMapData(usedItem, event.world), targetIcon, event.x, event.z);
                    }
                }
            }
        }
    }

}
