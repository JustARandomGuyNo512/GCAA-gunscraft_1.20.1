package sheridan.gcaa.items.gun;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.client.IReloadTask;
import sheridan.gcaa.client.UnloadTask;

public class Pistol extends Gun{
    public Pistol(GunProperties gunProperties) {
        super(gunProperties);
    }

    @Override
    public boolean isPistol() {
        return true;
    }

    @Override
    public boolean canUseWithShield() {
        return true;
    }

    @Override
    public IReloadTask getUnloadingTask(ItemStack stack, Player player) {
        return new UnloadTask(this, stack, UnloadTask.PISTOL);
    }

    @Override
    public GunType getGunType() {
        return GunType.PISTOL;
    }
}
