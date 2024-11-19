package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.network.IPacket;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.BroadcastPlayerStatusPacket;

import java.util.function.Supplier;

public class RequestDataSyncPacket implements IPacket<RequestDataSyncPacket> {
    @Override
    public void encode(RequestDataSyncPacket message, FriendlyByteBuf buffer) {

    }

    @Override
    public RequestDataSyncPacket decode(FriendlyByteBuf buffer) {
        return new RequestDataSyncPacket();
    }

    @Override
    public void handle(RequestDataSyncPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((cap) ->
                        PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> player),
                                new BroadcastPlayerStatusPacket(
                                        player.getId(),
                                        cap.getLastShoot(),
                                        cap.getLastChamberAction(),
                                        cap.getLocalTimeOffset(),
                                        cap.getLatency(),
                                        cap.getBalance(),
                                        cap.isReloading())));
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
