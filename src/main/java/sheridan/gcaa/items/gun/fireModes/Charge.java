package sheridan.gcaa.items.gun.fireModes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;

public class Charge implements IGunFireMode {
    private final int chargeLength;
    private final String name;

    public Charge(int chargeLength, String name)  {
        this.chargeLength = Math.max(2, chargeLength);
        this.name = "tooltip.fire_mode." + name;
    }

    public int getChargeLength() {
        return chargeLength;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean canFire(Player player, ItemStack itemStack, IGun gun) {
        return gun.getAmmoLeft(itemStack) > 0 && Clients.mainHandStatus.chargeTick >= chargeLength;
    }

    @Override
    public void clientShoot(Player player, ItemStack itemStack, IGun gun) {
        gun.clientShoot(itemStack, player, this);
        Clients.mainHandStatus.buttonDown.set(false);
        Clients.mainHandStatus.fireCount = 0;
        Clients.mainHandStatus.clearCharge();
    }

    @Override
    public void shoot(Player player, ItemStack itemStack, IGun gun, float spread) {
        gun.shoot(itemStack, player, this, spread);
    }

    @Override
    public Component getTooltipName() {
        return Component.translatable(name);
    }
}
