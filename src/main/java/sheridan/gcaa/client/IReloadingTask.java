package sheridan.gcaa.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * A logic controller that handles the reloading process on the client side and is typically instantiated and assigned to this ReloadingHandler INSTANCE when the reloading process is triggered, with the ReloadingHandler executing the task.
 * */
public interface IReloadingTask {
    /**
     * return ture means this task is successfully completed.
     * */
    boolean isCompleted();
    void tick(Player clientPlayer);
    ItemStack getStack();
    /**
     * return an int value for logical features
     * */
    int getCustomPayload();
    /**
     * When this task is broken, the onBreak() method is executed, for example: shotgun can shoot while reloading,you can write some logical code to execute this method before the task be discarded. and then the tick() method is executed one last time
     * */
    void onBreak();
    /**
     * called when player switch item, reloading progress stop forcibly, this is handled by ReloadingHandler
     * */
    void onCancel();
    /**
     * called when this task be set to ReloadingHandler.
     * */
    void start();
    /**
     * if return true, when the NBT data of the ItemStack instance which the task hold was changed, this task will be canceled.
     * */
    default boolean restrictNBT() {
        return true;
    }
}
