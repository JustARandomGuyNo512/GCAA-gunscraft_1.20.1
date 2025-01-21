package sheridan.gcaa.items.gun;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.client.IReloadTask;
import sheridan.gcaa.client.UnloadTask;
import sheridan.gcaa.items.gun.propertyExtensions.HandActionExtension;

public class Sniper extends HandActionGun{

    public Sniper(GunProperties gunProperties, HandActionExtension extension) {
        super(gunProperties, extension);
    }

    @Override
    public IReloadTask getUnloadingTask(ItemStack stack, Player player) {
        return new UnloadTask(this, stack, UnloadTask.SNIPER);
    }

    @Override
    public boolean isSniper() {
        return true;
    }

    @Override
    public int getCrosshairType() {
        return -1;
    }

    @Override
    public boolean shootCreateBulletShell() {
        return false;
    }

    @Override
    public GunType getGunType() {
        return GunType.SNIPER;
    }
}
