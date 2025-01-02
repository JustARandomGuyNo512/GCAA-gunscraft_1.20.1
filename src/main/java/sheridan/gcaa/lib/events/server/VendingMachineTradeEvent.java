package sheridan.gcaa.lib.events.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.service.product.IProduct;

public class VendingMachineTradeEvent extends net.minecraftforge.eventbus.api.Event {
    private final ServerPlayer player;
    private final IProduct product;
    private final int price;
    private final ItemStack itemStack;
    private final boolean success;

    public VendingMachineTradeEvent(ServerPlayer player, IProduct product, int price, ItemStack itemStack, boolean success) {
        this.player = player;
        this.product = product;
        this.price = price;
        this.itemStack = itemStack;
        this.success = success;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public IProduct getProduct() {
        return product;
    }

    public int getPrice() {
        return price;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isSuccess() {
        return success;
    }
}
