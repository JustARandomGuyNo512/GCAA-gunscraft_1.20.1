package sheridan.gcaa.client.model.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import sheridan.gcaa.client.model.guns.IGunModel;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.items.guns.IGun;

import java.util.Map;

public class GunModelRegistry {
    static Map<IGun, IGunModel> gunModelMap = new Object2ObjectArrayMap<>();
    static Map<IGun, DisplayData> gunTransformMap = new Object2ObjectArrayMap<>();

    public static void registerModel(IGun gun, IGunModel model) {
        gunModelMap.put(gun, model);
    }

    public static void registerTransform(IGun gun, DisplayData transform) {
        gunTransformMap.put(gun, transform);
    }

    public static IGunModel getModel(IGun gun) {
        return gunModelMap.get(gun);
    }

    public static DisplayData getDisplayData(IGun gun) {
        return gunTransformMap.get(gun);
    }
}
