package sheridan.gcaa.network.packets.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.Clients;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class BroadcastPlayerStatusPacket implements IPacket<BroadcastPlayerStatusPacket> {
    public int id;
    public long lastShoot;
    public long lastChamberAction;
    public long localTimeOffset;
    public int latency;
    public long balance;
    public boolean reloading;

    public BroadcastPlayerStatusPacket() {}

    public BroadcastPlayerStatusPacket(int id, long lastShootLeft, long lastChamberAction, long localTimeOffset,
                                       int latency, long balance, boolean reloading) {
        this.id = id;
        this.lastShoot = lastShootLeft;
        this.lastChamberAction = lastChamberAction;
        this.localTimeOffset = localTimeOffset;
        this.latency = latency;
        this.balance = balance;
        this.reloading = reloading;
    }

    @Override
    public void encode(BroadcastPlayerStatusPacket message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.id);
        buffer.writeLong(message.lastShoot);
        buffer.writeLong(message.lastChamberAction);
        buffer.writeLong(message.localTimeOffset);
        buffer.writeInt(message.latency);
        buffer.writeLong(message.balance);
        buffer.writeBoolean(message.reloading);
    }

    @Override
    public BroadcastPlayerStatusPacket decode(FriendlyByteBuf buffer) {
        BroadcastPlayerStatusPacket packet = new BroadcastPlayerStatusPacket();
        packet.id = buffer.readInt();
        packet.lastShoot = buffer.readLong();
        packet.lastChamberAction = buffer.readLong();
        packet.localTimeOffset = buffer.readLong();
        packet.latency = buffer.readInt();
        packet.balance = buffer.readLong();
        packet.reloading = buffer.readBoolean();
        return packet;
    }

    @Override
    public void handle(BroadcastPlayerStatusPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                Clients.updateClientPlayerStatus(
                        message.id,
                        message.lastShoot,
                        message.lastChamberAction,
                        message.localTimeOffset,
                        message.latency,
                        message.balance,
                        message.reloading
                )));
        supplier.get().setPacketHandled(true);
    }
}
