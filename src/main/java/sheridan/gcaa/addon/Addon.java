package sheridan.gcaa.addon;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.BaseItem;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGunFireMode;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.items.gun.fireModes.Burst;
import sheridan.gcaa.items.gun.fireModes.Semi;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public class Addon {
    private static final Gson GSON = new Gson();
    public final String name;
    public List<Supplier<Item>> guns;
    public Path path;
    public boolean completed = false;

    protected Addon(String name, Path path) {
        this.name = name;
        this.path = path;
        this.guns = new ArrayList<>();
    }

    public static Addon read(Path p) {
        String name = p.getFileName().toString();
        Addon addon = new Addon(name, p);
        Path guns = p.resolve("assets/gcaa/guns/guns.json");
        if (Files.exists(guns)) {
            try (BufferedReader reader = Files.newBufferedReader(guns))  {
                JsonObject object = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                for (String gunName : object.keySet()) {
                    if (hasRegistry(ModItems.ITEMS, gunName)) {
                        GCAA.LOGGER.info("Gun Name" + gunName + " already registered, skipping");
                        continue;
                    }
                    JsonObject gun = object.getAsJsonObject(gunName);
                    RegistryObject<BaseItem> gunRegistryObject = ModItems.ITEMS.register(gunName,
                            () -> GunFactory.create(gunName, gun));
                    if (gunRegistryObject == null) {
                        GCAA.LOGGER.error("Failed to create gun " + gunName);
                        continue;
                    }
                    addon.guns.add(gunRegistryObject::get);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return addon;
            }
        }
        addon.completed = true;
        return addon;
    }

    public static Addon readZIP() {

        return null;
    }

    private static boolean hasRegistry(DeferredRegister<Item> items, String key) {
        Collection<RegistryObject<Item>> itemEntries = items.getEntries();
        return itemEntries.contains(RegistryObject.create(new ResourceLocation(GCAA.MODID, key), ForgeRegistries.ITEMS));
    }

    public static class GunFactory {
        private static final Map<String, gunGetter> FACTORIES = new HashMap<>();

        public interface gunGetter {
            Gun get(JsonObject jsonObject);
        }

        static {
            FACTORIES.put("RIFLE", (json) -> {
                GunProperties instance = GunProperties.createInstance();
                instance.caliber = new Caliber();
                JsonArray fireModes = json.get("fire_modes").getAsJsonArray();
                List<IGunFireMode> fireModeList = new ArrayList<>();
                for (JsonElement element : fireModes) {
                    String asString = element.getAsString();
                    switch (asString) {
                        case "SEMI" -> fireModeList.add(Semi.SEMI);
                        case "AUTO" -> fireModeList.add(Auto.AUTO);
                        default -> {
                            String[] s = asString.split(" ");
                            if (s.length == 2) {
                                String name = s[0];
                                if ("BURST".equals(name)) {
                                    int rounds = Integer.parseInt(s[1]);
                                    fireModeList.add(new Burst(rounds));
                                }
                            }
                        }
                    }
                }
                instance.fireModes = fireModeList;
                String ammunitionName = json.get("ammunition").getAsString();
                Item value = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ammunitionName));
                if (value instanceof IAmmunition ammunition) {
                    instance.caliber.ammunition = ammunition;
                }

                return new Gun(instance);
            });
            FACTORIES.put("PISTOL", FACTORIES.get("RIFLE"));
        }

        public static Gun create(String name, JsonObject gun) {
            try {
                String type = gun.get("type").getAsString();
                gunGetter getter = FACTORIES.getOrDefault(type, null);
                if (getter == null) {
                    throw new RuntimeException("Gun type " + type + " is not registered");
                }
                return getter.get(gun);
            } catch (Exception e) {
                GCAA.LOGGER.info("Failed to create gun " + name + " due to: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        public static void add(String type, gunGetter factory) {
            if (!FACTORIES.containsKey(type)) {
                FACTORIES.put(type, factory);
            }
        }
    }
}
