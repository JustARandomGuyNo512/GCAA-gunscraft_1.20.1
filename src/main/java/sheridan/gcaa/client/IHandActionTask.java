package sheridan.gcaa.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IHandActionTask {
    ItemStack getItemStack();
    void tick(Player clientPlayer);
    boolean isCompleted();
    void stop();
    void start();
}
