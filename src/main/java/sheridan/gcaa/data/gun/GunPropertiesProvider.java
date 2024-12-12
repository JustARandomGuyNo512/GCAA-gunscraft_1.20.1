package sheridan.gcaa.data.gun;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GunPropertiesProvider implements DataProvider {
    private final CompletableFuture<HolderLookup.Provider> registries;
    protected final PackOutput.PathProvider pathProvider;
    public GunPropertiesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "guns_properties");
        this.registries = registries;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        List<CompletableFuture<?>> list = new ArrayList<>();
        String str = "abcdefghijklmn";
        for (int i = 0 ; i < 10; i++) {
            ResourceLocation key = new ResourceLocation(GCAA.MODID, "test_" + str.charAt(i));
            Path path = this.pathProvider.json(key);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("test", i);
            list.add(DataProvider.saveStable(pOutput, jsonObject, path));
        }
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return GCAA.MODID + ": guns_properties";
    }
}
