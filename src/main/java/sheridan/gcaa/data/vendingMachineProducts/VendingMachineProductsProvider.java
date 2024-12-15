package sheridan.gcaa.data.vendingMachineProducts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.Commons;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.service.ProductsRegister;
import sheridan.gcaa.service.product.IProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class VendingMachineProductsProvider implements DataProvider {
    protected final PackOutput.PathProvider pathProvider;
    public VendingMachineProductsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "vending_machine_products");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        List<CompletableFuture<?>> list = new ArrayList<>();
        Commons.registerVendingMachineProducts();
        Set<String> categories = ProductsRegister.getAllCategories();
        ResourceLocation key = new ResourceLocation(GCAA.MODID, "vending_machine_products_register");
        JsonObject object = new JsonObject();
        for (String category : categories) {
            JsonArray array = new JsonArray();
            Set<IProduct> products = ProductsRegister.getProducts(category);
            for (IProduct product : products) {
                JsonObject productObject = new JsonObject();
                product.writeData(productObject);
                array.add(productObject);
            }
            object.add(category, array);
        }
        list.add(DataProvider.saveStable(pOutput, object, this.pathProvider.json(key)));
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return GCAA.MODID + ": vending_machine_products";
    }
}
