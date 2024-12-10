package sheridan.gcaa.industrial;

import sheridan.gcaa.items.ammunition.Ammunition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeRegister {
    private static final Map<Ammunition, Recipe> AMMUNITION_RECIPE_MAP = new HashMap<>();

    public static void registerAmmo(Ammunition ammo, Recipe recipe) {
        AMMUNITION_RECIPE_MAP.put(ammo, recipe);
    }

    public static void registerAmmunition(List<Ammunition> ammoList, List<Recipe> recipes) {
        for (int i = 0; i < ammoList.size(); i++) {
            registerAmmo(ammoList.get(i), recipes.get(i));
        }
    }
    public static Recipe getRecipe(Ammunition ammo) {
        return AMMUNITION_RECIPE_MAP.get(ammo);
    }

}
