package sheridan.gcaa.network.packets.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.Clients;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class UpdateVendingMachineScreenPacket implements IPacket<UpdateVendingMachineScreenPacket>  {
    public long balance;

    public UpdateVendingMachineScreenPacket() {}

    public UpdateVendingMachineScreenPacket(long balance) {
        this.balance = balance;
    }

    @Override
    public void encode(UpdateVendingMachineScreenPacket message, FriendlyByteBuf buffer) {
        buffer.writeLong(message.balance);
    }

    @Override
    public UpdateVendingMachineScreenPacket decode(FriendlyByteBuf buffer) {
        return new UpdateVendingMachineScreenPacket(buffer.readLong());
    }

    @Override
    public void handle(UpdateVendingMachineScreenPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    {
                        Clients.updateVendingMachineScreen(message.balance);
                    }
            );
        });
        supplier.get().setPacketHandled(true);
    }
}
