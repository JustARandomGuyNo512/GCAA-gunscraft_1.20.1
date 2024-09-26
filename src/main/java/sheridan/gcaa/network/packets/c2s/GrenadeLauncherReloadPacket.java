package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class GrenadeLauncherReloadPacket implements IPacket<GrenadeLauncherReloadPacket> {
    public GrenadeLauncherReloadPacket() {}

    @Override
    public void encode(GrenadeLauncherReloadPacket message, FriendlyByteBuf buffer) {

    }

    @Override
    public GrenadeLauncherReloadPacket decode(FriendlyByteBuf buffer) {
        return new GrenadeLauncherReloadPacket();
    }

    @Override
    public void handle(GrenadeLauncherReloadPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                ItemStack heldItem = player.getMainHandItem();
                if (heldItem.getItem() instanceof IGun gun) {
                    GrenadeLauncher.reload(heldItem, gun, player);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
