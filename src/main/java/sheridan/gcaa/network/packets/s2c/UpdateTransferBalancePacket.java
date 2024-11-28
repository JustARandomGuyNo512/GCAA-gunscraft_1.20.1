package sheridan.gcaa.network.packets.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.Clients;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class UpdateTransferBalancePacket implements IPacket<UpdateTransferBalancePacket> {
    public long balance;

    public UpdateTransferBalancePacket(long balance) {
        this.balance = balance;
    }

    public UpdateTransferBalancePacket() {
    }

    @Override
    public void encode(UpdateTransferBalancePacket message, FriendlyByteBuf buffer) {
        buffer.writeLong(message.balance);
    }

    @Override
    public UpdateTransferBalancePacket decode(FriendlyByteBuf buffer) {
        return new UpdateTransferBalancePacket(buffer.readLong());
    }

    @Override
    public void handle(UpdateTransferBalancePacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    {
                        Clients.updateTransferBalance(message.balance);
                    }
            );
        });
        supplier.get().setPacketHandled(true);
    }
}
