package sheridan.gcaa.industrial;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.ammunition.Ammunition;

import java.util.*;

public class RecipeRegister {
    public static final String AMMUNITION = "ammunition";
    private static final Map<String, Map<? extends Item, ? extends Recipe>> RECIPE_MAP = new HashMap<>();
    private static final Map<Ammunition, AmmunitionRecipe> AMMUNITION_RECIPE_MAP = new HashMap<>();
    private static final Gson GSON = new Gson();

    static {
        RECIPE_MAP.put(AMMUNITION, AMMUNITION_RECIPE_MAP);
    }

    public static void registerAmmo(Ammunition ammo, AmmunitionRecipe recipe) {
        AMMUNITION_RECIPE_MAP.put(ammo, recipe);
    }

    public static void registerAmmunition(List<Ammunition> ammoList, List<AmmunitionRecipe> recipes) {
        for (int i = 0; i < ammoList.size(); i++) {
            registerAmmo(ammoList.get(i), recipes.get(i));
        }
    }
    public static AmmunitionRecipe getRecipe(Ammunition ammo) {
        return AMMUNITION_RECIPE_MAP.get(ammo);
    }

    public static Map<Ammunition, AmmunitionRecipe> getAmmunitionRecipeMap() {
        return AMMUNITION_RECIPE_MAP;
    }

    public static void syncAmmunitionRecipeFromServer(String string) {
        JsonObject jsonObject = GSON.fromJson(string, JsonObject.class);
        Set<Ammunition> set = new HashSet<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.getKey()));
            if (item instanceof Ammunition ammunition) {
                set.add(ammunition);
                AmmunitionRecipe recipe = getRecipe(ammunition);
                if (recipe != null) {
                    recipe.loadData(entry.getValue().getAsJsonObject());
                } else {
                    AmmunitionRecipe ammunitionRecipe = new AmmunitionRecipe();
                    ammunitionRecipe.loadData(entry.getValue().getAsJsonObject());
                    ammunitionRecipe.ammunition = ammunition;
                    registerAmmo(ammunition, ammunitionRecipe);
                }
            }
        }
        for (Ammunition ammunition : AMMUNITION_RECIPE_MAP.keySet()) {
            // 客户端删除不存在的配方, 以服务端的配方为准
            if (!set.contains(ammunition)) {
                AMMUNITION_RECIPE_MAP.remove(ammunition);
            }
        }
    }
}
