package sheridan.gcaa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.guns.IGun;

import java.util.TimerTask;

public class ClientWeaponLooper extends TimerTask {
    int mainHandDelay;
    int offHandDelay;

    @Override
    public void run() {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (!Minecraft.getInstance().isWindowActive() || minecraft.isPaused() || minecraft.screen != null) {
                return;
            }
            if (player == null) {
                handleCoolDown();
                return;
            }
            if (player.isSpectator() || player.isSwimming() || player.isInLava()) {
                handleCoolDown();
                return;
            }
            if (mainHandDelay <= 0 && Clients.mainButtonDown()) {
                postShootTask(player, true);
            }
            if (offHandDelay <= 0 && Clients.offButtonDown()) {
                postShootTask(player, false);
            }
            handleCoolDown();
        } catch (Exception ignored){}
    }

    private void postShootTask(Player player, boolean mainHand) {
        if (player != null) {
            ItemStack stack = mainHand ? player.getMainHandItem() : player.getOffhandItem();
            if (stack.getItem() instanceof IGun) {
                if (mainHand) {

                } else {

                }
            }
        }
    }

    private void handleCoolDown() {
        mainHandDelay = mainHandDelay > 0 ? mainHandDelay : mainHandDelay - 1;
        offHandDelay = offHandDelay > 0 ? offHandDelay : offHandDelay - 1;
    }


}
