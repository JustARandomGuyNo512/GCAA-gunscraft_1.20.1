package sheridan.gcaa.client.render;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.items.attachments.IAttachment;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AttachmentRenderEntry {
    public IAttachmentModel model;
    public IAttachment attachment;
    public String slotName;
    public String modelSlotName;
    public String slotUUID;
    public byte direction;
    public Map<String, AttachmentRenderEntry> children = null;
    public boolean rendered = false;

    public AttachmentRenderEntry(IAttachmentModel model, IAttachment attachment, String slotName, String modelSlotName, String slotUUID, byte direction)  {
        this.model = model;
        this.attachment = attachment;
        this.slotName = slotName;
        this.modelSlotName = modelSlotName;
        this.slotUUID = slotUUID;
        this.direction = direction;
    }

    public void addChild(String modelSlotName, AttachmentRenderEntry child) {
        if (child == null) {
            return;
        }
        if (children == null) {
            children = new HashMap<>();
        }
        children.put(modelSlotName, child);
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public void reset() {
        rendered = false;
    }

    public AttachmentRenderEntry getChild(String modelSlotName) {
        return hasChildren() ? children.get(modelSlotName) : null;
    }


    public void render(GunRenderContext context, ModelPart posePart) {
        if (model == IAttachmentModel.EMPTY) {
            return;
        }
        model.render(context, this, posePart);
        rendered = true;
    }
}
