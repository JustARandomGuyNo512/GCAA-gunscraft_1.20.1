package sheridan.gcaa.items.gun.fireModes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;
import sheridan.gcaa.utils.RenderAndMathUtils;

public class Burst implements IGunFireMode {
    public final int burstCount;

    public Burst(int burstCount) {
        this.burstCount = burstCount;
    }

    @Override
    public String getName() {
        return "burst";
    }

    @Override
    public boolean canFire(Player player, ItemStack itemStack, IGun gun) {
        return fireInSprinting(player, itemStack, gun, RenderAndMathUtils.secondsToTicks(1.25f));
    }

    @Override
    public void clientShoot(Player player, ItemStack itemStack, IGun gun) {
        if (Clients.MAIN_HAND_STATUS.fireCount < burstCount) {
            gun.clientShoot(itemStack, player, this);
            Clients.MAIN_HAND_STATUS.fireCount ++;
        } else {
            Clients.MAIN_HAND_STATUS.buttonDown.set(false);
            Clients.MAIN_HAND_STATUS.fireCount = 0;
        }
    }

    @Override
    public void shoot(Player player, ItemStack itemStack, IGun gun, float spread) {
        gun.shoot(itemStack, player, this, spread);
    }

    @Override
    public Component getTooltipName() {
        return Component.translatable("tooltip.fire_mode.burst");
    }
}
