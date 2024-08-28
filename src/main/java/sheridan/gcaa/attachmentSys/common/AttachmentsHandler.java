package sheridan.gcaa.attachmentSys.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.AttachmentsRenderContext;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.attachments.ISubSlotActivator;
import sheridan.gcaa.items.attachments.ISubSlotProvider;
import sheridan.gcaa.items.gun.IGun;

import java.util.*;


public class AttachmentsHandler {
    public static final AttachmentsHandler INSTANCE = new AttachmentsHandler();
    static final String PASSED = Attachment.PASSED;
    static final String ROOT = AttachmentSlot.ROOT;
    static final String NONE = AttachmentSlot.NONE;

    public void checkAndUpdate(ItemStack itemStack, IGun gun, Player player) {
        CompoundTag properties = gun.getGunProperties().getInitialData();
        ListTag attachments = gun.getAttachmentsListTag(itemStack);
        ListTag newAttachments = new ListTag();
        List<Item> unSupportedAttachments = new ArrayList<>();
        if (!attachments.isEmpty()) {
            AttachmentSlot root = getAttachmentBaseSlots(gun);
            root.cleanAll();
            if (root != AttachmentSlot.EMPTY) {
                for (int i = 0; i < attachments.size(); i++) {
                    CompoundTag tag = attachments.getCompound(i);
                    String id = tag.getString("id");
                    IAttachment attachment = AttachmentsRegister.get(id);
                    if (attachment != null) {
                        String slotName = tag.getString("slot_name");
                        AttachmentSlot prevSlot = root.searchChild(slotName);
                        if (!prevSlot.isLocked() && PASSED.equals(attachment.canAttach(itemStack, gun, root, prevSlot))) {
                            attachment.onAttach(itemStack, gun, properties);
                            if (attachment instanceof ISubSlotProvider provider) {
                                provider.appendSlots(prevSlot);
                            }
                            if (attachment instanceof ISubSlotActivator activator) {
                                activator.unlockSlots(prevSlot);
                            }
                            newAttachments.add(tag);
                        } else {
                            unSupportedAttachments.add(attachment.get());
                        }
                    } else {
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

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public AttachmentsRenderContext getRenderContext(ItemStack stack, IGun gun) {
        ListTag attachmentsTag = gun.getAttachmentsListTag(stack);
        if (attachmentsTag == null) {
            return null;
        }
        AttachmentsRenderContext context = new AttachmentsRenderContext();
        Map<String, AttachmentRenderEntry> entries = new HashMap<>(attachmentsTag.size());
        for (int i = 0; i < attachmentsTag.size(); i++) {
            CompoundTag attachmentTag = attachmentsTag.getCompound(i);
            IAttachment attachment = AttachmentsRegister.get(attachmentTag.getString("id"));
            if (attachment != null) {
                IAttachmentModel model = AttachmentsRegister.getModel(attachment);
                if (model != null) {
                    String slotName = attachmentTag.getString("slot_name");
                    String modelSlotName = attachmentTag.getString("model_slot_name");
                    String parentUuid = attachmentTag.getString("parent_uuid");
                    String uuid = attachmentTag.getString("uuid");
                    AttachmentRenderEntry entry = new AttachmentRenderEntry(model, attachment, slotName, modelSlotName, uuid);
                    if (!ROOT.equals(parentUuid) && !NONE.equals(parentUuid)) {
                        AttachmentRenderEntry parent = entries.get(parentUuid);
                        if (parent != null) {
                            parent.addChild(entry.modelSlotName, entry);
                        }
                        entries.put(uuid, entry);
                    } else {
                        entries.put(uuid, entry);
                        context.add(entry);
                    }
                }
            }
        }
        context.onFinish();
        return context.isEmpty() ? null : context;
    }

    private CompoundTag getMark(IAttachment attachment,String slotName, String modelSlotName, String parentUuid) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", AttachmentsRegister.getKey(attachment).toString());
        tag.putString("model_slot_name", modelSlotName);
        tag.putString("parent_uuid", parentUuid);
        tag.putString("slot_name", slotName);
        tag.putString("uuid", UUID.randomUUID().toString());
        return tag;
    }

    public void serverSetAttachment(ItemStack stack, IGun gun, IAttachment attachment, String slotName, String modelSlotName, String parentUuid)  {
        CompoundTag properties = gun.getPropertiesTag(stack);
        ListTag attachments = gun.getAttachmentsListTag(stack);
        attachment.onAttach(stack, gun, properties);
        CompoundTag mark = getMark(attachment, slotName, modelSlotName, parentUuid);
        attachments.add(mark);
        gun.setAttachmentsListTag(stack, attachments);
    }

    public ItemStack serverUninstallAttachment(ItemStack stack, IGun gun, String uuid) {
        CompoundTag properties = gun.getPropertiesTag(stack);
        ListTag attachments = gun.getAttachmentsListTag(stack);
        ItemStack stackToReturn = null;
        int index = -1;
        for (int i = 0; i < attachments.size(); i++) {
            CompoundTag tag = attachments.getCompound(i);
            if (tag.contains("uuid") && tag.getString("uuid").equals(uuid))  {
                String attachmentId = tag.getString("id");
                IAttachment attachment = AttachmentsRegister.get(attachmentId);
                if (attachment != null) {
                    attachment.onDetach(stack, gun, properties);
                    stackToReturn = new ItemStack(attachment.get());
                    index = i;
                } else {
                    stackToReturn = new ItemStack(ModItems.UNKNOWN_ATTACHMENT.get());
                    CompoundTag idTag = new CompoundTag();
                    idTag.putString("id", attachmentId);
                    stackToReturn.setTag(idTag);
                }
                break;
            }
        }
        if (index != -1) {
            attachments.remove(index);
            gun.setAttachmentsListTag(stack, attachments);
            gun.setPropertiesTag(stack, properties);
        }
        return stackToReturn;
    }

    public List<IAttachment> getAttachments(ItemStack itemStack, IGun gun) {
        ListTag tag = gun.getAttachmentsListTag(itemStack);
        List<IAttachment> attachments = new ArrayList<>();
        if (tag != null) {
            for (int i = 0; i < tag.size(); i ++) {
                CompoundTag slot = tag.getCompound(i);
                String id = slot.getString("id");
                IAttachment attachment = AttachmentsRegister.get(id);
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
            slot.cleanAll();
            if (attachmentsTag != null && !attachmentsTag.isEmpty()) {
                for (int i = 0; i < attachmentsTag.size(); i++) {
                    CompoundTag slotTag = attachmentsTag.getCompound(i);
                    String id = slotTag.getString("id");
                    String slotName = slotTag.getString("slot_name");
                    IAttachment attachment = AttachmentsRegister.get(id);
                    AttachmentSlot prevSlot = slot.searchChild(slotName);
                    if (prevSlot != null) {
                        if (attachment != null) {
                            if (attachment instanceof ISubSlotProvider provider) {
                                provider.appendSlots(prevSlot);
                            }
                            if (attachment instanceof ISubSlotActivator activator) {
                                activator.unlockSlots(prevSlot);
                            }
                            prevSlot.setAttachmentId(id);
                            prevSlot.setId(slotTag.getString("uuid"));
                        } else {
                            prevSlot.cleanAll();
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

    public AttachmentSlot getAttachmentBaseSlots(IGun gun) {
        return AttachmentSlot.copyAll(AttachmentsRegister.getAttachmentSlot(gun));
    }
}
