package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class SetScopeMagnificationPacket implements IPacket<SetScopeMagnificationPacket> {
    public String attachmentId;
    public float magnification;

    public SetScopeMagnificationPacket() {}

    public SetScopeMagnificationPacket(String attachmentId, float magnification) {
        this.attachmentId = attachmentId;
        this.magnification = magnification;
    }

    @Override
    public void encode(SetScopeMagnificationPacket message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.attachmentId);
        buffer.writeFloat(message.magnification);
    }

    @Override
    public SetScopeMagnificationPacket decode(FriendlyByteBuf buffer) {
        return new SetScopeMagnificationPacket(
                buffer.readUtf(),
                buffer.readFloat()
        );
    }

    @Override
    public void handle(SetScopeMagnificationPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                ItemStack heldItem = player.getMainHandItem();
                if (heldItem.getItem() instanceof IGun gun) {
                    gun.getGun().setMagnificationsRateFor(message.attachmentId, heldItem, message.magnification);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
