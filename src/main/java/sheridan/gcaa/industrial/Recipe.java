package sheridan.gcaa.industrial;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import sheridan.gcaa.data.IJsonSyncable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Recipe implements IJsonSyncable {
    public Item item;
    public Map<Item, Integer> ingredients;
    public int craftingTicks;

    public Recipe() {
        ingredients = new HashMap<>();
    }

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
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null) return;
        jsonObject.addProperty("item", key.toString());
        JsonObject newJsonObject = new JsonObject();
        for (Map.Entry<Item, Integer> entry : ingredients.entrySet()) {
            ResourceLocation itemsKey = ForgeRegistries.ITEMS.getKey(entry.getKey());
            if (itemsKey == null) continue;
            newJsonObject.addProperty(itemsKey.toString(), entry.getValue());
        }
        jsonObject.add("ingredients", newJsonObject);
        jsonObject.addProperty("craftingTicks", craftingTicks);
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        craftingTicks = jsonObject.get("craftingTicks").getAsInt();
        item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(jsonObject.get("item").getAsString()));
        JsonObject ingredientsJson = jsonObject.get("ingredients").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = ingredientsJson.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.getKey()));
            if (item == null) continue;
            ingredients.put(item, entry.getValue().getAsInt());
        }
    }
}
