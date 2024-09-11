package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public abstract class SightModel implements IAttachmentModel, IDirectionalModel {
    public abstract void handleCrosshairTranslation(PoseStack poseStack);
    public abstract ModelPart getCrosshair();

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        //ModelPart root = root();
        context.pushPose();
        //root.copyFrom(pose);
        //context.pushPose().translateTo(root);
        initTranslation(attachmentRenderEntry, context, pose);
        renderModel(context, attachmentRenderEntry, pose);
        context.popPose();
        //root.resetPose();
    }

    protected abstract void renderModel(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose);

    @Override
    public byte getDirection() {
        return UPPER;
    }

    @Override
    public ModelPart root() {
        return getRoot();
    }
}
