package sheridan.gcaa.items.gun;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.client.*;
import sheridan.gcaa.items.ammunition.AmmunitionHandler;
import sheridan.gcaa.items.gun.propertyExtensions.AutoShotgunExtension;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.ClearGunAmmoPacket;

public class AutoShotgun extends HandActionGun {
    protected final AutoShotgunExtension autoShotgunExtension;

    public AutoShotgun(GunProperties gunProperties, AutoShotgunExtension autoShotgunExtension) {
        super(gunProperties, null);
        gunProperties.addExtension(autoShotgunExtension);
        this.autoShotgunExtension = autoShotgunExtension;
    }

    @Override
    public void reload(ItemStack stack, Player player) {
        int num = autoShotgunExtension.singleReloadNum;
        if (num > 0) {
            AmmunitionHandler.reloadFor(player, stack, this, num);
        }
    }

    @Override
    public IReloadTask getReloadingTask(ItemStack stack, Player player) {
        return new AutoShotgunReloadTask(stack, this,  Math.min((getMagSize(stack) - getAmmoLeft(stack)), AmmunitionHandler.getAmmunitionCount(stack, this, player)), autoShotgunExtension);
    }

    @Override
    public void clientShoot(ItemStack stack, Player player, IGunFireMode fireMode) {
        super.clientShoot(stack, player, fireMode);
        ReloadingHandler.INSTANCE.breakTask();
    }

    @Override
    public boolean needHandAction(ItemStack itemStack) {
        return getAmmoLeft(itemStack) <= 0;
    }

    @Override
    public void setNeedHandAction(ItemStack itemStack, boolean val) {}

    @Override
    protected void afterClientShoot(ItemStack stack, Player player, IGunFireMode fireMode) {}

    @Override
    protected void afterShoot(ItemStack stack, Player player, IGunFireMode fireMode, float spread) {}

    @Override
    public IHandActionTask getHandActionTask(ItemStack stack, boolean immediate) {
        return null;
    }

    @Override
    public boolean allowShootWhileReloading() {
        return true;
    }

    @Override
    public boolean shouldHandleAds(ItemStack stack) {
        return true;
    }

    @Override
    public int getCrosshairType() {
        return 1;
    }

    @Override
    public boolean clientReload(ItemStack stack, Player player) {
        if (isNotUsingSelectedAmmo(stack)) {
            PacketHandler.simpleChannel.sendToServer(new ClearGunAmmoPacket());
            clearAmmo(stack, player);
        }
        return super.clientReload(stack, player);
    }

    @Override
    public IReloadTask getUnloadingTask(ItemStack stack, Player player) {
        return new UnloadTask(this, stack, UnloadTask.SHOTGUN);
    }

    @Override
    public GunType getGunType() {
        return GunType.SHOTGUN;
    }
}
