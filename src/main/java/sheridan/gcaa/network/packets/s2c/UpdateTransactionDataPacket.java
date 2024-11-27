package sheridan.gcaa.network.packets.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.Clients;
import sheridan.gcaa.network.IPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class UpdateTransactionDataPacket implements IPacket<UpdateTransactionDataPacket> {
    public List<Integer> playerIds  = new ArrayList<>();

    public UpdateTransactionDataPacket() {
    }

    public UpdateTransactionDataPacket(List<Integer> playerIds) {
        this.playerIds = playerIds;
    }

    @Override
    public void encode(UpdateTransactionDataPacket message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.playerIds.size());
        for (int i : message.playerIds) {
            buffer.writeInt(i);
        }
    }
    @Override
    public UpdateTransactionDataPacket decode(FriendlyByteBuf buffer) {
        UpdateTransactionDataPacket updateTransactionDataPacket = new UpdateTransactionDataPacket();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            updateTransactionDataPacket.playerIds.add(buffer.readInt());
        }
        return updateTransactionDataPacket;
    }

    @Override
    public void handle(UpdateTransactionDataPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    {
                        Clients.updateTransactionTerminalScreenData(message.playerIds);
                    }
            );
        });
        supplier.get().setPacketHandled(true);
    }
}
