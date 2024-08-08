package sheridan.gcaa.network.packets.c2s;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.client.screens.containers.AttachmentsMenu;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.UpdateAttachmentScreenGuiContext;

import java.util.function.Supplier;

public class SetAttachmentsPacket implements IPacket<SetAttachmentsPacket> {
    public String attachmentName;
    public String slotName;
    public String modelSlotName;
    public String parentSlot;
    public int itemSlotIndex;

    public SetAttachmentsPacket() {}

    public SetAttachmentsPacket(String attachmentName, String slotName, String modelSlotName, String parentSlot, int itemSlotIndex) {
        this.attachmentName = attachmentName;
        this.slotName = slotName;
        this.modelSlotName = modelSlotName;
        this.parentSlot = parentSlot;
        this.itemSlotIndex = itemSlotIndex;
    }

    @Override
    public void encode(SetAttachmentsPacket message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.attachmentName);
        buffer.writeUtf(message.slotName);
        buffer.writeUtf(message.modelSlotName);
        buffer.writeUtf(message.parentSlot);
        buffer.writeInt(message.itemSlotIndex);
    }

    @Override
    public SetAttachmentsPacket decode(FriendlyByteBuf buffer) {
        SetAttachmentsPacket packet = new SetAttachmentsPacket();
        packet.attachmentName = buffer.readUtf();
        packet.slotName = buffer.readUtf();
        packet.modelSlotName = buffer.readUtf();
        packet.parentSlot = buffer.readUtf();
        packet.itemSlotIndex = buffer.readInt();
        return packet;
    }

    @Override
    public void handle(SetAttachmentsPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            String attachmentName = message.attachmentName;
            IAttachment attachment = AttachmentsRegister.get(attachmentName);
            if (attachment != null) {
                ServerPlayer player = supplier.get().getSender();
                if (player != null) {
                    ItemStack heldItem = player.getMainHandItem();
                    if (heldItem.getItem() instanceof IGun gun) {
                        AttachmentsHandler.INSTANCE.serverSetAttachment(heldItem, gun, attachment,
                                message.slotName, message.modelSlotName, message.parentSlot);
                        ListTag attachments = gun.getAttachmentsListTag(heldItem);
                        if (player.containerMenu instanceof AttachmentsMenu menu) {
                            menu.slots.get(message.itemSlotIndex).set(ItemStack.EMPTY);
                        }
                        PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> player), new UpdateAttachmentScreenGuiContext(attachments));
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
