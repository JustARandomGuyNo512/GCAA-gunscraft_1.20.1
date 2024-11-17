package sheridan.gcaa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.IGun;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class HandActionHandler {
    private static final Map<String, Object> TEMP = new HashMap<>();
    public static final HandActionHandler INSTANCE = new HandActionHandler();
    private IHandActionTask handActionTask;
    public static long lastEndTask = 0;
    public static long lastStartTask = 0;
    public static boolean lastTaskFinished = false;

    public void tick(Player clientPlayer) {
        if (!Minecraft.getInstance().isPaused() && !clientPlayer.isSpectator()) {
            if (handActionTask != null) {
                boolean shouldCancel = !ItemStack.isSameItem(clientPlayer.getMainHandItem(), handActionTask.getItemStack())
                        || !(clientPlayer.getMainHandItem().getItem() instanceof IGun);
                if (shouldCancel) {
                    lastEndTask = System.currentTimeMillis();
                    lastTaskFinished = false;
                    breakTask();
                    return;
                }
                handActionTask.tick(clientPlayer);
                if (handActionTask.isCompleted()) {
                    lastEndTask = System.currentTimeMillis();
                    handActionTask = null;
                    lastTaskFinished = true;
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

    public float secondsSinceLastTask() {
        return (System.currentTimeMillis() - lastEndTask) * 0.001f;
    }

    public void setHandActionTask(IHandActionTask handActionTask) {
        if (handActionTask.getItemStack().getItem() instanceof HandActionGun) {
            if (this.handActionTask == null) {
                this.handActionTask = handActionTask;
                lastStartTask = System.currentTimeMillis();
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
