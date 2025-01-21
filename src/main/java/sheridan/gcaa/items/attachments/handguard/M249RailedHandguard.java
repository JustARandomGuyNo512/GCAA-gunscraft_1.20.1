package sheridan.gcaa.items.attachments.handguard;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.attachments.ISubSlotActivator;
import sheridan.gcaa.items.attachments.SubSlotActivator;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

import java.util.List;

public class M249RailedHandguard extends SubSlotActivator {
    public M249RailedHandguard() {
        super(0.9f);
    }

    public List<AttachmentSlot> getLinkedSlots(AttachmentSlot root) {
        return List.of(root.getChild("handguard_grip"), root.getChild("handguard_left"), root.getChild("handguard_right"));
    }
}
