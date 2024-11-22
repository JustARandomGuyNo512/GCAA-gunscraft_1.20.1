package sheridan.gcaa.service.product;

import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.IAmmunition;

public class AmmunitionProduct extends CommonProduct{
    public IAmmunition ammunition;

    public AmmunitionProduct(Ammunition ammunition, int price) {
        super(ammunition, price);
        this.ammunition =  ammunition;
    }

    @Override
    public int getMaxBuyCount() {
        return ammunition.get().getMaxDamage();
    }

    @Override
    public ItemStack getItemStack(int count) {
        ItemStack stack = new ItemStack(ammunition.get());
        ammunition.get().checkAndGet(stack);
        ammunition.setAmmoLeft(stack, count);
        return stack;
    }

    @Override
    public ItemStack getDisplayItem() {
        return getItemStack(getMaxBuyCount());
    }
}
