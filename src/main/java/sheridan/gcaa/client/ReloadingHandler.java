package sheridan.gcaa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class ReloadingHandler {
    public static final ReloadingHandler INSTANCE = new ReloadingHandler();
    public static final int EMPTY_PAYLOAD = -1;
    private int lastPayload = EMPTY_PAYLOAD;
    private IReloadingTask reloadingTask;
    private long lastStartReload = 0;
    private long lastEndReload = 0;
    private boolean usingGenericReload = false;

    public boolean reloading() {
        return reloadingTask != null;
    }

    public static boolean isReloading() {
        return INSTANCE.reloading();
    }

    public int getLastPayload() {
        return lastPayload;
    }

    public long getLastStartReload() {
        return lastStartReload;
    }

    public long getLastEndReload() {
        return lastEndReload;
    }

    public boolean isUsingGenericReload() {
        return usingGenericReload;
    }

    public IReloadingTask getReloadingTask() {
        return reloadingTask;
    }

    public void breakTask() {
        if (reloadingTask != null && !reloadingTask.isCompleted()) {
            reloadingTask.onBreak();
            reloadingTask.tick(Minecraft.getInstance().player);
        }
        clearTask();
    }

    public static boolean disFromLastReload(long ms) {
        return System.currentTimeMillis() - INSTANCE.getLastStartReload() > ms;
    }

    private void clearTask() {
        this.reloadingTask = null;
        this.lastStartReload = 0;
        lastEndReload = System.currentTimeMillis();
    }

    public void cancelTask() {
        if (reloadingTask != null && !reloadingTask.isCompleted()) {
            reloadingTask.onCancel();
        }
        clearTask();
    }

    public int getCustomPayload(boolean useTemp) {
        if (reloadingTask != null) {
            lastPayload = reloadingTask.getCustomPayload();
            return lastPayload;
        } else {
            return useTemp ? lastPayload : EMPTY_PAYLOAD;
        }
    }

    public void setTask(IReloadingTask task) {
        if (task.getStack().getItem() instanceof IGun) {
            if (reloadingTask == null || !ItemStack.isSameItemSameTags(reloadingTask.getStack(), task.getStack())) {
                reloadingTask = task;
                reloadingTask.start();
                lastStartReload = System.currentTimeMillis();
            }
        }
    }

    public void tick() {
        if (!Minecraft.getInstance().isPaused()) {
            if (reloadingTask == null) {
                usingGenericReload = false;
                return;
            }
            Player player = Minecraft.getInstance().player;
            if (player != null && !player.isSpectator()) {
                if (reloadingTask != null) {
                    boolean shouldCancel = reloadingTask.restrictNBT() ?
                            !ItemStack.isSameItemSameTags(player.getMainHandItem(), reloadingTask.getStack()) :
                            !ItemStack.isSameItem(player.getMainHandItem(), reloadingTask.getStack());
                    if (shouldCancel) {
                        cancelTask();
                        return;
                    }
                    reloadingTask.tick(player);
                    if (reloadingTask.isCompleted()) {
                        clearTask();
                    }
                }
            }
        }
    }
}
