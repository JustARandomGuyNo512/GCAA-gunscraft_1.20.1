package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GlobalWeaponBobbing;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.RenderTypes;

@OnlyIn(Dist.CLIENT)
public class SightViewRenderer {
    public static final float CROSSHAIR_SCALE = 0.06F;

    public static void renderRedDot(boolean effective, GunRenderContext context, ResourceLocation bodyTexture, ResourceLocation crosshairTexture, ModelPart crosshair, ModelPart... bodyParts) {
        renderRedDot(effective, CROSSHAIR_SCALE, context, bodyTexture, crosshairTexture, crosshair, bodyParts);
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
                poseStack.translate(Mth.clamp(-swing.y * 0.5f, -0.35, 0.35), Mth.clamp(swing.x * 0.5f, -0.35, 0.35), 0);
            }
            poseStack.scale(crosshairScale, crosshairScale, crosshairScale);
            RenderSystem.enableBlend();
            Matrix4f matrix4f = poseStack.last().pose();
            VertexConsumer crosshairVertexConsumer = context.getBuffer(RenderTypes.getAttachmentOverlayDepth(crosshairTexture));
            crosshairVertexConsumer.vertex(matrix4f, -0.5F, -0.5F, 0.0F).color(1.0F, 1.0F, 1.0F, 1).uv(0, 0).uv2(15728880).endVertex();
            crosshairVertexConsumer.vertex(matrix4f, 0.5F, -0.5F, 0.0F).color(1.0F, 1.0F, 1.0F, 1).uv(1, 0).uv2(15728880).endVertex();
            crosshairVertexConsumer.vertex(matrix4f, 0.5F, 0.5F, 0.0F).color(1.0F, 1.0F, 1.0F, 1).uv(1, 1).uv2(15728880).endVertex();
            crosshairVertexConsumer.vertex(matrix4f, -0.5F, 0.5F, 0.0F).color(1.0F, 1.0F, 1.0F, 1).uv(0, 1).uv2(15728880).endVertex();
            RenderSystem.disableBlend();
            poseStack.popPose();
        }
    }

    public static void renderScope() {

    }
}
