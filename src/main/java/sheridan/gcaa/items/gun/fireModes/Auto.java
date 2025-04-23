package sheridan.gcaa.items.gun.fireModes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;
import sheridan.gcaa.utils.RenderAndMathUtils;

public class Auto implements IGunFireMode {

    public static final Auto AUTO = new Auto();

    @Override
    public String getName() {
        return "auto";
    }

    @Override
    public boolean canFire(Player player, ItemStack itemStack, IGun gun) {
        return fireInSprinting(player, itemStack, gun, RenderAndMathUtils.secondsToTicks(1.25f));
    }

    @Override
    public void clientShoot(Player player, ItemStack itemStack, IGun gun) {
        gun.clientShoot(itemStack, player, this);
        Clients.MAIN_HAND_STATUS.fireCount ++;
    }

    @Override
    public void shoot(Player player, ItemStack itemStack, IGun gun, float spread) {
        gun.shoot(itemStack, player, this, spread);
    }

    @Override
    public Component getTooltipName() {
        return Component.translatable("tooltip.fire_mode.auto");
    }
}
