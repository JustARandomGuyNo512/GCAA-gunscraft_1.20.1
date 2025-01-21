package sheridan.gcaa.items.attachments.scope;

import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.ISubSlotProvider;
import sheridan.gcaa.items.attachments.Scope;
import sheridan.gcaa.items.attachments.SubSlotProvider;
import sheridan.gcaa.items.gun.IGun;

import java.util.Set;

public class Acog extends Scope implements ISubSlotProvider {
    private static final AttachmentSlot root = AttachmentSlot.root()
            .addChild(new AttachmentSlot("sub_scope", Set.of("gcaa:micro_red_dot")).upper());


    public Acog() {
        super( 4, 1.5f, 0.9f, 1.5f);
    }

    @Override
    public void appendSlots(AttachmentSlot parent, AttachmentSlot root, IGun gun) {
        parent.addChild(AttachmentSlot.copyAll(Acog.root.getChild("sub_scope")));
    }

    @Override
    public AttachResult canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        AttachResult result = super.canDetach(stack, gun, root, prevSlot);
        return SubSlotProvider.checkForChild(result, prevSlot);
    }
}
