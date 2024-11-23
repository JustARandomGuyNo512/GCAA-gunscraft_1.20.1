package sheridan.gcaa.service;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import sheridan.gcaa.service.product.IProduct;

import java.util.*;

public class ProductsRegister {
    public static final String EXCHANGE = "exchange";
    public static final String GUN = "gun";
    public static final String AMMUNITION = "ammunition";
    public static final String ATTACHMENT = "attachment";
    public static final String OTHER = "other";

    private static final Map<String, Set<IProduct>> PRODUCTS = new HashMap<>();
    private static final Map<String, Item> ICON_MAP = new HashMap<>();

    private static int nextId = 0;

    private static final Map<Integer, IProduct> ID_TO_PRODUCT = new HashMap<>();
    private static final Map<IProduct, Integer> PRODUCT_TO_ID = new HashMap<>();

    static {
        PRODUCTS.put(EXCHANGE, new LinkedHashSet<>());
        PRODUCTS.put(GUN, new LinkedHashSet<>());
        PRODUCTS.put(AMMUNITION, new LinkedHashSet<>());
        PRODUCTS.put(ATTACHMENT, new LinkedHashSet<>());
        PRODUCTS.put(OTHER, new LinkedHashSet<>());
    }

    public static Set<IProduct> getProducts(String type) {
        return PRODUCTS.getOrDefault(type, new LinkedHashSet<>());
    }


    public static void registerProduct(String category, IProduct product) {
        if (PRODUCTS.get(category).add(product)) {
            ID_TO_PRODUCT.put(nextId, product);
            PRODUCT_TO_ID.put(product, nextId);
            nextId++;
        }
        if (!ICON_MAP.containsKey(category)) {
            ICON_MAP.put(category, product.getItem());
        }
    }

    public static void registerProducts(String category, IProduct... products) {
        for (IProduct product : products) {
            registerProduct(category, product);
        }
    }

    public static Item getIcon(String category) {
        return ICON_MAP.getOrDefault(category, Items.AIR);
    }

    public static IProduct getProductById(int id) {
        return ID_TO_PRODUCT.get(id);
    }

    public static int getId(IProduct product) {
        return PRODUCT_TO_ID.get(product);
    }

    public static void syncFrom() {

    }

    public static void writeForSync() {

    }
}
