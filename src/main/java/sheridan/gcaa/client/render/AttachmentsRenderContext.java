package sheridan.gcaa.client.render;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.attachments.Attachment;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AttachmentsRenderContext {
    public Map<String, AttachmentRenderEntry> modelSlotLayer = new HashMap<>();
    public Map<String, AttachmentRenderEntry> slotLayer = new HashMap<>();

    public void add(AttachmentRenderEntry attachmentRenderEntry) {
        modelSlotLayer.put(attachmentRenderEntry.modelSlotName, attachmentRenderEntry);
        slotLayer.put(attachmentRenderEntry.slotName, attachmentRenderEntry);
    }

    public boolean isEmpty() {
        return modelSlotLayer.isEmpty() && slotLayer.isEmpty();
    }

    public boolean has(String slotName) {
        return slotLayer.containsKey(slotName);
    }

    public boolean hasMuzzle() {
        return has(Attachment.MUZZLE);
    }

    public boolean hasStock() {
        return has(Attachment.STOCK);
    }

    public boolean hasScope() {
        return has(Attachment.SCOPE);
    }

    public boolean hasGrip() {
        return has(Attachment.GRIP);
    }

    public boolean hasHandguard() {
        return has(Attachment.HANDGUARD);
    }

    public boolean hasMag() {
        return has(Attachment.MAG);
    }
}
