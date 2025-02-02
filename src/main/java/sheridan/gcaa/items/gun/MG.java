package sheridan.gcaa.items.gun;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.client.IReloadTask;
import sheridan.gcaa.client.UnloadTask;

public class MG extends Gun{
    public MG(GunProperties gunProperties) {
        super(gunProperties);
    }

    @Override
    public boolean isFreeBlot() {
        return true;
    }

    @Override
    public IReloadTask getUnloadingTask(ItemStack stack, Player player) {
        return new UnloadTask(this, stack, UnloadTask.MG);
    }

    @Override
    public int getCrosshairType() {
        return 2;
    }
}
