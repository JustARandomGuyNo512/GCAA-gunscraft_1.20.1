package sheridan.gcaa.items.guns.fireModes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.guns.IGun;
import sheridan.gcaa.items.guns.IGunFireMode;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.GunFirePacket;

public class Semi implements IGunFireMode {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean canFire(Player player, ItemStack itemStack, IGun gun) {
        return gun.getAmmoLeft(itemStack) > 0;
    }

    @Override
    public void clientShoot(Player player, ItemStack itemStack, IGun gun) {
        gun.clientShoot(itemStack, player);
        PacketHandler.simpleChannel.sendToServer(new GunFirePacket());
        Clients.mainHandStatus.buttonDown.set(false);
        Clients.mainHandStatus.fireCount = 0;
    }

    @Override
    public void shoot(Player player, ItemStack itemStack, IGun gun) {
        gun.shoot(itemStack, player);
    }

    @Override
    public Component getTooltipName() {
        return Component.translatable("tooltip.gunscraft.semi");
    }
}
