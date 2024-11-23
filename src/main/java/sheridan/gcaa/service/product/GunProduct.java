package sheridan.gcaa.service.product;

import net.minecraft.world.item.Item;

public class GunProduct extends CommonProduct{
    public GunProduct(Item item, int price) {
        super(item, price);
    }

    @Override
    public int getMaxBuyCount() {
        return 1;
    }


}
