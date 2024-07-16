package sheridan.gcaa.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RenderAndMathUtils {
    public static float sLerp(float progress) {
        float f1 = progress * progress;
        float f2 = 1.0f - (1.0f - progress) * (1.0f - progress);
        return Mth.lerp(progress, f1, f2);
    }

    public static PoseStack lerpPoseStack(PoseStack from, PoseStack to, float progress) {
        PoseStack res = new PoseStack();
        lerpPoseStack(from, to, res, progress, true, true, true);
        return res;
    }

    public static void lerpPoseStack(PoseStack from, PoseStack to, PoseStack res, float progress, boolean translation, boolean rotation, boolean scale) {
        if (translation || rotation || scale) {
            Matrix4f fromPose = from.last().pose();
            Matrix4f toPose = to.last().pose();
            if (translation) {
                Vector3f fromTranslation = fromPose.getTranslation(new Vector3f(0,0,0));
                Vector3f toTranslation = toPose.getTranslation(new Vector3f(0,0,0));
                res.translate(fromTranslation.x + (toTranslation.x - fromTranslation.x) * progress,
                        fromTranslation.y + (toTranslation.y - fromTranslation.y) * progress,
                        fromTranslation.z + (toTranslation.z - fromTranslation.z) * progress);
            }
            if (rotation) {
                Quaternionf fromRotation = fromPose.getNormalizedRotation(new Quaternionf());
                Quaternionf toRotation = toPose.getNormalizedRotation(new Quaternionf());
                res.mulPose(fromRotation.nlerp(toRotation, progress));
            }
            if (scale) {
                Vector3f fromScale = fromPose.getScale(new Vector3f(0,0,0));
                Vector3f toScale = toPose.getScale(new Vector3f(0,0,0));
                res.scale(fromScale.x + (toScale.x - fromScale.x) * progress,
                        fromScale.y + (toScale.y - fromScale.y) * progress,
                        fromScale.z + (toScale.z - fromScale.z) * progress);
            }
        }
    }

    public static PoseStack copyPoseStack(PoseStack stack) {
        PoseStack result = new PoseStack();
        result.setIdentity();
        result.last().pose().set(stack.last().pose());
        result.last().normal().set(stack.last().normal());
        return result;
    }

    public static int secondsToTicks(float seconds) {
        return (int) (seconds / 0.05f);
    }
}
