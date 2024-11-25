package sheridan.gcaa.service.product;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.items.gun.IGun;

import java.util.List;

public class GrenadeLauncherProduct extends AttachmentProduct{
    private final GrenadeLauncher grenadeLauncher;
    public GrenadeLauncherProduct(GrenadeLauncher grenadeLauncher, int price) {
        super(grenadeLauncher, price);
        this.grenadeLauncher = grenadeLauncher;
    }

    @Override
    public long getRecyclePrice(ItemStack itemStack, List<Component> tooltip) {
        long recyclePrice = super.getRecyclePrice(itemStack, tooltip);
        if (itemStack.getItem() instanceof IGun gun && GrenadeLauncher.hasGrenade(itemStack, gun)) {
            IAmmunition ammunition = grenadeLauncher.ammunition;
            AmmunitionProduct ammunitionProduct = AmmunitionProduct.get(ammunition.get());
            if (ammunitionProduct != null) {

            }
        }
        return recyclePrice;
    }
}
