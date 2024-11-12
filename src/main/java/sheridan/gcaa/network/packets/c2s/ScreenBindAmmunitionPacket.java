package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.client.screens.containers.GunModifyMenu;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class ScreenBindAmmunitionPacket implements IPacket<ScreenBindAmmunitionPacket> {

    @Override
    public void encode(ScreenBindAmmunitionPacket message, FriendlyByteBuf buffer) {}

    @Override
    public ScreenBindAmmunitionPacket decode(FriendlyByteBuf buffer) {
        return new ScreenBindAmmunitionPacket();
    }

    @Override
    public void handle(ScreenBindAmmunitionPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                if (player.containerMenu instanceof GunModifyMenu menu) {
                    ItemStack itemStack = menu.ammoSelector.getItem(0);
                    ItemStack gunStack = player.getMainHandItem();
                    if (itemStack.getItem() instanceof IAmmunition ammunition && gunStack.getItem() instanceof IGun gun) {
                        if (ammunition == gun.getGunProperties().caliber.ammunition) {
                            gun.bindAmmunition(gunStack, itemStack, ammunition);
                        }
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
