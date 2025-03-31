package sheridan.gcaa.addon;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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
import sheridan.gcaa.sounds.ModSounds;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public class Addon {
    private static final Gson GSON = new Gson();
    public final String name, prefix;
    public List<Supplier<Item>> guns;
    public Path rootPath;
    public Path assetsPath;
    public boolean completed = false;
    public Map<Supplier<Item>, JsonObject> configMapping = new HashMap<>();
    public List<Runnable> soundRegistry = new ArrayList<>();
    private final Map<String, RegistryObject<SoundEvent>> soundRegistryObjectCache = new HashMap<>();

    protected Addon(String name, String prefix, Path path) {
        this.name = name;
        this.rootPath = path;
        this.assetsPath = path.resolve("assets/" + prefix);
        this.prefix = prefix;
        this.guns = new ArrayList<>();
    }

    public static Addon read(Path p) {
        String name = p.getFileName().toString();
        GCAA.LOGGER.info(" Start Reading addon " + name);
        String prefix;
        if (Files.exists(p.resolve("pack.mcmeta"))) {
            GCAA.LOGGER.info("Reading addon mcmeta for " + name);
            try (BufferedReader reader = Files.newBufferedReader(p.resolve("pack.mcmeta")))  {
                JsonObject object = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                prefix = object.get("pack").getAsJsonObject().get("registry_id").getAsString();
                GCAA.LOGGER.info("Addon prefix is " + prefix);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            GCAA.LOGGER.info("Can't find pack.mcmeta for " + name + " path " + p.resolve("pack.mcmeta") + " skipping");
            return null;
        }
        GCAA.LOGGER.info("Addon object created for " + name + " prefix " + prefix);
        Addon addon = new Addon(name, prefix, p);
        Path guns = p.resolve("assets/" + prefix + "/guns/guns.json");
        if (Files.exists(guns)) {
            try (BufferedReader reader = Files.newBufferedReader(guns))  {
                JsonObject object = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                if (!ModItems.ADDON_ITEMS.containsKey(addon.prefix)) {
                    ModItems.ADDON_ITEMS.put(addon.prefix, DeferredRegister.create(ForgeRegistries.ITEMS, addon.prefix));
                }
                DeferredRegister<Item> itemDeferredRegister = ModItems.ADDON_ITEMS.get(addon.prefix);
                for (String gunName : object.keySet()) {
                    if (hasRegistry(itemDeferredRegister, addon.prefix, gunName)) {
                        GCAA.LOGGER.info("Gun Name: '" + gunName + "' already registered, skipping");
                        continue;
                    }
                    JsonObject gun = object.getAsJsonObject(gunName);
                    RegistryObject<BaseItem> gunRegistryObject = itemDeferredRegister.register(gunName,
                            () -> GunFactory.create(gunName, addon.prefix, gun, addon));
                    if (gunRegistryObject == null) {
                        GCAA.LOGGER.error("Failed to create gun " + gunName);
                        continue;
                    }
                    Supplier<Item> gunSupplier = gunRegistryObject::get;
                    addon.guns.add(gunSupplier);
                    addon.configMapping.put(gunSupplier, gun);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return addon;
            }
        }

        Path sounds = p.resolve("assets/" + prefix + "/sounds.json");
        if (Files.exists(sounds)) {
            try (BufferedReader reader = Files.newBufferedReader(sounds))  {
                JsonObject object = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                for (String soundName : object.keySet()) {
                    String[] split = soundName.split("\\.");
                    addon.soundRegistry.add(() -> {
                        GCAA.LOGGER.info("Registering sound, name  " + split[split.length - 1] + " path " + soundName + " id " + prefix);
                        RegistryObject<SoundEvent> soundEventRegistryObject = ModSounds.registerSound(split[split.length - 1], soundName, prefix);
                        addon.soundRegistryObjectCache.put(soundName, soundEventRegistryObject);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        addon.completed = true;
        return addon;
    }

    public static Addon readZIP() {

        return null;
    }

    private static boolean hasRegistry(DeferredRegister<Item> items, String id, String key) {
        Collection<RegistryObject<Item>> itemEntries = items.getEntries();
        return itemEntries.contains(RegistryObject.create(new ResourceLocation(id, key), ForgeRegistries.ITEMS));
    }

    public static class GunFactory {
        private static final Map<String, gunGetter> FACTORIES = new HashMap<>();

        public interface gunGetter {
            Gun get(JsonObject jsonObject, Addon addon);
        }

        static {
            FACTORIES.put("RIFLE", (json, addon) -> {
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
                String fireSound = json.get("fire_sound").getAsString();
                instance.fireSound = addon.soundRegistryObjectCache.get(fireSound);
                if (json.has("suppressed_fire_sound")) {
                    fireSound = json.get("suppressed_fire_sound").getAsString();
                    instance.suppressedSound = addon.soundRegistryObjectCache.get(fireSound);
                }
                return new Gun(instance);
            });
            FACTORIES.put("PISTOL", FACTORIES.get("RIFLE"));
        }

        public static Gun create(String name, String id, JsonObject gun, Addon addon) {
            try {
                String type = gun.get("type").getAsString();
                gunGetter getter = FACTORIES.getOrDefault(type, null);
                if (getter == null) {
                    throw new RuntimeException("Gun type " + type + " is not registered");
                }
                return getter.get(gun, addon).resetId(id);
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
