package sheridan.gcaa.utils;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import java.nio.IntBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL11C.glGetIntegerv;
import static org.lwjgl.opengl.GL30C.*;

public class RenderAndMathUtils {
    public static final Random RANDOM = new Random();

    public static float sLerp(float progress) {
        float f1 = progress * progress;
        float f2 = 1.0f - (1.0f - progress) * (1.0f - progress);
        return Mth.lerp(progress, f1, f2);
    }
    public static float sCurve(float val) {
        return 3f * val * val - 2f * val * val * val;
    }

    public static PoseStack lerpPoseStack(PoseStack from, PoseStack to, float progress) {
        PoseStack res = new PoseStack();
        lerpPoseStack(from, to, res, progress, true, true, true);
        return res;
    }

    private static int framebuffer = -1;
    private static int lastWidth, lastHeight;

    private static void initFramebuffer(int width, int height) {
        framebuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        int depthStencilBuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthStencilBuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width, height, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (java.nio.ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthStencilBuffer, 0);
        lastWidth = width;
        lastHeight = height;
    }

    @OnlyIn(Dist.CLIENT)
    public static void copyDepthBuffer(int sourceFramebuffer, int width, int height) {
        if (framebuffer == -1 || width != lastWidth || height != lastHeight) {
            initFramebuffer(width, height);
        }
        glBindFramebuffer(GL_READ_FRAMEBUFFER, sourceFramebuffer);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, framebuffer);
        GL11.glDisable(GL43.GL_DEBUG_OUTPUT);
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        GL11.glEnable(GL43.GL_DEBUG_OUTPUT);
    }


    @OnlyIn(Dist.CLIENT)
    public static void restoreDepthBuffer(int targetFramebuffer, int width, int height) {
        if (framebuffer == -1) {
            return;
        }
        glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, targetFramebuffer);
        GL11.glDisable(GL43.GL_DEBUG_OUTPUT);
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        GL11.glEnable(GL43.GL_DEBUG_OUTPUT);
    }

    @OnlyIn(Dist.CLIENT)
    public static int getCurrentFramebuffer() {
        int[] framebuffer = new int[1];
        glGetIntegerv(GL_FRAMEBUFFER_BINDING, framebuffer);
        return framebuffer[0];
    }

    @OnlyIn(Dist.CLIENT)
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

    @OnlyIn(Dist.CLIENT)
    public static PoseStack copyPoseStack(PoseStack stack) {
        PoseStack result = new PoseStack();
        result.setIdentity();
        result.last().pose().set(stack.last().pose());
        result.last().normal().set(stack.last().normal());
        return result;
    }

    public static float randomIndex() {
        return Math.random() <= 0.5 ? 1 : -1;
    }

    public static int secondsToTicks(float seconds) {
        return (int) (seconds * 20);
    }

    public static float secondsFromNow(long timeStamp) {
        return  (System.currentTimeMillis() - timeStamp) * 0.001f;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isStencilEnabled() {
        int result = GL30.glGetFramebufferAttachmentParameteri(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_STENCIL_ATTACHMENT,
                GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
        return result != GL11.GL_NONE;
    }

    @OnlyIn(Dist.CLIENT)
    public static void setUpStencil() {
        GL11.glDisable(GL43.GL_DEBUG_OUTPUT);
        Minecraft.getInstance().getMainRenderTarget().enableStencil();
        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            return;
        }
        if (isStencilEnabled()) {
            return;
        }
        int depthTextureId = glGetFramebufferAttachmentParameteri(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
        GL30.glBindTexture(GL_TEXTURE_2D, depthTextureId);
        GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8,
                glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH),
                glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT),
                0, 34041, 34042, null);
        GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthTextureId, 0);
        GL11.glEnable(GL43.GL_DEBUG_OUTPUT);
    }

    @OnlyIn(Dist.CLIENT)
    public static int getDepthTextureFormat() {
        IntBuffer depthTextureID = BufferUtils.createIntBuffer(1);
        glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME, depthTextureID);
        int depthTex = depthTextureID.get(0);
        if (depthTex == 0) {
            return -1;
        }
        glBindTexture(GL_TEXTURE_2D, depthTex);
        return glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_INTERNAL_FORMAT);
    }

    public static int getRandomIndex(int max) {
        return RANDOM.nextInt(max);
    }

    @OnlyIn(Dist.CLIENT)
    public static float disToCamera(PoseStack poseStack) {
        return (float) Math.sqrt(disToCameraSqr(poseStack));
    }

    @OnlyIn(Dist.CLIENT)
    public static float disToCameraSqr(PoseStack poseStack) {
        Vector3f pos = poseStack.last().pose().getTranslation(new Vector3f(0,0,0));
        return pos.x * pos.x + pos.y * pos.y + pos.z * pos.z;
    }
}
