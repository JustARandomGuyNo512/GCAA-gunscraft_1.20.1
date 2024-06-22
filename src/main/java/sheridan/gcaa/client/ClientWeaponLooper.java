package sheridan.gcaa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.guns.IGun;

import java.util.TimerTask;

@OnlyIn(Dist.CLIENT)
public class ClientWeaponLooper extends TimerTask {
    int mainHandDelay;

    @Override
    public void run() {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (!Minecraft.getInstance().isWindowActive() || minecraft.isPaused() || minecraft.screen != null) {
                return;
            }
            if (player == null || player.isSpectator() || player.isSwimming() || player.isInLava()) {
                handleCoolDown();
                return;
            }
            if (mainHandDelay <= 0 && Clients.mainButtonDown()) {
                postShootTask(player);
            }
            handleCoolDown();
        } catch (Exception ignored){}
    }

    private void postShootTask(Player player) {
        if (player != null) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IGun gun) {
                Clients.handleClientShoot(stack, gun, player);
            }
        }
    }

    private void handleCoolDown() {
        mainHandDelay = mainHandDelay > 0 ? mainHandDelay : mainHandDelay - 1;
    }

}
