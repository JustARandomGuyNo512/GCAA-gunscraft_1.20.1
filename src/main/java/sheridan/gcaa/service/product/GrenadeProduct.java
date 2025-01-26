package sheridan.gcaa.service.product;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.items.gun.IGun;

import java.util.List;

public class GrenadeProduct extends AmmunitionProduct{
    public GrenadeProduct(Ammunition ammunition, int price) {
        super(ammunition, price);
    }

    @Override
    public long getRecyclePrice(ItemStack itemStack, List<Component> tooltip) {
        if (itemStack.getItem() instanceof IGun gun && GrenadeLauncher.hasGrenade(itemStack, gun)) {
            String string = Component.translatable(ammunition.get().getDescriptionId()).getString() + " x 1";
            int singlePrice = (int) ((1f / ammunition.get().getMaxDamage()) * getDefaultPrice());
            tooltip.add(Component.literal(string + " = " + singlePrice));
            return singlePrice;
        }
        return super.getRecyclePrice(itemStack, tooltip);
    }
}
