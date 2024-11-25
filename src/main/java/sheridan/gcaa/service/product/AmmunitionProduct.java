package sheridan.gcaa.service.product;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.IAmmunition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmmunitionProduct extends CommonProduct implements IRecycleProduct{
    private static final Map<Ammunition, AmmunitionProduct> AMMUNITION_PRODUCT_MAP = new HashMap<>();
    public IAmmunition ammunition;

    public AmmunitionProduct(Ammunition ammunition, int price) {
        super(ammunition, price);
        this.ammunition =  ammunition;
        AMMUNITION_PRODUCT_MAP.put(ammunition, this);
    }

    @Override
    public void onRemoveRegistry() {
        AMMUNITION_PRODUCT_MAP.remove(ammunition.get());
    }

    public static AmmunitionProduct get(Ammunition ammunition) {
        return AMMUNITION_PRODUCT_MAP.get(ammunition);
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

    @Override
    public int getPrice(ItemStack itemStack) {
        double singlePrice = getDefaultPrice() / (double) getMaxBuyCount();
        int ammoLeft = ammunition.getAmmoLeft(itemStack);
        return (int) (ammoLeft * singlePrice);
    }

    @Override
    public int getMinBuyCount() {
        return Math.min((int) Math.ceil(getMaxBuyCount() / (double) getDefaultPrice()), getMaxBuyCount());
    }

    @Override
    public long getRecyclePrice(ItemStack itemStack, List<Component> tooltip) {

        return getPrice(itemStack);
    }
}
