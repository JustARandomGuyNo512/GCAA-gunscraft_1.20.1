package sheridan.gcaa.data.vendingMachineProducts;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.data.IDataPacketGen;
import sheridan.gcaa.data.Utils;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.UpdateVendingMachineProductsPacket;
import sheridan.gcaa.service.ProductsRegister;
import sheridan.gcaa.service.product.IProduct;
import sun.misc.Unsafe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Mod.EventBusSubscriber(modid = GCAA.MODID)
public class VendingMachineProductsHandler extends SimplePreparableReloadListener<Map<String, List<JsonObject>>> {
    private static String strCache;
    private static byte[] cache;

    @Override
    protected @NotNull Map<String,  List<JsonObject>> prepare(@NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        Map<String,  List<JsonObject>> map = new HashMap<>();
        ResourceLocation location = new ResourceLocation(GCAA.MODID, "vending_machine_products/vending_machine_products_register.json");
        pResourceManager.getResource(location).ifPresent(res -> {
            try {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(res.open(), StandardCharsets.UTF_8))) {
                    JsonObject jsonObject = GsonHelper.fromJson(IDataPacketGen.GSON, reader, JsonObject.class);
                    Set<String> allCategories = ProductsRegister.getAllCategories();
                    for (String category : allCategories) {
                        if (jsonObject.has(category)) {
                            JsonArray array = jsonObject.getAsJsonArray(category);
                            List<JsonObject> products = new ArrayList<>(100);
                            for (JsonElement element : array) {
                                if (element.isJsonObject()) {
                                    JsonObject asJsonObject = element.getAsJsonObject();
                                    products.add(asJsonObject);
                                }
                            }
                            map.put(category, products);
                        } else {
                            map.put(category, new ArrayList<>());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return map;
    }

    private static IProduct getProduct(JsonObject asJsonObject) {
        try {
            String className = asJsonObject.get("class").getAsString();
            if (className == null || className.isEmpty()) {
                throw new IllegalArgumentException("Class name is null or empty");
            }
            Class<?> clazz = Class.forName(className);
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            Object instance = unsafe.allocateInstance(clazz);
            if (instance instanceof IProduct product) {
                product.loadData(asJsonObject);
                return product.getItem() == null ? null : product;
            } else {
                throw new ClassCastException("Instance is not of type IProduct: " + instance.getClass().getName());
            }
        } catch (Exception e) {
            System.err.println("Error creating product from JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @SubscribeEvent
    public static void onDataPackSync(OnDatapackSyncEvent event) {
        ServerPlayer player = event.getPlayer();
        if (player == null) {
            PacketHandler.simpleChannel.send(PacketDistributor.ALL.noArg(), new UpdateVendingMachineProductsPacket(strCache, cache));
        } else {
            PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> player), new UpdateVendingMachineProductsPacket(strCache, cache));
        }
    }

    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent event) {
        VendingMachineProductsHandler handler = new VendingMachineProductsHandler();
        event.addListener(handler);
    }

    @Override
    protected void apply(@NotNull Map<String,  List<JsonObject>> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        Set<String> allCategories = ProductsRegister.getAllCategories();
        ProductsRegister.clear();
        for (String category : allCategories) {
            if (pObject.containsKey(category)) {
                List<JsonObject> list = pObject.getOrDefault(category, new ArrayList<>());
                for (JsonObject object : list) {
                    IProduct product = getProduct(object);
                    if (product != null) {
                        ProductsRegister.registerProduct(category, product);
                    }
                }
            }
        }
        genCache();
    }

    public static void syncFromServer(String strData, byte[] byteData) {
        String data = strData;
        if (byteData != null) {
            try {
                data = Utils.decompress(byteData);
            } catch (Exception e) {
                data = strData;
                e.printStackTrace();
            }
        }
        if (data == null) {
            return;
        }
        ProductsRegister.clear();
        String[] split = data.split("`");
        List<IndexedProductRegistry> registryList = new ArrayList<>();
        for (String string : split) {
            IndexedProductRegistry registry = IndexedProductRegistry.fromString(string);
            registryList.add(registry);
        }
        registryList.sort(Comparator.comparingInt(IndexedProductRegistry::id));
        for (IndexedProductRegistry registry : registryList) {
            IProduct product = registry.getProduct();
            if (product != null) {
                ProductsRegister.registerProduct(registry.category, registry.getProduct());
            }
        }
    }

    private void genCache() {
        StringBuilder builder = new StringBuilder();
        for (String category : ProductsRegister.getAllCategories()) {
            Set<IProduct> products = ProductsRegister.getProducts(category);
            for (IProduct product : products) {
                int id = ProductsRegister.getId(product);
                JsonObject object = new JsonObject();
                product.writeData(object);
                IndexedProductRegistry indexedProductRegistry = new IndexedProductRegistry(id, category, object);
                String string = indexedProductRegistry.toString();
                builder.append(string).append("`");
            }
        }
        String string = builder.toString();
        try {
            cache = Utils.compress(string);
            strCache = null;
        } catch (Exception e) {
            cache = null;
            strCache = string;
        }
    }

    private record IndexedProductRegistry(int id, String category, JsonObject dataObject) {
        public String toString() {
            JsonObject object = new JsonObject();
            object.addProperty("id", id);
            object.addProperty("category", category);
            object.add("data", dataObject);
            return object.toString();
        }

        public static IndexedProductRegistry fromString(String string) {
            JsonObject object = IDataPacketGen.GSON.fromJson(string, JsonObject.class);
            return new IndexedProductRegistry(
                    object.get("id").getAsInt(),
                    object.get("category").getAsString(),
                    object.get("data").getAsJsonObject());
        }

        public IProduct getProduct() {
            return VendingMachineProductsHandler.getProduct(dataObject);
        }

    }
}
