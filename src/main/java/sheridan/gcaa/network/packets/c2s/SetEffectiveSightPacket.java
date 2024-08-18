package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class SetEffectiveSightPacket implements IPacket<SetEffectiveSightPacket> {
    public String uuid;

    public SetEffectiveSightPacket() {}

    public SetEffectiveSightPacket(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public void encode(SetEffectiveSightPacket message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.uuid);
    }

    @Override
    public SetEffectiveSightPacket decode(FriendlyByteBuf buffer) {
        return new SetEffectiveSightPacket(buffer.readUtf());
    }

    @Override
    public void handle(SetEffectiveSightPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                ItemStack heldItem = player.getMainHandItem();
                if (heldItem.getItem() instanceof IGun gun) {
                    gun.setEffectiveSightUUID(heldItem, message.uuid);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
