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
import sheridan.gcaa.network.packets.s2c.UpdateAttachmentScreenGuiContextPacket;

import java.util.function.Supplier;

public class InstallAttachmentsPacket implements IPacket<InstallAttachmentsPacket> {
    public String attachmentName;
    public String slotName;
    public String modelSlotName;
    public String parentUuid;
    public int itemSlotIndex;
    public byte direction;

    public InstallAttachmentsPacket() {}

    public InstallAttachmentsPacket(String attachmentName, String slotName, String modelSlotName, String parentUuid, int itemSlotIndex, byte direction) {
        this.attachmentName = attachmentName;
        this.slotName = slotName;
        this.modelSlotName = modelSlotName;
        this.parentUuid = parentUuid;
        this.itemSlotIndex = itemSlotIndex;
        this.direction = direction;
    }

    @Override
    public void encode(InstallAttachmentsPacket message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.attachmentName);
        buffer.writeUtf(message.slotName);
        buffer.writeUtf(message.modelSlotName);
        buffer.writeUtf(message.parentUuid);
        buffer.writeInt(message.itemSlotIndex);
        buffer.writeByte(message.direction);
    }

    @Override
    public InstallAttachmentsPacket decode(FriendlyByteBuf buffer) {
        InstallAttachmentsPacket packet = new InstallAttachmentsPacket();
        packet.attachmentName = buffer.readUtf();
        packet.slotName = buffer.readUtf();
        packet.modelSlotName = buffer.readUtf();
        packet.parentUuid = buffer.readUtf();
        packet.itemSlotIndex = buffer.readInt();
        packet.direction = buffer.readByte();
        return packet;
    }

    @Override
    public void handle(InstallAttachmentsPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            String attachmentName = message.attachmentName;
            IAttachment attachment = AttachmentsRegister.get(attachmentName);
            if (attachment != null) {
                ServerPlayer player = supplier.get().getSender();
                if (player != null) {
                    ItemStack heldItem = player.getMainHandItem();
                    if (heldItem.getItem() instanceof IGun gun) {
                        AttachmentsHandler.INSTANCE.serverSetAttachment(heldItem, gun, attachment, message.slotName, message.modelSlotName, message.parentUuid, message.direction);
                        ListTag attachments = gun.getAttachmentsListTag(heldItem);
                        if (player.containerMenu instanceof AttachmentsMenu menu) {
                            menu.slots.get(message.itemSlotIndex).set(ItemStack.EMPTY);
                        }
                        PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> player), new UpdateAttachmentScreenGuiContextPacket(attachments));
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
