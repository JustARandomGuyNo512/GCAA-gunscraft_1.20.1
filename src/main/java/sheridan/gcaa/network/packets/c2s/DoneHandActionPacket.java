package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class DoneHandActionPacket implements IPacket<DoneHandActionPacket> {
    public DoneHandActionPacket() {}

    @Override
    public void encode(DoneHandActionPacket message, FriendlyByteBuf buffer) {

    }

    @Override
    public DoneHandActionPacket decode(FriendlyByteBuf buffer) {
        return new DoneHandActionPacket();
    }

    @Override
    public void handle(DoneHandActionPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                ItemStack heldItem = player.getMainHandItem();
                if (heldItem.getItem() instanceof HandActionGun gun) {
                    gun.setNeedHandAction(heldItem, gun.getAmmoLeft(heldItem) == 0);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
