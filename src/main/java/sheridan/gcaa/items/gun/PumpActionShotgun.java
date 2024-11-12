package sheridan.gcaa.items.gun;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.client.HandActionHandler;
import sheridan.gcaa.client.IReloadTask;
import sheridan.gcaa.client.SingleReloadTask;
import sheridan.gcaa.items.ammunition.AmmunitionHandler;
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
            AmmunitionHandler.reloadFor(player, stack, this, num);
        }
    }

    @Override
    public int getCrosshairType() {
        return 1;
    }

    @Override
    public IReloadTask getReloadingTask(ItemStack stack, Player player) {
        return new SingleReloadTask(stack, this,
                singleReloadExtension.enterDelay,
                singleReloadExtension.singleReloadLength,
                singleReloadExtension.exitDelay,
                Math.min((getMagSize(stack) - getAmmoLeft(stack)), AmmunitionHandler.getAmmunitionCount(stack, this, player)),
                singleReloadExtension.triggerReloadDelay);
    }

    @Override
    public boolean allowShootWhileReloading() {
        return true;
    }

    @Override
    public boolean canUnload() {
        return false;
    }
}
