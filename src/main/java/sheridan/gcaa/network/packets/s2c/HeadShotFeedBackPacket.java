package sheridan.gcaa.network.packets.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.Clients;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class HeadShotFeedBackPacket implements IPacket<HeadShotFeedBackPacket> {
    @Override
    public void encode(HeadShotFeedBackPacket message, FriendlyByteBuf buffer) {

    }

    @Override
    public HeadShotFeedBackPacket decode(FriendlyByteBuf buffer) {
        return new HeadShotFeedBackPacket();
    }

    @Override
    public void handle(HeadShotFeedBackPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    {
                        Clients.handleClientHeadShotFeedBack();
                    }
            );
        });
        supplier.get().setPacketHandled(true);
    }
}
