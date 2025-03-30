package sheridan.gcaa.data.gun;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.data.IJsonSyncable;
import sheridan.gcaa.data.Utils;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.UpdateGunPropertiesPacket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = GCAA.MODID)
public class GunPropertiesHandler extends SimplePreparableReloadListener<Map<String, JsonObject>> {
    public static Map<String, JsonObject> loadedProperties = new HashMap<>();
    private static byte[] cache = null;
    private static String strCache = null;

    @Override
    protected @NotNull Map<String, JsonObject> prepare(@NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        Map<String, JsonObject> propertiesMap = new HashMap<>();
        for (Gun gun : Gun.getAllInstances()) {
            String fileName = gun.getDescriptionId().split("\\.")[2];
            String forgeKey = String.valueOf(ForgeRegistries.ITEMS.getKey(gun));
            String name = "gun_properties/" + fileName + ".json";
            ResourceLocation location = new ResourceLocation(gun.id, name);
            pResourceManager.getResource(location).ifPresent(res -> {
                try {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(res.open(), StandardCharsets.UTF_8))) {
                        JsonObject jsonObject = GsonHelper.fromJson(IJsonSyncable.GSON, reader, JsonObject.class);
                        propertiesMap.put(forgeKey, jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return propertiesMap;
    }

    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent event) {
        GunPropertiesHandler handler = new GunPropertiesHandler();
        event.addListener(handler);
    }

    @SubscribeEvent
    public static void onDataPackSync(OnDatapackSyncEvent event) {
        if (!loadedProperties.isEmpty()) {
            ServerPlayer player = event.getPlayer();
            if (player == null) {
                PacketHandler.simpleChannel.send(PacketDistributor.ALL.noArg(), new UpdateGunPropertiesPacket(cache, strCache));
            } else {
                PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> player), new UpdateGunPropertiesPacket(cache, strCache));
            }
        }
    }

    public static void syncFromServer(byte[] data, @Nullable String strData) {
        if (data != null) {
            try {
                String str = Utils.decompress(data);
                JsonObject object = IJsonSyncable.GSON.fromJson(str, JsonObject.class);
                handleSync(object);
                return;
            } catch (Exception exception) {exception.printStackTrace();}
        }
        if (strData != null) {
            JsonObject object = IJsonSyncable.GSON.fromJson(strData, JsonObject.class);
            handleSync(object);
        }
    }

    private static void handleSync(JsonObject jsonObject) {
        for (Gun gun : Gun.getAllInstances()) {
            String forgeKey = String.valueOf(ForgeRegistries.ITEMS.getKey(gun));
            JsonObject gunObject = jsonObject.getAsJsonObject(forgeKey);
            if (gunObject != null) {
                gun.getGunProperties().loadData(gunObject);
            }
        }
    }

    private void createCache() {
        JsonObject object = new JsonObject();
        for (Map.Entry<String, JsonObject> entry : loadedProperties.entrySet()) {
            object.add(entry.getKey(), entry.getValue());
        }
        try {
            cache = Utils.compress(object.toString());
            strCache = null;
        } catch (Exception e) {
            e.printStackTrace();
            cache = null;
            strCache = object.toString();
        }
    }

    @Override
    protected void apply(@NotNull Map<String, JsonObject> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        if (!pObject.isEmpty()) {
            for (Gun gun : Gun.getAllInstances()) {
                String forgeKey = String.valueOf(ForgeRegistries.ITEMS.getKey(gun));
                JsonObject jsonObject = pObject.get(forgeKey);
                if (jsonObject != null) {
                    gun.getGunProperties().loadData(jsonObject);
                }
            }
        }
        loadedProperties = pObject;
        createCache();
    }
}
