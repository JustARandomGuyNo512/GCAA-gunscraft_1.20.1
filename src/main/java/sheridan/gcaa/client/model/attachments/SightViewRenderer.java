package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GlobalWeaponBobbing;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.RenderTypes;
import sheridan.gcaa.utils.RenderAndMathUtils;

import static com.mojang.blaze3d.platform.GlConst.GL_DEPTH_COMPONENT24;
import static com.mojang.blaze3d.platform.GlConst.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.ARBDepthBufferFloat.GL_DEPTH32F_STENCIL8;
import static org.lwjgl.opengl.ARBDepthBufferFloat.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL11C.GL_EQUAL;
import static org.lwjgl.opengl.GL11C.GL_NOTEQUAL;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16;
import static org.lwjgl.opengl.GL30C.GL_DEPTH24_STENCIL8;

@OnlyIn(Dist.CLIENT)
public class SightViewRenderer {
    private static final ResourceLocation DEFAULT_SCOPE_BACKGROUND = new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/default_scope_background.png");
    private static final BufferBuilder SCOPE_VIEW_BUFFER = new BufferBuilder(256 * 256);
    public static final float DEFAULT_RED_DOT_CROSSHAIR_SCALE = 0.06F;

    public static void renderRedDot(boolean effective, GunRenderContext context, ResourceLocation bodyTexture, ResourceLocation crosshairTexture, ModelPart crosshair, ModelPart... bodyParts) {
        renderRedDot(effective, DEFAULT_RED_DOT_CROSSHAIR_SCALE, context, bodyTexture, crosshairTexture, crosshair, bodyParts);
    }

    public static void renderRedDot(boolean effective, float crosshairScale, GunRenderContext context, ResourceLocation bodyTexture, ResourceLocation crosshairTexture, ModelPart crosshair, ModelPart... bodyParts) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(bodyTexture));
        for (ModelPart part : bodyParts) {
            context.render(part, vertexConsumer);
        }
        if (effective && crosshairTexture != null) {
            PoseStack poseStack = context.poseStack;
            poseStack.pushPose();
            crosshair.translateAndRotate(poseStack);
            Vector2f swing = GlobalWeaponBobbing.INSTANCE.getSwing();
            if (swing != null) {
                poseStack.translate(
                        Mth.clamp(-swing.y * 0.5f, -0.35, 0.35),
                        Mth.clamp(swing.x * 0.5f, -0.35, 0.35),
                        0);
            }
            if (crosshairScale != 1) {poseStack.scale(crosshairScale, crosshairScale, crosshairScale);}
            RenderSystem.enableBlend();
            VertexConsumer crosshairVertexConsumer = context.getBuffer(RenderTypes.getAttachmentOverlayDepth(crosshairTexture));
            renderCrosshair(poseStack, crosshairVertexConsumer);
            RenderSystem.disableBlend();
            poseStack.popPose();
        }
    }

    public static void renderScope(boolean active, boolean glowingCrosshair, float crosshairScale, float backgroundScale, GunRenderContext context, ResourceLocation crosshairTexture,
                                   ResourceLocation bodyTexture, ModelPart crosshair, ModelPart glassShape, ModelPart backGlass, ModelPart back_ground, ModelPart ...body) {
        renderScope(active, glowingCrosshair, crosshairScale, backgroundScale, context, crosshairTexture, bodyTexture, DEFAULT_SCOPE_BACKGROUND, crosshair, glassShape, backGlass, back_ground, body);
    }

    public static void renderScope(boolean active, boolean glowingCrosshair, float crosshairScale, float backgroundScale, GunRenderContext context, ResourceLocation crosshairTexture,
                                   ResourceLocation bodyTexture, ResourceLocation backgroundTexture, ModelPart crosshair, ModelPart glassShape, ModelPart backGlass, ModelPart back_ground, ModelPart ...body) {
        RenderAndMathUtils.setUpStencil();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        if (!active) {
            VertexConsumer vertexConsumer = context.solid(bodyTexture);
            context.render(backGlass, vertexConsumer);
            for (ModelPart part : body) {
                context.render(part, vertexConsumer);
            }
            return;
        }
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(SCOPE_VIEW_BUFFER);
        PoseStack poseStack = context.poseStack;
        poseStack.pushPose();

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        RenderSystem.clear(GL11.GL_STENCIL_BUFFER_BIT, Minecraft.ON_OSX);
        GlStateManager._stencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GlStateManager._stencilMask(0xFF);
        GlStateManager._stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        context.render(glassShape, bufferSource.getBuffer(RenderTypes.getStencilCull(bodyTexture)));
        bufferSource.endBatch();

        GlStateManager._stencilFunc(GL_NOTEQUAL, 1, 0xFF);
        GlStateManager._stencilMask(0x00);
        VertexConsumer bodyVertex = bufferSource.getBuffer(RenderType.entityCutout(bodyTexture));
        for (ModelPart part : body) {context.render(part, bodyVertex);}
        bufferSource.endBatch();

        GlStateManager._stencilFunc(GL_EQUAL, 1, 0xFF);
        if (glowingCrosshair) {RenderSystem.enableBlend();}
        VertexConsumer crosshairConsumer = bufferSource.getBuffer(RenderTypes.getAttachmentOverlayDepth(crosshairTexture));
        poseStack.pushPose();
        crosshair.translateAndRotate(poseStack);
        if (crosshairScale != 1) {poseStack.scale(crosshairScale, crosshairScale, crosshairScale);}
        renderCrosshair(poseStack, crosshairConsumer);
        bufferSource.endBatch();
        if (glowingCrosshair) {RenderSystem.disableBlend();}
        poseStack.popPose();

        VertexConsumer backgroundConsumer = bufferSource.getBuffer(RenderTypes.getBackground(backgroundTexture));
        poseStack.pushPose();
        back_ground.translateAndRotate(poseStack);
        if (backgroundScale != 1) {poseStack.scale(backgroundScale, backgroundScale, backgroundScale);}
        renderCrosshair(poseStack, backgroundConsumer);
        bufferSource.endBatch();
        poseStack.popPose();

        GlStateManager._stencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GlStateManager._stencilMask(0xFF);
        GlStateManager._stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        context.render(glassShape, bufferSource.getBuffer(RenderTypes.getStencilCull(bodyTexture)));
        bufferSource.endBatch();
        GlStateManager._stencilMask(0x00);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        poseStack.popPose();
        SCOPE_VIEW_BUFFER.clear();

    }

    private static void renderCrosshair(PoseStack poseStack, VertexConsumer vertexConsumer) {
        Matrix4f matrix4f = poseStack.last().pose();
        vertexConsumer.vertex(matrix4f, -0.5F, -0.5F, 0.0F).color(1.0F, 1.0F, 1.0F, 1).uv(0, 0).uv2(15728880).endVertex();
        vertexConsumer.vertex(matrix4f, 0.5F, -0.5F, 0.0F).color(1.0F, 1.0F, 1.0F, 1).uv(1, 0).uv2(15728880).endVertex();
        vertexConsumer.vertex(matrix4f, 0.5F, 0.5F, 0.0F).color(1.0F, 1.0F, 1.0F, 1).uv(1, 1).uv2(15728880).endVertex();
        vertexConsumer.vertex(matrix4f, -0.5F, 0.5F, 0.0F).color(1.0F, 1.0F, 1.0F, 1).uv(0, 1).uv2(15728880).endVertex();
    }
}

