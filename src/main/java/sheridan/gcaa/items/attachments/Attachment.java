package sheridan.gcaa.items.attachments;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.items.AutoRegister;
import sheridan.gcaa.items.NoRepairNoEnchantmentItem;
import sheridan.gcaa.items.gun.IGun;

import java.util.Map;
import java.util.Set;

public abstract class Attachment extends NoRepairNoEnchantmentItem implements IAttachment, AutoRegister {
    public static final String REJECTED = "rejected", PASSED = "passed";
    public static final String MUZZLE = "muzzle";
    public static final String MAG = "mag";
    public static final String HANDGUARD = "handguard";
    public static final String STOCK = "stock";
    public static final String GRIP = "grip";
    public static final String SCOPE = "scope";
    public Attachment() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public String canAttach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        return prevSlot.isEmpty() && prevSlot.acceptsAttachment(AttachmentsRegister.getStrKey(this)) ? PASSED : REJECTED;
    }

    @Override
    public String canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        return PASSED;
    }

    @Override
    public void clientRegister(Map.Entry<ResourceKey<Item>, Item> entry) {
        AttachmentsRegister.register(entry);
    }

    @Override
    public Attachment get() {
        return this;
    }

    @Override
    public void serverRegister(Map.Entry<ResourceKey<Item>, Item> entry) {
        AttachmentsRegister.register(entry);
    }


    protected String checkConflict(String originalMessage, AttachmentSlot root, AttachmentSlot checkAim, Set<IAttachment> conflicts)  {
        if (checkAim != null && !checkAim.isEmpty()) {
            String message = Component.translatable("tooltip.action_res.conflict").getString();
            IAttachment attachment = AttachmentsRegister.get(checkAim.getAttachmentId());
            if (attachment != null) {
                if (conflicts.isEmpty()) {
                    message = message.replace("$id", Component.translatable(attachment.get().getDescriptionId()).getString());
                    return message;
                } else if (conflicts.contains(attachment)) {
                    message = message.replace("$id", Component.translatable(attachment.get().getDescriptionId()).getString());
                    return message;
                }
            }
        }
        return  originalMessage;
    }
}
