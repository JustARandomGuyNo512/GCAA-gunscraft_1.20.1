package sheridan.gcaa.attachmentSys.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Commons;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.attachmentSys.client.AttachmentSlot;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.attachments.ISubSlotActivator;
import sheridan.gcaa.items.attachments.ISubSlotProvider;
import sheridan.gcaa.items.guns.IGun;

import java.util.ArrayList;
import java.util.List;

public class AttachmentsHandler {
    public static final AttachmentsHandler INSTANCE = new AttachmentsHandler();

    public void checkAndUpdate(ItemStack itemStack) {
        if (itemStack.getItem() instanceof IGun gun) {
            CompoundTag nbt = itemStack.getTag();
            if (nbt == null || !nbt.contains("inner_version")) {
                GCAA.LOGGER.info("Can't find data version in NBT, updating stopped");
                return;
            }
            int innerVersion = nbt.getInt("inner_version");
            if (gun.shouldUpdate(innerVersion)) {
                //check attachments map
                //recalculate the data
                //send packet to client

            } else {
                CompoundTag prevProperties = gun.getPropertiesTag(itemStack);
                long date = prevProperties.contains("date") ? prevProperties.getLong("date") : -1;
                if (date != Commons.SERVER_START_TIME) {
                    //recalculate the data
                    ListTag listTag = gun.getAttachmentsListTag(itemStack);
                    if (listTag.size() == 0) {
                        return;
                    }
                    CompoundTag whiteData = gun.getGunProperties().getInitialData();
                    for (int i = 0; i < listTag.size(); i++) {
                        CompoundTag tag = listTag.getCompound(i);
                        String id = tag.getString("id");
                        IAttachment attachment = AttachmentRegister.get(id);
                        if(attachment != null) {
                            attachment.onAttach(itemStack, gun, whiteData);
                        }
                    }
                    gun.setPropertiesTag(itemStack, whiteData);
                }

            }
        }
    }

    private CompoundTag getMark(IAttachment attachment,String slotName) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", AttachmentRegister.getKey(attachment).toString());
        tag.putString("slot_name", slotName);
        return tag;
    }

    public void setAttachment(ItemStack stack, IAttachment attachment, ListTag translation, String slotName) {

    }

    public List<IAttachment> getAttachments(ItemStack itemStack, IGun gun) {
        ListTag tag = gun.getAttachmentsListTag(itemStack);
        List<IAttachment> attachments = new ArrayList<>();
        if (tag != null) {
            for (int i = 0; i < tag.size(); i ++) {
                CompoundTag slot = tag.getCompound(i);
                String id = slot.getString("id");
                IAttachment attachment = AttachmentRegister.get(id);
                if(attachment != null) {
                    attachments.add(attachment);
                }
            }
        }
        return attachments;
    }

    @OnlyIn(Dist.CLIENT)
    public AttachmentSlot getAttachmentSlots(ItemStack itemStack) {
        if (itemStack.getItem() instanceof IGun gun) {
            AttachmentSlot slot = getAttachmentBaseSlots(gun);
            if (slot != AttachmentSlot.EMPTY) {
                ListTag tag = gun.getAttachmentsListTag(itemStack);
                if (tag != null && !tag.isEmpty()) {
                    for (int i = 0; i < tag.size(); i++) {
                        CompoundTag slotTag = tag.getCompound(i);
                        String id = slotTag.getString("id");
                        String slotName = slotTag.getString("slot_name");
                        IAttachment attachment = AttachmentRegister.get(id);
                        if (attachment instanceof ISubSlotProvider provider) {
                            AttachmentSlot parent = slot.searchChild(slotName);
                            if (parent != null) {
                                AttachmentSlot subSlot = provider.getSubSlot();
                                parent.addChild(subSlot);
                            }
                        } else if (attachment instanceof ISubSlotActivator activator) {
                            AttachmentSlot s = slot.searchChild(slotName);
                            if (s != null) {
                                activator.doUnlock(s);
                            }
                        }
                    }
                }
            }
            return slot;
        }
        return AttachmentSlot.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    public AttachmentSlot getAttachmentBaseSlots(IGun gun) {
        return AttachmentRegister.getAttachmentSlot(gun).copy();
    }
}
