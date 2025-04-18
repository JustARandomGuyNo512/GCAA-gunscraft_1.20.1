package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public abstract class SightModel implements IAttachmentModel, IDirectionalModel {
    public abstract void handleCrosshairTranslation(PoseStack poseStack);
    public abstract ModelPart getCrosshair();

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        context.pushPose();
        initTranslation(attachmentRenderEntry, context, pose);
        renderModel(context, attachmentRenderEntry, pose);
        context.popPose();
    }

    public abstract float handleMinZTranslation(PoseStack poseStack);

    Vector3f v1 = new Vector3f();
    Vector3f v2 = new Vector3f();
    protected float defaultHandleMinZTranslation(PoseStack poseStack, ModelPart back_glass, ModelPart min_z_dis) {
        PoseStack near = RenderAndMathUtils.copyPoseStack(poseStack);
        back_glass.translateAndRotate(near);
        float zStart = near.last().pose().getTranslation(v1).z;
        min_z_dis.translateAndRotate(poseStack);
        return poseStack.last().pose().getTranslation(v2).z - zStart;
    }

    protected abstract void renderModel(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose);

    @Override
    public byte getDirection() {
        return UPPER;
    }
}
