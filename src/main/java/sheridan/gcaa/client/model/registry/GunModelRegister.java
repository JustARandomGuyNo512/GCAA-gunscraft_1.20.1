package sheridan.gcaa.client.model.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.guns.IGunModel;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.items.gun.IGun;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class GunModelRegister {
    private static final Map<IGun, IGunModel> GUN_MODEL_MAP = new Object2ObjectArrayMap<>();
    private static final Map<IGun, DisplayData> GUN_TRANSFORM_MAP = new Object2ObjectArrayMap<>();

    public static void registerModel(IGun gun, IGunModel model) {
        if (!GUN_MODEL_MAP.containsKey(gun)) {
            GUN_MODEL_MAP.put(gun, model);
        }
    }

    public static void registerTransform(IGun gun, DisplayData transform) {
        if (!GUN_TRANSFORM_MAP.containsKey(gun)) {
            GUN_TRANSFORM_MAP.put(gun, transform);
        }
    }

    public static IGunModel getModel(IGun gun) {
        return GUN_MODEL_MAP.get(gun);
    }

    public static DisplayData getDisplayData(IGun gun) {
        return GUN_TRANSFORM_MAP.get(gun);
    }
}
