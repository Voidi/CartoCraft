package sidben.cartocraft.asm.transformers;

import corelibrary.asm.transformerbases.substitution.SubstituteTransformerCoreBase;
import corelibrary.asm.transformerbases.substitution.Substitutes.SubstituteClass;
import corelibrary.asm.transformerbases.substitution.Substitutes.SubstituteField;
import corelibrary.asm.transformerbases.substitution.Substitutes.SubstituteMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData.MapCoord;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.tree.ClassNode;
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

    @Override
    protected void transformClassPost(ClassNode classNode) throws Exception {
    }

    @SubstituteClass
    static final class InternalSubstituteClass {

        @SubstituteField
        public static final ResourceLocation CUSTOM_ICONS_RESOURCE_LOCATION = new ResourceLocation(CartoCraft.CARTO_CRAFT_MOD_ID, "textures/map/custom_map_icons.png");

        @SubstituteMethod
        private void renderCustomIcons(byte aByte, MapDataTransformer.InternalSubstituteClass mapData) {
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            Minecraft.getMinecraft().getTextureManager().bindTexture(InternalSubstituteClass.CUSTOM_ICONS_RESOURCE_LOCATION);
            for(MapCoord mapCoord : mapData.mapCoords) {
                final float iconSize = 8.0F; // Default vanilla = 4, using a 8x8 icon
                final int iconsPerRow = 4;
                GL11.glPushMatrix();
                GL11.glTranslatef(aByte + mapCoord.centerX / 2.0F + 64.0F, aByte + mapCoord.centerZ / 2.0F + 64.0F, -0.02F);
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScalef(iconSize, iconSize, 3.0F);
                GL11.glTranslatef(-0.125F, 0.125F, 0.0F);
                final double startX = (mapCoord.iconSize % iconsPerRow) / iconsPerRow;
                final double startY = (mapCoord.iconSize / iconsPerRow) / iconsPerRow;
                final double endX = (mapCoord.iconSize % iconsPerRow + 1) / iconsPerRow;
                final double endY = (mapCoord.iconSize / iconsPerRow + 1) / iconsPerRow;
                Tessellator.instance.startDrawingQuads();
                Tessellator.instance.addVertexWithUV(-1.0D, 1.0D, 0.001D, startX, startY);
                Tessellator.instance.addVertexWithUV(1.0D, 1.0D, 0.001D, endX, startY);
                Tessellator.instance.addVertexWithUV(1.0D, -1.0D, 0.001D, endX, endY);
                Tessellator.instance.addVertexWithUV(-1.0D, -1.0D, 0.001D, startX, endY);
                Tessellator.instance.draw();
                GL11.glPopMatrix();
            }
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
        }

    }

}
