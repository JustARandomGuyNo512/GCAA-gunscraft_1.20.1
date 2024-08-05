package sheridan.gcaa.attachmentSys.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Commons;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.attachments.ISubSlotActivator;
import sheridan.gcaa.items.attachments.ISubSlotProvider;
import sheridan.gcaa.items.gun.IGun;

import java.util.ArrayList;
import java.util.List;


public class AttachmentsHandler {
    public static final AttachmentsHandler INSTANCE = new AttachmentsHandler();
    static final String REJECTED = IAttachment.REJECTED, PASSED = IAttachment.PASSED;

    public void checkAndUpdate(ItemStack itemStack, IGun gun, Player player) {
        CompoundTag properties = gun.getGunProperties().getInitialData();
        ListTag attachments = gun.getAttachmentsListTag(itemStack);
        ListTag newAttachments = new ListTag();
        List<Item> unSupportedAttachments = new ArrayList<>();
        if (!attachments.isEmpty()) {
            AttachmentSlot root = getAttachmentBaseSlots(gun);
            root.clean();
            if (root != AttachmentSlot.EMPTY) {
                for (int i = 0; i < attachments.size(); i++) {
                    CompoundTag tag = attachments.getCompound(i);
                    String id = tag.getString("id");
                    IAttachment attachment = AttachmentRegister.get(id);
                    if (attachment != null) {
                        String slotName = tag.getString("slot_name");
                        AttachmentSlot prevSlot = root.searchChild(slotName);
                        if (!prevSlot.isLocked() && PASSED.equals(attachment.canAttach(itemStack, gun, root, prevSlot))) {
                            attachment.onAttach(itemStack, gun, properties);
                            if (attachment instanceof ISubSlotProvider provider) {
                                provider.appendSlots(prevSlot);
                            } else if (attachment instanceof ISubSlotActivator activator) {
                                activator.unlockSlots(prevSlot);
                            }
                            newAttachments.add(tag);
                        } else {
                            unSupportedAttachments.add(attachment.get());
                        }
                    } else {
                        //unknown attachment give UNKNOWN_ATTACHMENT item to player.
                        CompoundTag idTag = new CompoundTag();
                        idTag.putString("id", id);
                        ItemStack stack = new ItemStack(ModItems.UNKNOWN_ATTACHMENT.get());
                        stack.setTag(idTag);
                        if (!player.addItem(stack)) {
                            ItemEntity entity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
                            player.level().addFreshEntity(entity);
                        }
                    }

                }
            }
        }
        gun.setAttachmentsListTag(itemStack, newAttachments);
        gun.setPropertiesTag(itemStack, properties);
        gun.updateDate(itemStack);
        for (Item item : unSupportedAttachments) {
            ItemStack stack = new ItemStack(item);
            if (!player.addItem(stack)) {
                ItemEntity entity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
                player.level().addFreshEntity(entity);
            }
        }
    }


    private CompoundTag getMark(IAttachment attachment,String slotName, String modelSlotName, String parentSlot) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", AttachmentRegister.getKey(attachment).toString());
        tag.putString("model_slot_name", modelSlotName);
        tag.putString("parent_slot", parentSlot);
        tag.putString("slot_name", slotName);
        return tag;
    }

    public void serverSetAttachment(ItemStack stack, IGun gun, IAttachment attachment, String slotName, String modelSlotName, String parentSlot)  {
        CompoundTag properties = gun.getPropertiesTag(stack);
        ListTag attachments = gun.getAttachmentsListTag(stack);
        attachment.onAttach(stack, gun, properties);
        CompoundTag mark = getMark(attachment, slotName, modelSlotName, parentSlot);
        attachments.add(mark);
        gun.setAttachmentsListTag(stack, attachments);
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

    public AttachmentSlot getAttachmentSlots(ListTag attachmentsTag, IGun gun) {
        AttachmentSlot slot = getAttachmentBaseSlots(gun);
        if (slot != AttachmentSlot.EMPTY) {
            slot.clean();
            if (attachmentsTag != null && !attachmentsTag.isEmpty()) {
                for (int i = 0; i < attachmentsTag.size(); i++) {
                    CompoundTag slotTag = attachmentsTag.getCompound(i);
                    String id = slotTag.getString("id");
                    String slotName = slotTag.getString("slot_name");
                    IAttachment attachment = AttachmentRegister.get(id);
                    AttachmentSlot prevSlot = slot.searchChild(slotName);
                    if (prevSlot != null) {
                        if (attachment != null) {
                            if (attachment instanceof ISubSlotProvider provider) {
                                provider.appendSlots(prevSlot);
                            } else if (attachment instanceof ISubSlotActivator activator) {
                                activator.unlockSlots(prevSlot);
                            }
                            prevSlot.setAttachmentId(id);
                        } else {
                            prevSlot.clean();
                        }
                    }
                }
            }
        }
        return slot;
    }

    public AttachmentSlot getAttachmentSlots(ItemStack itemStack) {
        if (itemStack.getItem() instanceof IGun gun) {
            return getAttachmentSlots(gun.getAttachmentsListTag(itemStack), gun);
        }
        return AttachmentSlot.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    public AttachmentSlot getAttachmentBaseSlots(IGun gun) {
        return AttachmentRegister.getAttachmentSlot(gun).copy();
    }
}
