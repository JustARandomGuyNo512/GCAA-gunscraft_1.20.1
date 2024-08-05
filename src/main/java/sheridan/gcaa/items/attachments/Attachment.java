package sheridan.gcaa.items.attachments;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.attachmentSys.common.AttachmentRegister;
import sheridan.gcaa.items.AutoRegister;
import sheridan.gcaa.items.BaseItem;

import java.util.Map;

public abstract class Attachment extends BaseItem implements IAttachment, AutoRegister {
    public static final String REJECTED = "rejected", PASSED = "passed";
    public Attachment() {

    }

    @Override
    public void clientRegister(Map.Entry<ResourceKey<Item>, Item> entry) {
        AttachmentRegister.register(entry);
    }

    @Override
    public void serverRegister(Map.Entry<ResourceKey<Item>, Item> entry) {
        AttachmentRegister.register(entry);
    }
}
