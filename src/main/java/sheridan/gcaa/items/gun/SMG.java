package sheridan.gcaa.items.gun;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.client.IReloadTask;
import sheridan.gcaa.client.UnloadTask;

public class SMG extends Gun{
    public SMG(GunProperties gunProperties) {
        super(gunProperties);
    }

    @Override
    public IReloadTask getUnloadingTask(ItemStack stack, Player player) {
        return new UnloadTask(this, stack, UnloadTask.SMG);
    }

    @Override
    public GunType getGunType() {
        return GunType.SMG;
    }
}
