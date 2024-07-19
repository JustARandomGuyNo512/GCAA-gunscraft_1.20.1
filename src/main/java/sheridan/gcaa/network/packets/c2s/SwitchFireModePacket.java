package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class SwitchFireModePacket implements IPacket<SwitchFireModePacket> {

    public SwitchFireModePacket() {}

    @Override
    public void encode(SwitchFireModePacket message, FriendlyByteBuf buffer) {

    }

    @Override
    public SwitchFireModePacket decode(FriendlyByteBuf buffer) {
        return new SwitchFireModePacket();
    }

    @Override
    public void handle(SwitchFireModePacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null && player.getMainHandItem().getItem() instanceof IGun gun) {
               gun.switchFireMode(player.getMainHandItem());
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
