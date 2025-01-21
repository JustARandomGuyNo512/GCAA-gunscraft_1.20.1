package sheridan.gcaa.items.attachments.scope;

import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.ISubSlotProvider;
import sheridan.gcaa.items.attachments.Scope;
import sheridan.gcaa.items.attachments.SubSlotProvider;
import sheridan.gcaa.items.gun.IGun;

import java.util.Set;

public class ScopeX10 extends Scope implements ISubSlotProvider {
    private static final AttachmentSlot root = AttachmentSlot.root()
            .addChild(new AttachmentSlot("sub_scope", Set.of("gcaa:micro_red_dot")).upper());
    public ScopeX10() {
        super(10, 5, 0.75f, 2f);
    }

    @Override
    public void appendSlots(AttachmentSlot parent, AttachmentSlot root, IGun gun) {
        parent.addChild(AttachmentSlot.copyAll(ScopeX10.root.getChild("sub_scope")));
    }

    @Override
    public AttachResult canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        AttachResult result = super.canDetach(stack, gun, root, prevSlot);
        return SubSlotProvider.checkForChild(result, prevSlot);
    }
}
