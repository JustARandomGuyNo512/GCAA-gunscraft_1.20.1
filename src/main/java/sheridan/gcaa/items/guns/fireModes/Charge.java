package sheridan.gcaa.items.guns.fireModes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.guns.IGun;
import sheridan.gcaa.items.guns.IGunFireMode;

public class Charge implements IGunFireMode {
    @Override
    public String getName() {
        return "charge";
    }

    @Override
    public boolean canFire(Player player, ItemStack itemStack, IGun gun) {
        return false;
    }

    @Override
    public void preShoot(Player player, ItemStack itemStack, IGun gun) {

    }

    @Override
    public void shoot(Player player, ItemStack itemStack, IGun gun) {

    }

    @Override
    public Component getTooltipName() {
        return Component.translatable("tooltip.gunscraft.charge");
    }
}
