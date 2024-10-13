package sheridan.gcaa.items.gun.fireModes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.HandActionHandler;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.SprintingHandler;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;
import sheridan.gcaa.items.gun.PumpActionShotgun;

public class HandAction implements IGunFireMode {
    public static final HandAction HAND_ACTION = new HandAction();
    
    @Override
    public String getName() {
        return "hand_action";
    }

    @Override
    public boolean canFire(Player player, ItemStack itemStack, IGun gun) {
        SprintingHandler.INSTANCE.exitSprinting(40);
        if (SprintingHandler.INSTANCE.getSprintingProgress() != 0) {
            return false;
        }
        if (gun.getGun() instanceof HandActionGun handActionGun) {
            boolean hasAmmo = gun.getAmmoLeft(itemStack) > 0;
            boolean needHandAction = handActionGun.needHandAction(itemStack);
            boolean can = hasAmmo && !needHandAction;
            if (needHandAction && hasAmmo && HandActionHandler.INSTANCE.secondsSinceLastTask() > 0.5f) {
                HandActionHandler.INSTANCE.setHandActionTask(handActionGun.getHandActionTask(itemStack, true));
            }
            if (gun instanceof PumpActionShotgun && hasAmmo) {
                ReloadingHandler.INSTANCE.breakTask();
            }
            return can;
        }
        return false;
    }

    @Override
    public void clientShoot(Player player, ItemStack itemStack, IGun gun) {
        gun.clientShoot(itemStack, player, this);
        Clients.mainHandStatus.buttonDown.set(false);
    }

    @Override
    public void shoot(Player player, ItemStack itemStack, IGun gun, float spread) {
        gun.shoot(itemStack, player, this, spread);
    }

    @Override
    public Component getTooltipName() {
        return Component.translatable("tooltip.fire_mode.hand_action");
    }
}
