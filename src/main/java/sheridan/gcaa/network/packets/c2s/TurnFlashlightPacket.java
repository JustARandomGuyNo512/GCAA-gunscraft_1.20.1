package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.items.attachments.grips.Flashlight;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class TurnFlashlightPacket implements IPacket<TurnFlashlightPacket> {
    public boolean turnOn;

    public TurnFlashlightPacket(boolean turnOn) {
        this.turnOn = turnOn;
    }

    public TurnFlashlightPacket() {}

    @Override
    public void encode(TurnFlashlightPacket message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.turnOn);
    }

    @Override
    public TurnFlashlightPacket decode(FriendlyByteBuf buffer) {
        return new TurnFlashlightPacket(buffer.readBoolean());
    }

    @Override
    public void handle(TurnFlashlightPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof IGun gun) {
                    Flashlight.switchFlashlightMode(stack, gun);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
