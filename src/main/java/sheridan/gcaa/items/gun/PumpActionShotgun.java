package sheridan.gcaa.items.gun;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.client.HandActionHandler;
import sheridan.gcaa.client.IReloadingTask;
import sheridan.gcaa.client.SingleReloadTask;
import sheridan.gcaa.items.gun.propertyExtensions.HandActionExtension;
import sheridan.gcaa.items.gun.propertyExtensions.SingleReloadExtension;

public class PumpActionShotgun extends HandActionGun{
    protected final SingleReloadExtension singleReloadExtension;

    public PumpActionShotgun(GunProperties gunProperties, HandActionExtension handActionExtension, SingleReloadExtension singleReloadExtension) {
        super(gunProperties.addExtension(singleReloadExtension), handActionExtension);
        this.singleReloadExtension = singleReloadExtension;
    }

    @Override
    public void reload(ItemStack stack, Player player) {
        int num = singleReloadExtension.singleReloadNum;
        if (num > 0) {
            setAmmoLeft(stack, Math.min(getAmmoLeft(stack) + num, getMagSize(stack)));
        }
    }

    @Override
    public boolean shouldHandleAds(ItemStack stack) {
        if (needHandAction(stack) && getAmmoLeft(stack) > 0 && HandActionHandler.INSTANCE.secondsSinceLastTask() > 0.5f) {
            HandActionHandler.INSTANCE.setHandActionTask(getHandActionTask(stack, true));
        }
        return true;
    }

    @Override
    public int getCrosshairType() {
        return 1;
    }

    @Override
    public IReloadingTask getReloadingTask(ItemStack stack) {
        return new SingleReloadTask(stack, this,
                singleReloadExtension.enterDelay,
                singleReloadExtension.singleReloadLength,
                singleReloadExtension.exitDelay,
                (getMagSize(stack) - getAmmoLeft(stack)),
                singleReloadExtension.triggerReloadDelay);
    }

    @Override
    public boolean allowShootWhileReloading() {
        return true;
    }
}
