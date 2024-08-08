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

public class UpdateAttachmentScreenGuiContext implements IPacket<UpdateAttachmentScreenGuiContext> {
    public ListTag attachments;

    public UpdateAttachmentScreenGuiContext() {}

    public UpdateAttachmentScreenGuiContext(ListTag attachments) {
        this.attachments = attachments;
    }

    @Override
    public void encode(UpdateAttachmentScreenGuiContext message, FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        tag.put("data", message.attachments);
        buffer.writeNbt(tag);
    }

    @Override
    public UpdateAttachmentScreenGuiContext decode(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readNbt();
        return new UpdateAttachmentScreenGuiContext(tag.getList("data", Tag.TAG_COMPOUND));
    }

    @Override
    public void handle(UpdateAttachmentScreenGuiContext message, Supplier<NetworkEvent.Context> supplier) {
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
