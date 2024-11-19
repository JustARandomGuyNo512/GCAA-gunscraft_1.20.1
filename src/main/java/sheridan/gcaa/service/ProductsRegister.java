package sheridan.gcaa.service;

import sheridan.gcaa.service.product.IProduct;

import java.util.*;

public class ProductsRegister {
    public static final String EXCHANGE = "exchange";
    public static final String WEAPON = "weapon";
    public static final String AMMUNITION = "ammunition";
    public static final String ATTACHMENT = "attachment";
    public static final String DEVICE = "device";

    private static final Map<String, Set<IProduct>> PRODUCTS = new HashMap<>();

    static {
        PRODUCTS.put(EXCHANGE, new HashSet<>());
        PRODUCTS.put(WEAPON, new HashSet<>());
        PRODUCTS.put(AMMUNITION, new HashSet<>());
        PRODUCTS.put(ATTACHMENT, new HashSet<>());
        PRODUCTS.put(DEVICE, new HashSet<>());
    }

    public static Set<IProduct> getProducts(String type) {
        return PRODUCTS.getOrDefault(type, new HashSet<>());
    }

    public static void registerProduct(String category, IProduct product) {
        PRODUCTS.get(category).add(product);
    }

    public static void registerProducts(String category, IProduct... products) {
        for (IProduct product : products) {
            registerProduct(category, product);
        }
    }
}
