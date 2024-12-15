package sheridan.gcaa.industrial;

import net.minecraft.world.item.Item;
import sheridan.gcaa.items.ammunition.Ammunition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeRegister {
    public static final String AMMUNITION = "ammunition";
    private static final Map<String, Map<? extends Item, ? extends Recipe>> RECIPE_MAP = new HashMap<>();
    private static final Map<Ammunition, AmmunitionRecipe> AMMUNITION_RECIPE_MAP = new HashMap<>();

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

}
