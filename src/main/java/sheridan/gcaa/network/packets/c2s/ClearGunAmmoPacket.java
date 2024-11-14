package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class ClearGunAmmoPacket implements IPacket<ClearGunAmmoPacket> {
    @Override
    public void encode(ClearGunAmmoPacket message, FriendlyByteBuf buffer) {

    }

    @Override
    public ClearGunAmmoPacket decode(FriendlyByteBuf buffer) {
        return new ClearGunAmmoPacket();
    }

    @Override
    public void handle(ClearGunAmmoPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof IGun gun) {
                    gun.clearAmmo(stack, player);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
