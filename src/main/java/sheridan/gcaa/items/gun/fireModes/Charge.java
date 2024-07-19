package sheridan.gcaa.items.gun.fireModes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;

public class Charge implements IGunFireMode {

    private final int chargeLength;

    public Charge(int chargeLength) {
        this.chargeLength = chargeLength;
    }

    public int getChargeLength() {
        return chargeLength;
    }

    @Override
    public String getName() {
        return "charge";
    }

    @Override
    public boolean canFire(Player player, ItemStack itemStack, IGun gun) {
        return gun.getAmmoLeft(itemStack) > 0 && Clients.mainHandStatus.chargeTick >= chargeLength;
    }

    @Override
    public void clientShoot(Player player, ItemStack itemStack, IGun gun) {

    }

    @Override
    public void shoot(Player player, ItemStack itemStack, IGun gun) {

    }

    @Override
    public Component getTooltipName() {
        return Component.translatable("tooltip.gunscraft.charge");
    }
}
