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

public class UpdateGunModifyScreenGuiContextPacket implements IPacket<UpdateGunModifyScreenGuiContextPacket> {
    public ListTag attachments;

    public UpdateGunModifyScreenGuiContextPacket() {}

    public UpdateGunModifyScreenGuiContextPacket(ListTag attachments) {
        this.attachments = attachments;
    }

    @Override
    public void encode(UpdateGunModifyScreenGuiContextPacket message, FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        tag.put("data", message.attachments);
        buffer.writeNbt(tag);
    }

    @Override
    public UpdateGunModifyScreenGuiContextPacket decode(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readNbt();
        return new UpdateGunModifyScreenGuiContextPacket(tag.getList("data", Tag.TAG_COMPOUND));
    }

    @Override
    public void handle(UpdateGunModifyScreenGuiContextPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    {
                        Clients.updateGunModifyScreenGuiContext(message.attachments);
                    }
            );
        });
        supplier.get().setPacketHandled(true);
    }
}
