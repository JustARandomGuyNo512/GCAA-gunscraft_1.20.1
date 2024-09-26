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
        context.pushPose();
        initTranslation(attachmentRenderEntry, context, pose);
        renderModel(context, attachmentRenderEntry, pose);
        context.popPose();
    }

    protected abstract void renderModel(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose);

    @Override
    public byte getDirection() {
        return UPPER;
    }
}
