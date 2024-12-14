package sheridan.gcaa.service.product;

import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.data.IDataPacketGen;

public interface IProduct extends IDataPacketGen {
    int getPrice(ItemStack itemStack);
    int getDefaultPrice();
    Item getItem();
    ItemStack getItemStack(int count);
    ItemStack getDisplayItem();
    int getMaxBuyCount();
    int getMinBuyCount();

    static IProduct of(Item item) {
        return new CommonProduct(item, 1);
    }

    default void writeToNBT(ItemStack itemStack) {}
    default void syncFormNBT(ItemStack itemStack) {}
    default void onRemoveRegistry() {}

}
