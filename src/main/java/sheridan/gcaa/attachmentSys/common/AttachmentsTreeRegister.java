package sheridan.gcaa.attachmentSys.common;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import sheridan.gcaa.attachmentSys.client.AttachmentSlot;
import sheridan.gcaa.items.guns.IGun;

import java.util.Map;

public class AttachmentsTreeRegister {
    public static final AttachmentSlot EMPTY = new AttachmentSlot();
    private static final Map<IGun, AttachmentSlot> GUN_ATTACHMENT_SLOT_MAP = new Object2ObjectArrayMap<>();

    public static void register(IGun gun, AttachmentSlot attachmentSlot) {
        GUN_ATTACHMENT_SLOT_MAP.put(gun, attachmentSlot);
    }

    public static AttachmentSlot getAttachmentTreeBase(IGun gun) {
        AttachmentSlot slot = GUN_ATTACHMENT_SLOT_MAP.get(gun);
        return slot == null ? EMPTY : slot;
    }

    public static AttachmentSlot getAttachmentTree(IGun gun) {
        AttachmentSlot slot = GUN_ATTACHMENT_SLOT_MAP.get(gun);
        if (slot != null) {
            return slot.copy();
        }
        return EMPTY.copy();
    }
}
