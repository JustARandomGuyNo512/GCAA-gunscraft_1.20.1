package sheridan.gcaa.items.gun.fireModes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.SprintingHandler;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;

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
        return fireInSprinting(player, itemStack, gun, 40);
    }

    @Override
    public void clientShoot(Player player, ItemStack itemStack, IGun gun) {
        if (Clients.mainHandStatus.fireCount < burstCount) {
            gun.clientShoot(itemStack, player, this);
            Clients.mainHandStatus.fireCount ++;
        } else {
            Clients.mainHandStatus.buttonDown.set(false);
            Clients.mainHandStatus.fireCount = 0;
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
