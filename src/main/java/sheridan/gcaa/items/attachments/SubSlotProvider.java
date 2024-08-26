package sheridan.gcaa.items.attachments;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.gun.IGun;

public abstract class SubSlotProvider extends Attachment implements ISubSlotProvider {

    @Override
    public String canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        String res = super.canDetach(stack, gun, root, prevSlot);
        boolean detach = PASSED.equals(res);
        if (detach) {
            for (AttachmentSlot child : prevSlot.getChildren().values()) {
                if (!child.isEmpty()) {
                    return Component.translatable("tooltip.action_res.prevented_by_child").getString();
                }
            }
        }
        return res;
    }
}