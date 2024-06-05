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
    public long lastShootLeft;
    public long lastShootRight;
    public long lastChamberAction;
    public boolean reloading;

    public BroadcastPlayerStatusPacket() {}

    public BroadcastPlayerStatusPacket(int id, long lastShootLeft, long lastShootRight, long lastChamberAction, boolean reloading) {
        this.id = id;
        this.lastShootLeft = lastShootLeft;
        this.lastShootRight = lastShootRight;
        this.lastChamberAction = lastChamberAction;
        this.reloading = reloading;
    }

    @Override
    public void encode(BroadcastPlayerStatusPacket message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.id);
        buffer.writeLong(message.lastShootLeft);
        buffer.writeLong(message.lastShootRight);
        buffer.writeLong(message.lastChamberAction);
        buffer.writeBoolean(message.reloading);
    }

    @Override
    public BroadcastPlayerStatusPacket decode(FriendlyByteBuf buffer) {
        BroadcastPlayerStatusPacket packet = new BroadcastPlayerStatusPacket();
        packet.id = buffer.readInt();
        packet.lastShootLeft = buffer.readLong();
        packet.lastShootRight = buffer.readLong();
        packet.lastChamberAction = buffer.readLong();
        packet.reloading = buffer.readBoolean();
        return packet;
    }

    @Override
    public void handle(BroadcastPlayerStatusPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    Clients.updateClientPlayerStatus(
                            message.id,
                            message.lastShootRight,
                            message.lastShootLeft,
                            message.lastChamberAction,
                            message.reloading
                    ));
        });
        supplier.get().setPacketHandled(true);
    }
}
