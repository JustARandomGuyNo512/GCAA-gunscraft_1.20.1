package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public abstract class MuzzleFlashRendererModel implements IAttachmentModel{

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        context.pushPose();
        ModelPart root = getRoot();
        root.copyFrom(pose);
        renderModel(context, attachmentRenderEntry, root);
        root.translateAndRotate(context.poseStack);
        handleMuzzleTranslate(context.poseStack);
        renderMuzzleFlash(context);
        root.resetPose();
        context.popPose();
    }

    public abstract void renderModel(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose);

    public abstract void handleMuzzleTranslate(PoseStack poseStack);

    public abstract void renderMuzzleFlash(GunRenderContext context);

    protected void defaultRenderMuzzleFlash(GunRenderContext context, float scaleModify) {
        context.renderMuzzleFlash(scaleModify);
        context.clearMuzzleFlashEntry();
    }

    @Override
    public Direction getDirection() {
        return Direction.NO_DIRECTION;
    }
}
