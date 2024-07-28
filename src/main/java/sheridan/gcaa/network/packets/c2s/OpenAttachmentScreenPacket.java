package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import sheridan.gcaa.client.screens.containers.providers.AttachmentMenuProvider;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class OpenAttachmentScreenPacket implements IPacket<OpenAttachmentScreenPacket>  {
    @Override
    public void encode(OpenAttachmentScreenPacket message, FriendlyByteBuf buffer) {

    }

    @Override
    public OpenAttachmentScreenPacket decode(FriendlyByteBuf buffer) {
        return new OpenAttachmentScreenPacket();
    }

    @Override
    public void handle(OpenAttachmentScreenPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                ItemStack heldItem = player.getMainHandItem();
                if (heldItem.getItem() instanceof IGun) {
                    NetworkHooks.openScreen(player, new AttachmentMenuProvider());
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
