package sheridan.gcaa.client.model.attachments.grip;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.ArmRendererModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.IDirectionalModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public class CommonGripModel extends ArmRendererModel implements IAttachmentModel, IDirectionalModel {
    protected ModelPart root;
    protected ModelPart body;
    protected ModelPart left_arm;
    protected ModelPart left_arm_rifle;
    protected ModelPart low;
    protected ResourceLocation texture;
    protected ResourceLocation texture_low;

    public CommonGripModel() {
        init();
    }

    protected void init() {}


    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        context.pushPose();
        initTranslation(attachmentRenderEntry, context, pose);
        if (context.useLowQuality()) {
            low.copyFrom(body);
            context.render(low, context.getBuffer(RenderType.entityCutout(texture_low)));
        } else {
            context.render(body, context.getBuffer(RenderType.entityCutout(texture)));
        }
        renderArm(false, RenderAndMathUtils.copyPoseStack(context.poseStack), context, attachmentRenderEntry);
        context.popPose();
    }

    @Override
    public ModelPart getRoot() {
        return root;
    }

    @Override
    protected ModelPart getLeftArm(GunRenderContext context) {
        return context.renderArmNew ? left_arm_rifle : left_arm;
    }

    @Override
    protected ModelPart getRightArm(GunRenderContext context) {
        return null;
    }

    @Override
    protected PoseStack lerpArmPose(boolean mainHand, PoseStack prevPose, GunRenderContext context) {
        return LerpReloadAnimationPose(false, context, prevPose);
    }

    @Override
    protected boolean shouldRenderArm(boolean mainHand, GunRenderContext context, AttachmentRenderEntry entry) {
        return defaultShouldRenderArm(mainHand, context, entry);
    }

    @Override
    public byte getDirection() {
        return LOWER;
    }

    @Override
    public ModelPart root() {
        return getRoot();
    }
}
