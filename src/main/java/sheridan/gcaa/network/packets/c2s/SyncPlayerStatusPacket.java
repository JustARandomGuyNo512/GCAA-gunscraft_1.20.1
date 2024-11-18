package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class SyncPlayerStatusPacket implements IPacket<SyncPlayerStatusPacket> {
    public long lastShoot;
    public long lastChamberAction;
    public long localTimeOffset;
    public boolean reloading;

    public SyncPlayerStatusPacket() {}

    public SyncPlayerStatusPacket(long lastShootLeft, long lastChamberAction, long localTimeOffset, boolean reloading) {
        this.lastShoot = lastShootLeft;
        this.lastChamberAction = lastChamberAction;
        this.localTimeOffset = localTimeOffset;
        this.reloading = reloading;
    }

    @Override
    public void encode(SyncPlayerStatusPacket message, FriendlyByteBuf buffer) {
        buffer.writeLong(message.lastShoot);
        buffer.writeLong(message.lastChamberAction);
        buffer.writeLong(message.localTimeOffset);
        buffer.writeBoolean(message.reloading);
    }

    @Override
    public SyncPlayerStatusPacket decode(FriendlyByteBuf buffer) {
        SyncPlayerStatusPacket packet = new SyncPlayerStatusPacket();
        packet.lastShoot = buffer.readLong();
        packet.lastChamberAction = buffer.readLong();
        packet.localTimeOffset = buffer.readLong();
        packet.reloading = buffer.readBoolean();
        return packet;
    }

    @Override
    public void handle(SyncPlayerStatusPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((capability -> {
                    capability.setLastShoot(message.lastShoot);
                    capability.setLastChamberAction(message.lastChamberAction);
                    capability.setReloading(message.reloading);
                    capability.setLocalTimeOffset(message.localTimeOffset);
                    capability.serverSetLatency(player);
                    capability.dataChanged = true;
                }));
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
