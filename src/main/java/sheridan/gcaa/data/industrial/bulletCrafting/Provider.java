package sheridan.gcaa.data.industrial.bulletCrafting;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.Commons;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.industrial.AmmunitionRecipe;
import sheridan.gcaa.industrial.RecipeRegister;
import sheridan.gcaa.items.ammunition.Ammunition;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Provider implements DataProvider {
    protected final PackOutput.PathProvider pathProvider;

    public Provider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "bullet_crafting_recipe");
    }
    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        List<CompletableFuture<?>> list = new ArrayList<>();
        Commons.registerAmmunitionRecipes();
        Map<Ammunition, AmmunitionRecipe> ammunitionRecipeMap = RecipeRegister.getAmmunitionRecipeMap();
        for (Map.Entry<Ammunition, AmmunitionRecipe> entry : ammunitionRecipeMap.entrySet()) {
            AmmunitionRecipe recipe = entry.getValue();
            JsonObject jsonObject = new JsonObject();
            recipe.writeData(jsonObject);
            String name = entry.getKey().getDescriptionId().split("\\.")[2];
            ResourceLocation key = new ResourceLocation(GCAA.MODID, name);
            Path path = this.pathProvider.json(key);
            list.add(DataProvider.saveStable(cachedOutput, jsonObject, path));
        }
        return CompletableFuture. allOf(list.toArray(CompletableFuture[]::new));
    }

    public @NotNull String getName() {
        return GCAA.MODID + ": bullet_crafting_recipe";
    }

}
