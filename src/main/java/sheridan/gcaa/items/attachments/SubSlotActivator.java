package sheridan.gcaa.items.attachments;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.gun.IGun;

import java.util.List;

public abstract class SubSlotActivator extends Attachment implements ISubSlotActivator{
    public SubSlotActivator(float weight) {
        super(weight);
    }

    @Override
    public AttachResult canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        List<AttachmentSlot> linkedSlots = getLinkedSlots(root);
        for (AttachmentSlot linkedSlot : linkedSlots) {
            if (!linkedSlot.isEmpty()) {
                return new AttachResult(false, () -> Component.translatable("tooltip.action_res.prevented_by_child").getString());
            }
        }
        return super.canDetach(stack, gun, root, prevSlot);
    }

    @Override
    public void unlockOrLockSlots(AttachmentSlot slot, AttachmentSlot root, IGun gun) {
        List<AttachmentSlot> linkedSlots = getLinkedSlots(root);
        for (AttachmentSlot linkedSlot : linkedSlots) {
            linkedSlot.unlock();
        }
    }

    public abstract List<AttachmentSlot> getLinkedSlots(AttachmentSlot root);
}
