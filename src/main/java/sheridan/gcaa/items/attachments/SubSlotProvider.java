package sheridan.gcaa.items.attachments;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.gun.IGun;

public abstract class SubSlotProvider extends Attachment implements ISubSlotProvider {

    @Override
    public AttachResult canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        AttachResult res = super.canDetach(stack, gun, root, prevSlot);
        if (res.isPassed()) {
            for (AttachmentSlot child : prevSlot.getChildren().values()) {
                if (!child.isEmpty()) {
                    return new AttachResult(false, () -> Component.translatable("tooltip.action_res.prevented_by_child").getString());
                }
            }
        }
        return res;
    }
}
