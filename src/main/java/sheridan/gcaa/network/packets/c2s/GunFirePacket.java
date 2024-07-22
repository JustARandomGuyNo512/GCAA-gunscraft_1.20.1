package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class GunFirePacket implements IPacket<GunFirePacket> {
    public float spread;

    public GunFirePacket(float spread) {
        this.spread = spread;
    }

    public GunFirePacket() {
    }

    @Override
    public void encode(GunFirePacket message, FriendlyByteBuf buffer) {
        buffer.writeFloat(message.spread);
    }

    @Override
    public GunFirePacket decode(FriendlyByteBuf buffer) {
        return new GunFirePacket(buffer.readFloat());
    }

    @Override
    public void handle(GunFirePacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof IGun gun) {
                    gun.getFireMode(stack).shoot(player, stack, gun, message.spread);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
