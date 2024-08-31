package sheridan.gcaa.client.model.attachments.grip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.model.attachments.ArmRendererModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.PlayerArmRenderer;
import sheridan.gcaa.lib.ArsenalLib;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public class VerticalGripModel extends ArmRendererModel implements IAttachmentModel {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart left_arm;
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/vertical_grip.png");

    public VerticalGripModel() {
        root = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/vertical_grip.geo.json")).bakeRoot().getChild("root");
        body = root.getChild("body").meshing();
        left_arm = root.getChild("left_arm");
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        PoseStack poseStack = context.poseStack;
        root.copyFrom(pose);
        poseStack.pushPose();
        root.translateAndRotate(poseStack);
        context.render(body, vertexConsumer);
        renderLeftArm(RenderAndMathUtils.copyPoseStack(poseStack), context);
        poseStack.popPose();
        root.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return root;
    }

    protected void renderLeftArm(PoseStack poseStack, GunRenderContext context) {
        if (!context.isFirstPerson) {
            return;
        }
        left_arm.translateAndRotate(poseStack);
        PoseStack renderPose = poseStack;
        long reloadStartTime = AnimationHandler.INSTANCE.getReloadStartTime();
        if (reloadStartTime != 0) {
            float length = AnimationHandler.INSTANCE.getReloadLengthIfHas();
            PoseStack savedPose = context.getLocalSavedPose(LEFT_ARM_RENDER_REPLACE);
            if (!Float.isNaN(length) && savedPose != null) {
                float disFromStart = RenderAndMathUtils.secondsFromNow(reloadStartTime);
                float progress = Mth.clamp(disFromStart / length, 0, 1);
                if (progress < 0.25f) {
                    renderPose = RenderAndMathUtils.lerpPoseStack(poseStack, savedPose, progress * 4);
                } else if (progress > 0.75f) {
                    renderPose = RenderAndMathUtils.lerpPoseStack(savedPose, poseStack, (progress - 0.75f) * 4);
                } else {
                    renderPose = savedPose;
                }
            }
        }
        PlayerArmRenderer.INSTANCE.renderLong(context.packedLight, context.packedOverlay, false, context.bufferSource, renderPose);
    }


}
