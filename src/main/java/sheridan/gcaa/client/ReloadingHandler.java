package sheridan.gcaa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.gun.IGun;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ReloadingHandler {
    private static final Map<String, Object> TEMP = new HashMap<>();
    public static final ReloadingHandler INSTANCE = new ReloadingHandler();
    public static final int EMPTY_PAYLOAD = -1;
    private int lastPayload = EMPTY_PAYLOAD;
    private IReloadTask reloadingTask;
    private long lastStartReload = 0;
    private long lastEndReload = 0;

    public boolean reloading() {
        return reloadingTask != null;
    }

    public static boolean isReloading() {
        return INSTANCE.reloading();
    }

    public static float getReloadingProgress() {
        return INSTANCE.reloadingTask != null && !INSTANCE.reloadingTask.isCompleted() ? INSTANCE.reloadingTask.getProgress() : 0;
    }

    public static boolean isReloadingGeneric() {
        return INSTANCE.isUsingGenericReloading();
    }

    public boolean isUsingGenericReloading() {
        return reloadingTask != null && reloadingTask.isGenericReloading();
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

    public IReloadTask getReloadingTask() {
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

    public static boolean disFromLastReloadEnd(long ms) {
        return System.currentTimeMillis() - INSTANCE.getLastEndReload() < ms;
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

    public void setTask(IReloadTask task) {
        if (task.getStack().getItem() instanceof IGun) {
            if (reloadingTask == null || !ItemStack.isSameItemSameTags(reloadingTask.getStack(), task.getStack())) {
                if (reloadingTask != null) {
                    reloadingTask.onCancel();
                }
                reloadingTask = task;
                reloadingTask.start();
                lastStartReload = System.currentTimeMillis();
            }
        }
    }

    public void tick(Player player) {
        if (!Minecraft.getInstance().isPaused()) {
            if (reloadingTask == null) {
                return;
            }
            if (!player.isSpectator()) {
                if (reloadingTask != null) {
                    boolean shouldCancel = reloadingTask.restrictNBT() ?
                            !ItemStack.isSameItemSameTags(player.getMainHandItem(), reloadingTask.getStack()) :
                            !ItemStack.isSameItem(player.getMainHandItem(), reloadingTask.getStack());
                    if (shouldCancel || !(player.getMainHandItem().getItem() instanceof IGun)) {
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

    public static void setTempValue(String key, Object value) {
        TEMP.put(key, value);
    }

    public static Object getTempValue(String key) {
        return TEMP.get(key);
    }
}
