package sheridan.gcaa.network.packets.c2s;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.UpdateAttachmentScreenGuiContext;

import java.util.function.Supplier;

public class UninstallAttachmentPacket implements IPacket<UninstallAttachmentPacket> {
    public String uuid;

    public UninstallAttachmentPacket() {}

    public UninstallAttachmentPacket(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public void encode(UninstallAttachmentPacket message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.uuid);
    }

    @Override
    public UninstallAttachmentPacket decode(FriendlyByteBuf buffer) {
        return new UninstallAttachmentPacket(buffer.readUtf());
    }

    @Override
    public void handle(UninstallAttachmentPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                ItemStack heldItem = player.getMainHandItem();
                if (heldItem.getItem() instanceof IGun gun) {
                    ItemStack stackToReturn = AttachmentsHandler.INSTANCE.serverUninstallAttachment(heldItem, gun, message.uuid);
                    if (stackToReturn != null) {
                        if (!player.addItem(stackToReturn)) {
                            ItemEntity entity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stackToReturn);
                            player.level().addFreshEntity(entity);
                        }
                    }
                    ListTag attachments = gun.getAttachmentsListTag(heldItem);
                    PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> player), new UpdateAttachmentScreenGuiContext(attachments));
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}