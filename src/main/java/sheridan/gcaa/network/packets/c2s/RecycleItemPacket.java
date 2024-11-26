package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import sheridan.gcaa.network.IPacket;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.UpdateVendingMachineScreenPacket;
import sheridan.gcaa.service.ProductTradingHandler;

import java.util.function.Supplier;

public class RecycleItemPacket implements IPacket<RecycleItemPacket> {
    public int id;

    public RecycleItemPacket() {}

    public RecycleItemPacket(int id) {
        this.id = id;
    }

    @Override
    public void encode(RecycleItemPacket message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.id);
    }

    @Override
    public RecycleItemPacket decode(FriendlyByteBuf buffer) {
        return new RecycleItemPacket(buffer.readInt());
    }

    @Override
    public void handle(RecycleItemPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            long balance = ProductTradingHandler.recycle(player, message.id);
            if (player != null) {
                PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> player), new UpdateVendingMachineScreenPacket(
                        balance
                ));
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
