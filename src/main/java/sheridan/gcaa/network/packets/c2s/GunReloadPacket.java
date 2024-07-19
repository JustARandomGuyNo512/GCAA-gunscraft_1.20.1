package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class GunReloadPacket implements IPacket<GunReloadPacket> {

    public GunReloadPacket() {

    }

    @Override
    public void encode(GunReloadPacket message, FriendlyByteBuf buffer) {

    }

    @Override
    public GunReloadPacket decode(FriendlyByteBuf buffer) {
        return new GunReloadPacket();
    }

    @Override
    public void handle(GunReloadPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null && player.getMainHandItem().getItem() instanceof IGun gun) {
                gun.reload(player.getMainHandItem(), player);
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
