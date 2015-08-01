package sidben.cartocraft.asm.transformers;

import corelibrary.asm.transformerbases.substitution.SubstituteTransformerCoreBase;
import corelibrary.asm.transformerbases.substitution.Substitutes.SubstituteClass;
import corelibrary.asm.transformerbases.substitution.Substitutes.SubstituteField;
import net.minecraft.util.ResourceLocation;
import sidben.cartocraft.CartoCraft;
import sidben.cartocraft.asm.transformers.MapItemRendererTransformer.InternalSubstituteClass;

/**
 * Created by Master801 on 7/29/2015 at 3:32 PM.
 * @author Master801
 */
public final class MapItemRendererTransformer extends SubstituteTransformerCoreBase<InternalSubstituteClass> {

    @Override
    protected Class<InternalSubstituteClass> getSubstituteClass(ClassName className) {
        return InternalSubstituteClass.class;
    }

    @Override
    protected ClassName getClassName() {
        return new ClassName("net/minecraft/client/gui/MapItemRenderer", "bbx");
    }

    @Override
    protected boolean outputClass(ClassName className) {
        return true;
    }

    @SubstituteClass
    static final class InternalSubstituteClass {

        @SubstituteField
        public static final ResourceLocation CUSTOM_ICONS_RESOURCE_LOCATION = new ResourceLocation(CartoCraft.CARTO_CRAFT_MOD_ID, "textures/map/custom_map_icons.png");

    }

}
