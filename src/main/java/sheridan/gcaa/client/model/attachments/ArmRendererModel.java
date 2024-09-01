package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.PlayerArmRenderer;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public abstract class ArmRendererModel {
    protected static final String LEFT_ARM_RENDER_REPLACE = GunRenderContext.LEFT_ARM_RENDER_REPLACE;
    protected static final String RIGHT_ARM_RENDER_REPLACE = GunRenderContext.RIGHT_ARM_RENDER_REPLACE;

    protected abstract ModelPart getLeftArm(GunRenderContext context);
    protected abstract ModelPart getRightArm(GunRenderContext context);
    protected abstract PoseStack lerpArmPose(boolean mainHand, PoseStack prevPose, GunRenderContext context);
    protected abstract boolean shouldRenderArm(boolean mainHand, GunRenderContext context, AttachmentRenderEntry entry);

    protected void renderArm(boolean mainHand, PoseStack poseStack, GunRenderContext context, AttachmentRenderEntry entry) {
        if (!shouldRenderArm(mainHand, context, entry)) {
            return;
        }
        ModelPart arm = mainHand ? getRightArm(context) : getLeftArm(context);
        if (arm == null) {
            return;
        }
        arm.translateAndRotate(poseStack);
        PoseStack renderPose = lerpArmPose(mainHand, poseStack, context);
        if (context.renderLongArm) {
            PlayerArmRenderer.INSTANCE.renderLong(context.packedLight, context.packedOverlay, false, context.bufferSource, renderPose);
            return;
        }
        PlayerArmRenderer.INSTANCE.render(context.packedLight, context.packedOverlay, false, context.bufferSource, renderPose);
    }

    protected boolean defaultShouldRenderArm(boolean mainHand, GunRenderContext context, AttachmentRenderEntry entry) {
        if (!context.isFirstPerson) {
            return false;
        }
        AttachmentSlot slot = mainHand ? Clients.mainHandStatus.getRightArmReplace() : Clients.mainHandStatus.getLeftArmReplace();
        if (slot == null) {
            return false;
        }
        return entry.slotUUID.equals(slot.getId());
    }

    protected PoseStack LerpReloadAnimationPose(boolean mainHand, GunRenderContext context, PoseStack poseStack) {
        return LerpAnimationPose(
                AnimationHandler.RELOAD,
                poseStack,
                context.getLocalSavedPose(mainHand ? RIGHT_ARM_RENDER_REPLACE : LEFT_ARM_RENDER_REPLACE),
                0.1f, 0.9f, 0.2f, 0.2f);
    }

    protected PoseStack LerpAnimationPose(String channel, PoseStack origin, PoseStack to,
                                          float enterLerp, float exitLerp, float enterLerpSeconds, float exitLerpSeconds)  {
        long reloadStartTime = AnimationHandler.INSTANCE.getStartTime(channel);
        if (reloadStartTime != 0) {
            enterLerp = Mth.clamp(enterLerp, 0, exitLerp);
            exitLerp = Mth.clamp(exitLerp, enterLerp, 1);
            float length = AnimationHandler.INSTANCE.getLengthIfHas(channel);
            if (!Float.isNaN(length) && to != null) {
                if (enterLerp * length > enterLerpSeconds) {
                    enterLerp = enterLerpSeconds / length;
                }
                if ((1 - exitLerp) * length > exitLerpSeconds) {
                    exitLerp = 1 - exitLerpSeconds / length;
                }
                float disFromStart = RenderAndMathUtils.secondsFromNow(reloadStartTime);
                float progress = Mth.clamp(disFromStart / length, 0, 1);
                if (progress < enterLerp) {
                    return RenderAndMathUtils.lerpPoseStack(origin, to, progress * (1 / enterLerp));
                } else if (progress > exitLerp) {
                    return RenderAndMathUtils.lerpPoseStack(to, origin, (progress - exitLerp) * (1 / (1 - exitLerp)));
                } else {
                    return to;
                }
            }
        }
        return origin;
    }
}
