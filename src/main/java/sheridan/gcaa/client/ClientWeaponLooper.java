package sheridan.gcaa.client;

import net.minecraft.client.Minecraft;
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
            work();
        } catch (Exception ignored){}
    }

    private void work() {
        if (Clients.cancelLooperWork.get()) {
            return;
        }
        if (Clients.cancelLooperWorkWithCoolDown.get()) {
            handleCoolDown();
            return;
        }
        if (mainHandDelay <= 0 && Clients.mainButtonDown()) {
            postShootTask();
        }
        handleCoolDown();
    }

    private void postShootTask() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IGun gun) {
                mainHandDelay = Clients.handleClientShoot(stack, gun, player);
            }
        }
    }

    private void handleCoolDown() {
        mainHandDelay = mainHandDelay > 0 ? mainHandDelay - 1 : mainHandDelay;
    }

}
