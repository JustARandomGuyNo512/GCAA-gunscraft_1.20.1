package sheridan.gcaa.service.product;

import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CommonProduct implements IProduct{
    public final Item item;
    public int price;

    public CommonProduct(Item item, int price) {
        this.item = item;
        this.price = Math.max(price, 1);
    }

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

    }

    @Override
    public void loadData(JsonObject jsonObject) {

    }
}
