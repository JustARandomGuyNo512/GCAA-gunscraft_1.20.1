package sheridan.gcaa.items.gun.fireModes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.HandActionHandler;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;

public class HandAction implements IGunFireMode {
    @Override
    public String getName() {
        return "hand_action";
    }

    @Override
    public boolean canFire(Player player, ItemStack itemStack, IGun gun) {
        if (gun.getGun() instanceof HandActionGun handActionGun) {
            boolean hasAmmo = gun.getAmmoLeft(itemStack) > 0;
            boolean needHandAction = handActionGun.needHandAction(itemStack);
            boolean can = hasAmmo && !needHandAction;
            if (needHandAction && hasAmmo && System.currentTimeMillis() - Clients.lastShootMain() > 100L) {
                HandActionHandler.INSTANCE.setHandActionTask(handActionGun.getHandActionTask(itemStack, true));
            }
            return can;
        }
        return false;
    }

    @Override
    public void clientShoot(Player player, ItemStack itemStack, IGun gun) {
        gun.clientShoot(itemStack, player, this);
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
