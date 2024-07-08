package sheridan.gcaa.items.guns.fireModes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.guns.IGun;
import sheridan.gcaa.items.guns.IGunFireMode;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.GunFirePacket;

public class Auto implements IGunFireMode {

    public static final Auto AUTO = new Auto();

    @Override
    public String getName() {
        return "auto";
    }

    @Override
    public boolean canFire(Player player, ItemStack itemStack, IGun gun) {
        return gun.getAmmoLeft(itemStack) > 0;
    }

    @Override
    public void clientShoot(Player player, ItemStack itemStack, IGun gun) {
        gun.clientShoot(itemStack, player, this);
        Clients.mainHandStatus.fireCount ++;
    }

    @Override
    public void shoot(Player player, ItemStack itemStack, IGun gun) {
        gun.shoot(itemStack, player, this);
    }

    @Override
    public Component getTooltipName() {
        return Component.translatable("tooltip.gunscraft.auto");
    }
}
