package sheridan.gcaa.items.attachments.replaceableParts;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.ReplaceableGunPart;
import sheridan.gcaa.items.gun.IGun;

import java.util.HashSet;
import java.util.Set;

public class Mk47Handguard extends ReplaceableGunPart {
    private static final Set<String> slots = new HashSet<>();
    public Mk47Handguard() {
        super(1.5f);
        slots.add("handguard_scope");
        slots.add("handguard_lower");
        slots.add("handguard_grip");
        slots.add("handguard_left");
        slots.add("handguard_left_rear");
        slots.add("handguard_right");
        slots.add("handguard_right_rear");
    }

    @Override
    protected AttachResult canReplace(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        for (String s : slots) {
            AttachmentSlot slot = root.getChild(s);
            if (slot != null && !slot.isEmpty()) {
                return new AttachResult(false, () -> Component.translatable("tooltip.action_res.prevented_by_child").getString());
            }
        }
        return super.canReplace(stack, gun, root, prevSlot);
    }
}
