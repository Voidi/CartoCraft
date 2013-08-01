package sidben.cartocraft;


import net.minecraftforge.common.MinecraftForge;
import sidben.cartocraft.client.PlayerEventHandler;
import sidben.cartocraft.common.CommonProxy;
import sidben.cartocraft.util.Reference;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;



@Mod(modid = Reference.ModID, name = Reference.ModName, version = Reference.ModVersion)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { Reference.Channel })
public class ModCartoCraft {

    // The instance of your mod that Forge uses.
    @Instance(Reference.ModID)
    public static ModCartoCraft instance;

    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide = Reference.ClientProxyClass, serverSide = Reference.ServerProxyClass)
    public static CommonProxy   proxy;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {}


    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {

        // Register my custom player event handler
        PlayerEventHandler playerEventHandler = new PlayerEventHandler();
        MinecraftForge.EVENT_BUS.register(playerEventHandler);

    }


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {}


    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {}




}