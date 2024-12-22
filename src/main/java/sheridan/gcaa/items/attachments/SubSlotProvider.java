package sheridan.gcaa.items.attachments;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.gun.IGun;

public abstract class SubSlotProvider extends Attachment implements ISubSlotProvider {

    public SubSlotProvider(float weight) {
        super(weight);
    }

    public static AttachResult checkForChild(AttachResult prevRes, AttachmentSlot prevSlot) {
        if (prevRes.isPassed()) {
            for (AttachmentSlot child : prevSlot.getChildren().values()) {
                if (!child.isEmpty()) {
                    return new AttachResult(false, () -> Component.translatable("tooltip.action_res.prevented_by_child").getString());
                }
            }
        }
        return prevRes;
    }

    @Override
    public AttachResult canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        AttachResult res = super.canDetach(stack, gun, root, prevSlot);
        return checkForChild(res, prevSlot);
    }
}
