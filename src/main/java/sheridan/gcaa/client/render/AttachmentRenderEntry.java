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
    public Map<String, AttachmentRenderEntry> children = null;

    public AttachmentRenderEntry(IAttachmentModel model, IAttachment attachment, String slotName, String modelSlotName) {
        this.model = model;
        this.attachment = attachment;
        this.slotName = slotName;
        this.modelSlotName = modelSlotName;
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
        return children != null;
    }

    public void render(GunRenderContext context) {
        context.poseStack.pushPose();
        model.render(context, this);
        context.poseStack.popPose();
    }

    public void render(GunRenderContext context, ModelPart posePart) {
        context.poseStack.pushPose();
        model.render(context, this, posePart);
        context.poseStack.popPose();
    }
}
