package sheridan.gcaa.items.gun.fireModes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;
import sheridan.gcaa.utils.RenderAndMathUtils;

public class Semi implements IGunFireMode {

    public static final Semi SEMI = new Semi();

    @Override
    public String getName() {
        return "semi";
    }

    @Override
    public boolean canFire(Player player, ItemStack itemStack, IGun gun) {
        if (gun instanceof HandActionGun handActionGun && handActionGun.needHandAction(itemStack)) {
            return false;
        }
        return fireInSprinting(player, itemStack, gun, RenderAndMathUtils.secondsToTicks(1.25f));
    }

    @Override
    public void clientShoot(Player player, ItemStack itemStack, IGun gun) {
        gun.clientShoot(itemStack, player, this);
        Clients.MAIN_HAND_STATUS.buttonDown.set(false);
        Clients.MAIN_HAND_STATUS.fireCount = 0;
    }

    @Override
    public void shoot(Player player, ItemStack itemStack, IGun gun, float spread) {
        gun.shoot(itemStack, player, this, spread);
    }

    @Override
    public Component getTooltipName() {
        return Component.translatable("tooltip.fire_mode.semi");
    }
}
