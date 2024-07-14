package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.items.attachments.IAttachment;

@OnlyIn(Dist.CLIENT)
public class AttachmentRenderEntry {
    public IAttachmentModel model;
    public IAttachment attachment;
    public String slotName;


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
