package sidben.cartocraft.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;



public class CCFMLLoadingPlugin implements IFMLLoadingPlugin {


    @Override
    public String[] getLibraryRequestClass() {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {CCClassTransformer.class.getName()};
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
        // TODO Auto-generated method stub
        
    }

}
