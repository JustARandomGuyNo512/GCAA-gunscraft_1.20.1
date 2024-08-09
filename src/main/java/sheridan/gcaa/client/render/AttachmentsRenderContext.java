package sheridan.gcaa.client.render;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.attachments.Scope;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class AttachmentsRenderContext {
    public Map<String, AttachmentRenderEntry> modelSlotLayer = new HashMap<>();
    public Map<String, AttachmentRenderEntry> slotLayer = new HashMap<>();
    public Set<AttachmentRenderEntry> allEntries = new HashSet<>();
    public AttachmentRenderEntry scopeEntry = null;

    public void add(AttachmentRenderEntry attachmentRenderEntry) {
        allEntries.add(attachmentRenderEntry);
        if (attachmentRenderEntry.attachment instanceof Scope scope) {
            if (scopeEntry == null) {
                scopeEntry = attachmentRenderEntry;
                return;
            } else {
                if (scope.getOrder() > ((Scope) scopeEntry.attachment).getOrder()) {
                    scopeEntry = attachmentRenderEntry;
                    return;
                }
            }
        }
        modelSlotLayer.put(attachmentRenderEntry.modelSlotName, attachmentRenderEntry);
        slotLayer.put(attachmentRenderEntry.slotName, attachmentRenderEntry);
    }

    public void reset() {
        for (AttachmentRenderEntry entry : allEntries) {
            entry.reset();
        }
    }

    public void onFinish() {

    }

    public boolean isEmpty() {
        return modelSlotLayer.isEmpty() && slotLayer.isEmpty();
    }

    public boolean has(String slotName) {
        if (Attachment.SCOPE.equals(slotName)) {
            return hasScope();
        }
        return slotLayer.containsKey(slotName);
    }

    public boolean hasMuzzle() {
        return has(Attachment.MUZZLE);
    }

    public boolean hasStock() {
        return has(Attachment.STOCK);
    }

    public boolean hasScope() {
        return scopeEntry != null;
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

    public void renderAll(GunRenderContext context) {
        for (AttachmentRenderEntry entry : modelSlotLayer.values()) {
            if (!entry.rendered) {
                entry.render(context);
            }
        }
    }
}
