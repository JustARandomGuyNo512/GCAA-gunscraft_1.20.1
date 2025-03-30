package sheridan.gcaa.client.model.gun;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.addon.Addon;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class GunModelFactory {
    static final Map<String, GunModelSupplier> SUPPLIER_MAP = new HashMap<>();

    public static GunModel createGunModel(JsonObject def, Addon addon) {
        String type = def.get("type").getAsString();
        GunModelSupplier gunModelSupplier = SUPPLIER_MAP.get(type);
        if (gunModelSupplier == null) {
            System.out.println("Unknown gun type: " + type);
            return null;
        }
        return gunModelSupplier.createGunModel(def, addon);
    }

    public interface GunModelSupplier {
        GunModel createGunModel(JsonObject def, Addon addon);
    }

    public static void registerGunModelSupplier(String type, GunModelSupplier supplier) {
        SUPPLIER_MAP.put(type, supplier);
    }

    public static GunModelSupplier getGunModelSupplier(String type) {
        return SUPPLIER_MAP.get(type);
    }

    static {
        registerGunModelSupplier("COMMON_RIFLE_MODEL", (def, addon) -> {
            String id = addon.prefix;
            String geo = def.get("geo").getAsString();
            String texture = def.get("texture").getAsString();
            String animation = def.get("animation").getAsString();
            if (def.has("geo_low") && def.has("texture_low")) {
                String geo_low = def.get("geo_low").getAsString();
                String texture_low = def.get("texture_low").getAsString();
                return new CommonRifleModel(
                        new ResourceLocation(id, geo),
                        new ResourceLocation(id, animation),
                        new ResourceLocation(id, texture),
                        new ResourceLocation(id, geo_low),
                        new ResourceLocation(id, texture_low));
            } else {
                return new CommonRifleModel(
                        new ResourceLocation(id, geo),
                        new ResourceLocation(id, animation),
                        new ResourceLocation(id, texture));
            }
        });

    }
}
