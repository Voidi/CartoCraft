package sidben.cartocraft;


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
public class ModVillagerTweaks {

    // The instance of your mod that Forge uses.
    @Instance(Reference.ModID)
    public static ModVillagerTweaks instance;

    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide = Reference.ClientProxyClass, serverSide = Reference.ServerProxyClass)
    public static CommonProxy       proxy;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {}


    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {}


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {}


    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {}




}