package sidben.cartocraft.common;

import corelibrary.helpers.ReflectionHelper;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapData.MapCoord;
import sidben.cartocraft.CartoCraft;

import java.lang.reflect.Method;

/**
 * Created by Master801 on 6/4/2015 at 9:23 AM.
 * @author Master801
 */
public final class MapHelper {

    public static void addCustomIcon(MapData mapData, byte customIcon, int xCoord, int zCoord) {
        if (mapData == null || customIcon < 0) {
            return;
        }
        final String methodName = "addCustomIcon";
        Method addCustomIconMethod = ReflectionHelper.getMethod(MapData.class, methodName, ReflectionHelper.createNewClassParameter(byte.class, int.class, int.class), true);
        if (addCustomIconMethod == null) {
            CartoCraft.CARTO_CRAFT_LOGGER.error(String.format("Failed to get method \"%s\" in class \"%s\"|\"(MapData)\"! Did the Asm injections fail?", methodName, MapData.class.getCanonicalName()));
            return;
        }
        ReflectionHelper.invokeMethod(addCustomIconMethod, mapData, ReflectionHelper.createNewObjectParameter(customIcon, xCoord, zCoord));
    }

    public static MapCoord createNewMapCoord(byte iconSize, byte centerX, byte centerZ, byte iconRotation) {
        return ReflectionHelper.createNewObjectFromConstructor(ReflectionHelper.getConstructor(MapCoord.class, ReflectionHelper.createNewClassParameter(byte.class, byte.class, byte.class, byte.class)), ReflectionHelper.createNewObjectParameter(iconSize, centerX, centerZ, iconRotation));
    }

}
