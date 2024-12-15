package sheridan.gcaa.service.product;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class CommonProduct implements IProduct {
    public Item item;
    public int price;

    public CommonProduct(Item item, int price) {
        this.item = item;
        this.price = Math.max(price, 1);
    }

    protected CommonProduct() {}

    @Override
    public int getPrice(ItemStack itemStack) {
        return itemStack.getCount() * price;
    }

    @Override
    public int getDefaultPrice() {
        return price;
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public ItemStack getItemStack(int count) {
        return new ItemStack(item, count);
    }

    @Override
    public ItemStack getDisplayItem() {
        return getItemStack(1);
    }

    @Override
    public int getMaxBuyCount() {
        return item.getMaxStackSize();
    }

    @Override
    public int getMinBuyCount() {
        return 1;
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IProduct product) {
            return product.getItem() == item;
        }
        return false;
    }

    @Override
    public void writeData(JsonObject jsonObject) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key != null) {
            jsonObject.addProperty("class", this.getClass().getName());
            jsonObject.addProperty("item", key.toString());
            jsonObject.addProperty("price", price);
        }
    }

    /**
     * @Description Set item to null if not found
     * */
    @Override
    public void loadData(JsonObject jsonObject) {
        if (jsonObject.has("item") && jsonObject.has("price")) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(jsonObject.get("item").getAsString()));
            if (item != null) {
                this.item = item;
                int price = jsonObject.get("price").getAsInt();
                price = Mth.clamp(price, 1, Integer.MAX_VALUE);
                this.price = price;
            }
        }
    }


}
