package sheridan.gcaa.service.product;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.attachments.IAttachment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttachmentProduct extends CommonProduct implements IRecycleProduct {
    private static final Map<Attachment, AttachmentProduct> ATTACHMENT_PRODUCT_MAP = new HashMap<>();
    public IAttachment attachment;

    public AttachmentProduct(Attachment attachment, int price) {
        super(attachment, price);
        ATTACHMENT_PRODUCT_MAP.put(attachment, this);
        this.attachment = attachment;
    }

    @Override
    public void onRemoveRegistry() {
        ATTACHMENT_PRODUCT_MAP.remove(attachment.get());
    }

    public static AttachmentProduct get(Attachment attachment) {
        return ATTACHMENT_PRODUCT_MAP.get(attachment);
    }

    @Override
    public int getMaxBuyCount() {
        return 1;
    }

    @Override
    public long getRecyclePrice(ItemStack gunStack, List<Component> tooltip) {
        tooltip.add(Component.translatable(attachment.get().getDescriptionId()).append(" = " + getDefaultPrice()));
        return getDefaultPrice();
    }

    @Override
    public IProduct get() {
        return this;
    }
}