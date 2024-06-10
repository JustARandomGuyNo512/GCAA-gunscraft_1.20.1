package sheridan.gcaa.client.model.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import sheridan.gcaa.client.model.guns.IGunModel;
import sheridan.gcaa.items.guns.IGun;

import java.util.Map;

public class GunModelRegistry {
    static Map<IGun, IGunModel> gunModelMap = new Object2ObjectArrayMap<>();

    public static void register(IGun gun, IGunModel model) {
        gunModelMap.put(gun, model);
    }

    public static IGunModel getModel(IGun gun) {
        return gunModelMap.get(gun);
    }
}
