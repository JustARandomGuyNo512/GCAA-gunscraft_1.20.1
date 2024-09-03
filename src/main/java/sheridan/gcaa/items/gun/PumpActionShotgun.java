package sheridan.gcaa.items.gun;

import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.client.IReloadingTask;
import sheridan.gcaa.items.gun.propertyExtensions.HandActionExtension;
import sheridan.gcaa.items.gun.propertyExtensions.SingleReloadExtension;

public class PumpActionShotgun extends HandActionGun{
    protected final SingleReloadExtension singleReloadExtension;

    public PumpActionShotgun(GunProperties gunProperties, HandActionExtension handActionExtension, SingleReloadExtension singleReloadExtension) {
        super(gunProperties.addExtension(singleReloadExtension), handActionExtension);
        this.singleReloadExtension = singleReloadExtension;
    }

    @Override
    public boolean shouldHandleAds(ItemStack stack) {
        return true;
    }

    @Override
    public IReloadingTask getReloadingTask(ItemStack stack) {
        return super.getReloadingTask(stack);
    }
}
