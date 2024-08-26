package sheridan.gcaa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.gun.HandActionGun;

@OnlyIn(Dist.CLIENT)
public class HandActionHandler {
    public static final HandActionHandler INSTANCE = new HandActionHandler();
    private IHandActionTask handActionTask;
    public static long lastEndTask = 0;
    public static long lastStartTask = 0;

    public void tick(Player clientPlayer) {
        if (handActionTask != null && !Minecraft.getInstance().isPaused() && !clientPlayer.isSpectator()) {
            if (handActionTask != null) {
                boolean shouldCancel = !ItemStack.isSameItem(clientPlayer.getMainHandItem(), handActionTask.getItemStack());
                if (shouldCancel) {
                    breakTask();
                    return;
                }
                handActionTask.tick(clientPlayer);
                if (handActionTask.isCompleted()) {
                    handActionTask = null;
                }
            }
        }
    }

    public IHandActionTask getHandActionTask() {
        return handActionTask;
    }

    public void breakTask() {
        if (handActionTask != null) {
            handActionTask.stop();
            handActionTask = null;
            lastEndTask = System.currentTimeMillis();
            lastStartTask = 0;
        }
    }

    public boolean hasTask() {
        return handActionTask != null;
    }

    public void setHandActionTask(IHandActionTask handActionTask) {
        if (handActionTask.getItemStack().getItem() instanceof HandActionGun) {
            if (this.handActionTask == null) {
                this.handActionTask = handActionTask;
            }
        }
    }
}
