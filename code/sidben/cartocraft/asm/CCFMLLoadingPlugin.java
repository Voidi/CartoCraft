package sidben.cartocraft.asm;


import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;


@MCVersion(value = "1.6.2")
public class CCFMLLoadingPlugin implements IFMLLoadingPlugin {


    // declare a placeholder for the name and location of the CartoCraftCore_dummy.jar
    public static File location;



    @Override
    public String[] getLibraryRequestClass() {
        return null;
    }


    @Override
    public String[] getASMTransformerClass() {
        return new String[] { CCClassTransformer.class.getName() };
    }


    @Override
    public String getModContainerClass() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public String getSetupClass() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void injectData(Map<String, Object> data) {
        CCFMLLoadingPlugin.location = (File) data.get("coremodLocation");
    }

}
