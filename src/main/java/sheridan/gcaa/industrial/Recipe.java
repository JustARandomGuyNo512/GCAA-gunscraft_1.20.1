package sheridan.gcaa.industrial;

import net.minecraft.world.item.Item;
import sheridan.gcaa.items.ammunition.Ammunition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Recipe {
    public final Ammunition ammunition;
    public final Map<Item, Integer> ingredients;

    public Recipe(Ammunition ammunition) {
        this.ammunition = ammunition;
        this.ingredients = new HashMap<>();
    }

    public Map<Item, Integer> getIngredients() {
        return ingredients;
    }

    public Recipe addIngredients(Set<Item> listItem, List<Integer> listAmount) {
        int i = 0;
        for (Item item: listItem) {
           int amount = i < listAmount.size() ? listAmount.get(i) : 1;
           ingredients.put(item, amount);
           i++;
        }
        return this;
    }
}
