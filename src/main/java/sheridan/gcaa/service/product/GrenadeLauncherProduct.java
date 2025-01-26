package sheridan.gcaa.service.product;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;

import java.util.List;

public class GrenadeLauncherProduct extends AttachmentProduct{
    private GrenadeLauncher grenadeLauncher;
    public GrenadeLauncherProduct(GrenadeLauncher grenadeLauncher, int price) {
        super(grenadeLauncher, price);
        this.grenadeLauncher = grenadeLauncher;
    }

    @Override
    public long getRecyclePrice(ItemStack itemStack, List<Component> tooltip) {
        long recyclePrice = super.getRecyclePrice(itemStack, tooltip);
        IAmmunition ammunition = grenadeLauncher.ammunition;
        AmmunitionProduct ammunitionProduct = AmmunitionProduct.get(ammunition.get());
        if (ammunitionProduct != null) {
            recyclePrice += ammunitionProduct.getRecyclePrice(itemStack, tooltip);
        }
        return recyclePrice;
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        super.loadData(jsonObject);
        if (this.attachment instanceof GrenadeLauncher grenadeLauncher) {
            this.grenadeLauncher = grenadeLauncher;
        }
    }
}
