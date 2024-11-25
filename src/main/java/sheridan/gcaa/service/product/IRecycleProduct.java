package sheridan.gcaa.service.product;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IRecycleProduct {
    float RECYCLE_PRICE_RATE = 0.5f;
    long getRecyclePrice(ItemStack itemStack,  List<Component> tooltip);
}
