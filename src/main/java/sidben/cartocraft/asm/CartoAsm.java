package sidben.cartocraft.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.*;
import sidben.cartocraft.asm.transformers.MapDataTransformer;

import java.util.Map;

/**
 * Created by Master801 on 6/4/2015 at 9:16 AM.
 * @author Master801
 */
@Name("Carto Asm")
@MCVersion("1.7.10")
@TransformerExclusions({"sidben.cartocraft.asm."})
public final class CartoAsm implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { MapDataTransformer.class.getCanonicalName() };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
