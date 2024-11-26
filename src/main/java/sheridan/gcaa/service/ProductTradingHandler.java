package sheridan.gcaa.service;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.capability.PlayerStatus;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.screens.containers.VendingMachineMenu;
import sheridan.gcaa.service.product.IProduct;
import sheridan.gcaa.service.product.IRecycleProduct;

import java.util.ArrayList;

public class ProductTradingHandler {

    public static long exchange(ServerPlayer player, long worth) {
        if (player != null) {
            PlayerStatus status = PlayerStatusProvider.getStatus(player);
            if (player.containerMenu instanceof VendingMachineMenu menu) {
                menu.exchange.setItem(0, ItemStack.EMPTY);
                status.serverSetBalance(status.getBalance() + worth);
                return status.getBalance();
            }
            return status.getBalance();
        }
        return 0;
    }

    public static long buy(ServerPlayer player, ItemStack itemStack, int productId) {
        if (player != null) {
            PlayerStatus status = PlayerStatusProvider.getStatus(player);
            IProduct product = ProductsRegister.getProductById(productId);
            if (product != null) {
                int price = product.getPrice(itemStack);
                if (status.getBalance() >= price) {
                    status.serverSetBalance(status.getBalance() - price);
                    if (!player.addItem(itemStack)) {
                        player.drop(itemStack, false);
                    }
                    return status.getBalance();
                }
            }
            return status.getBalance();
        }
        return 0;
    }

    public static long recycle(ServerPlayer player, int productId) {
        if (player != null) {
            PlayerStatus status = PlayerStatusProvider.getStatus(player);
            if (player.containerMenu instanceof VendingMachineMenu menu) {
                ItemStack itemStack = menu.recycleSlot.getItem();
                IProduct product = ProductsRegister.getProductById(productId);
                if (product instanceof IRecycleProduct recycleProduct) {
                    long recyclePrice = recycleProduct.getRecyclePrice(itemStack, new ArrayList<>());
                    long finalPrice = (long) (recyclePrice * IRecycleProduct.RECYCLE_PRICE_RATE);
                    status.serverSetBalance(status.getBalance() + finalPrice);
                    menu.recycleSlot.set(ItemStack.EMPTY);
                }
                return status.getBalance();
            }
            return status.getBalance();
        }
        return 0;
    }
}
