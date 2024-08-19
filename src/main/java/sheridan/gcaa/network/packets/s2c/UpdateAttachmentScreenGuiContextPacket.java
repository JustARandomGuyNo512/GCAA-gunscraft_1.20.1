package sheridan.gcaa.network.packets.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.Clients;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class UpdateAttachmentScreenGuiContextPacket implements IPacket<UpdateAttachmentScreenGuiContextPacket> {
    public ListTag attachments;

    public UpdateAttachmentScreenGuiContextPacket() {}

    public UpdateAttachmentScreenGuiContextPacket(ListTag attachments) {
        this.attachments = attachments;
    }

    @Override
    public void encode(UpdateAttachmentScreenGuiContextPacket message, FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        tag.put("data", message.attachments);
        buffer.writeNbt(tag);
    }

    @Override
    public UpdateAttachmentScreenGuiContextPacket decode(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readNbt();
        return new UpdateAttachmentScreenGuiContextPacket(tag.getList("data", Tag.TAG_COMPOUND));
    }

    @Override
    public void handle(UpdateAttachmentScreenGuiContextPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    {
                        Clients.updateAttachmentScreenGuiContext(message.attachments);
                    }
            );
        });
        supplier.get().setPacketHandled(true);
    }
}
