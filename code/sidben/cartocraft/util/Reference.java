package sidben.cartocraft.util;


/*
 * Inspired by EE3 from Pahimar
 */
public class Reference {

    // --- Forces debug mode always on. THIS SHOULD NEVER BE 'TRUE' ON RELEASE BUILDS!!!1!1
    public static final boolean ForceDebug         = false;

    // --- Mod basic info
    public static final String  ModID              = "cartocraft";
    public static final String  ModName            = "CartoCraft";
    public static final String  ModVersion         = "0.1 Alpha";
    public static final String  Channel            = "chSidbenCTC";

    public static final String  ResourcesNamespace = Reference.ModID;

    public static final String  ServerProxyClass   = "sidben.cartocraft.common.CommonProxy";
    public static final String  ClientProxyClass   = "sidben.cartocraft.client.ClientProxy";

}
