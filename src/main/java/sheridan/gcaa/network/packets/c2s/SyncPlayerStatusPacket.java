package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class SyncPlayerStatusPacket implements IPacket<SyncPlayerStatusPacket> {
    public long lastShootLeft;
    public long lastShootRight;
    public long lastChamberAction;
    public boolean reloading;

    public SyncPlayerStatusPacket() {}

    public SyncPlayerStatusPacket(long lastShootLeft, long lastShootRight, long lastChamberAction, boolean reloading) {
        this.lastShootLeft = lastShootLeft;
        this.lastShootRight = lastShootRight;
        this.lastChamberAction = lastChamberAction;
        this.reloading = reloading;
    }

    @Override
    public void encode(SyncPlayerStatusPacket message, FriendlyByteBuf buffer) {
        buffer.writeLong(message.lastShootLeft);
        buffer.writeLong(message.lastShootRight);
        buffer.writeLong(message.lastChamberAction);
        buffer.writeBoolean(message.reloading);
    }

    @Override
    public SyncPlayerStatusPacket decode(FriendlyByteBuf buffer) {
        SyncPlayerStatusPacket packet = new SyncPlayerStatusPacket();
        packet.lastShootLeft = buffer.readLong();
        packet.lastShootRight = buffer.readLong();
        packet.lastChamberAction = buffer.readLong();
        packet.reloading = buffer.readBoolean();
        return packet;
    }

    @Override
    public void handle(SyncPlayerStatusPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((capability -> {
                    capability.setLastShootLeft(message.lastShootLeft);
                    capability.setLastShootRight(message.lastShootRight);
                    capability.setLastChamberAction(message.lastChamberAction);
                    capability.setReloading(message.reloading);
                    capability.dataChanged = true;
                }));
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
