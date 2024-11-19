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

public class ExchangePacket implements IPacket<ExchangePacket> {
    public long worth;

    public ExchangePacket() {}

    public ExchangePacket(long worth) {
        this.worth = worth;
    }

    @Override
    public void encode(ExchangePacket message, FriendlyByteBuf buffer) {
        buffer.writeLong(message.worth);
    }

    @Override
    public ExchangePacket decode(FriendlyByteBuf buffer) {
        return new ExchangePacket(buffer.readLong());
    }

    @Override
    public void handle(ExchangePacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            long balance = ProductTradingHandler.exchange(player, message.worth);
            if (player != null) {
                PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> player), new UpdateVendingMachineScreenPacket(
                        balance
                ));
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
