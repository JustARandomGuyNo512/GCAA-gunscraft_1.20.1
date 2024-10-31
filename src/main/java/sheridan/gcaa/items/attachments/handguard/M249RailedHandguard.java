package sheridan.gcaa.items.attachments.handguard;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.attachments.ISubSlotActivator;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

import java.util.List;

public class M249RailedHandguard extends Attachment implements ISubSlotActivator {
    public M249RailedHandguard() {
        super(0.9f);
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
    public void unlockOrLockSlots(AttachmentSlot slot, AttachmentSlot root) {
        List<AttachmentSlot> linkedSlots = getLinkedSlots(root);
        for (AttachmentSlot linkedSlot : linkedSlots) {
            linkedSlot.unlock();
        }
    }

    private List<AttachmentSlot> getLinkedSlots(AttachmentSlot root) {
        return List.of(root.getChild("handguard_grip"), root.getChild("handguard_left"), root.getChild("handguard_right"));
    }
}
