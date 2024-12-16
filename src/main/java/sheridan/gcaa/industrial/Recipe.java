package sheridan.gcaa.industrial;

import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.data.IDataPacketGen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Recipe implements IDataPacketGen {
    public final Item item;
    public final Map<Item, Integer> ingredients;
    public final int craftingTicks;

    public Recipe(Item item, int ms) {
        this.item = item;
        this.ingredients = new HashMap<>();
        this.craftingTicks = Math.max((ms / 50), 1);
    }

    public ItemStack getResult() {
        return new ItemStack(item);
    }

    public Map<Item, Integer> getIngredients() {
        return ingredients;
    }

    @Override
    public void writeData(JsonObject jsonObject) {
    }

    @Override
    public void loadData(JsonObject jsonObject) {
    }
}
