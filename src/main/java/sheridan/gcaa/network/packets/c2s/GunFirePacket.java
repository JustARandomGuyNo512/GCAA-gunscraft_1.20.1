package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class GunFirePacket implements IPacket<GunFirePacket> {


    @Override
    public void encode(GunFirePacket message, FriendlyByteBuf buffer) {

    }

    @Override
    public GunFirePacket decode(FriendlyByteBuf buffer) {
        return null;
    }

    @Override
    public void handle(GunFirePacket message, Supplier<NetworkEvent.Context> supplier) {

    }
}
