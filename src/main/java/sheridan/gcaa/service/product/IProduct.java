package sheridan.gcaa.service.product;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IProduct {
    int getPrice(ItemStack itemStack);
    int getDefaultPrice();
    Item getItem();
    ItemStack getItemStack(int count);
    ItemStack getDisplayItem();
    int getMaxBuyCount();

    static IProduct of(Item item) {
        return new CommonProduct(item, 0);
    }

    default void writeToNBT(ItemStack itemStack) {}
    default void syncFormNBT(ItemStack itemStack) {}
}
