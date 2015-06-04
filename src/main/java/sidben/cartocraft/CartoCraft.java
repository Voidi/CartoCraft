package sidben.cartocraft;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sidben.cartocraft.common.PlayerEventHandler;

@Mod(modid = CartoCraft.CARTO_CRAFT_MOD_ID, name = CartoCraft.CARTO_CRAFT_NAME, version = CartoCraft.CARTO_CRAFT_VERSION)
public final class CartoCraft {

	public static final String CARTO_CRAFT_MOD_ID = "CartoCraft", CARTO_CRAFT_NAME = "Carto Craft", CARTO_CRAFT_VERSION = "@VERSION@";
    public static final Logger CARTO_CRAFT_LOGGER = LogManager.getLogger("Carto Craft");

    @Instance(CartoCraft.CARTO_CRAFT_MOD_ID)
    public static CartoCraft instance;

    @EventHandler
    public void load(FMLInitializationEvent event) {
        // Register my custom player event handler
        MinecraftForge.EVENT_BUS.register(PlayerEventHandler.INSTANCE);
    }

}