package sheridan.gcaa.industrial;

import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.data.IDataPacketGen;
import sheridan.gcaa.items.ammunition.Ammunition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AmmunitionRecipe implements IDataPacketGen {
    public final Ammunition ammunition;
    public final Map<Item, Integer> ingredients;
    public final int craftingTicks;

    public AmmunitionRecipe(Ammunition ammunition, int ms) {
        this.ammunition = ammunition;
        this.ingredients = new HashMap<>();
        this.craftingTicks = Math.max((ms / 50), 1);
    }

    public ItemStack getResult() {
        ItemStack stack = new ItemStack(ammunition);
        ammunition.checkAndGet(stack);
        return stack;
    }

    public Map<Item, Integer> getIngredients() {
        return ingredients;
    }

    public AmmunitionRecipe addIngredients(Set<Item> listItem, List<Integer> listAmount) {
        int i = 0;
        for (Item item: listItem) {
           int amount = i < listAmount.size() ? listAmount.get(i) : 1;
           ingredients.put(item, amount);
           if (i == 16) {
               break;
           }
           i++;
        }
        return this;
    }

    @Override
    public void writeData(JsonObject jsonObject) {

    }

    @Override
    public void loadData(JsonObject jsonObject) {

    }
}
