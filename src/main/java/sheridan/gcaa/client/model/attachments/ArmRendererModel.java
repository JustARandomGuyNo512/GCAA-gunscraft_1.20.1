package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.PlayerArmRenderer;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public abstract class ArmRendererModel {
    protected static final String LEFT_ARM_RENDER_REPLACE = GunRenderContext.LEFT_ARM_RENDER_REPLACE;
    protected static final String RIGHT_ARM_RENDER_REPLACE = GunRenderContext.RIGHT_ARM_RENDER_REPLACE;

    protected abstract ModelPart getLeftArm();
    protected abstract ModelPart getRightArm();
    protected abstract PoseStack lerpArmPose(boolean mainHand, PoseStack prevPose, GunRenderContext context);

    protected void renderArm(boolean mainHand, PoseStack poseStack, GunRenderContext context) {
        if (!context.isFirstPerson) {
            return;
        }
        ModelPart arm = mainHand ? getRightArm() : getLeftArm();
        if (arm == null) {
            return;
        }
        arm.translateAndRotate(poseStack);
        PoseStack renderPose = lerpArmPose(mainHand, poseStack, context);
        PlayerArmRenderer.INSTANCE.renderLong(context.packedLight, context.packedOverlay, false, context.bufferSource, renderPose);
    }

    protected PoseStack LerpReloadAnimationPose(boolean mainHand, GunRenderContext context, PoseStack poseStack) {
        return LerpAnimationPose(
                AnimationHandler.RELOAD,
                poseStack,
                context.getLocalSavedPose(mainHand ? RIGHT_ARM_RENDER_REPLACE : LEFT_ARM_RENDER_REPLACE),
                0.25f, 0.75f);
    }

    protected PoseStack LerpAnimationPose(String channel, PoseStack origin, PoseStack to, float enterLength, float exitLength) {
        long reloadStartTime = AnimationHandler.INSTANCE.getStartTime(channel);
        if (reloadStartTime != 0) {
            enterLength = Mth.clamp(enterLength, 0, exitLength);
            exitLength = Mth.clamp(exitLength, enterLength, 1);
            float length = AnimationHandler.INSTANCE.getLengthIfHas(channel);
            if (!Float.isNaN(length) && to != null) {
                float disFromStart = RenderAndMathUtils.secondsFromNow(reloadStartTime);
                float progress = Mth.clamp(disFromStart / length, 0, 1);
                if (progress < enterLength) {
                    return RenderAndMathUtils.lerpPoseStack(origin, to, progress * (1 / enterLength));
                } else if (progress > exitLength) {
                    return RenderAndMathUtils.lerpPoseStack(to, origin, (progress - exitLength) * (1 / (1 - exitLength)));
                } else {
                    return to;
                }
            }
        }
        return origin;
    }
}
