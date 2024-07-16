package sheridan.gcaa.client.render.fx.muzzleFlash;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import sheridan.gcaa.client.render.RenderTypes;

@OnlyIn(Dist.CLIENT)
public class MuzzleFlashTexture {
    private static final float BASE_ALPHA = 0.9f;
    private final int count;
    private final float quadSize;
    private final RenderType renderType;

    public MuzzleFlashTexture(ResourceLocation texture, int count) {
        quadSize = 1f / count;
        this.count = count;
        this.renderType = RenderTypes.getMuzzleFlash(texture);
    }

    public int getCount() {
        return count;
    }

    public void render(int index, PoseStack poseStack, MultiBufferSource buffer, boolean isFirstPerson) {
        if (index >= 0 && index < count) {
            VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
            draw(poseStack, 0, 0, 0, 0, vertexConsumer, index);
            draw(poseStack, 2, 1.5707963267948966f,  0,  -0.5f,  vertexConsumer, index);
            draw(poseStack, 1, 1.5707963267948966f, -1.5707963267948966f,   -0.5f,  vertexConsumer, index);
        }
    }

    private void draw(PoseStack poseStack,int axis, float rx, float ry, float tz, VertexConsumer vertexConsumer, int index) {
        poseStack.pushPose();
        if (tz != 0) {
            poseStack.translate(0 , 0 , tz);
        }
        if (rx != 0 || ry != 0) {
            poseStack.mulPose(new Quaternionf().rotateXYZ(rx, ry, 0));
        }
        drawQuad(axis, index,  poseStack, vertexConsumer);
        poseStack.popPose();
    }

    private void drawQuad(int axis, int index, PoseStack stack, VertexConsumer builder) {
        float[] uv = getUV(index, axis);
        if (uv != null) {
            drawQuad(builder, stack.last().pose(), uv[0], uv[1],uv[2], uv[3]);
        }
    }

    private float[] getUV(int index, int axis) {
        return switch (axis) {
            case 0 -> new float[]{quadSize * index, 0f, quadSize * (index + 1), quadSize};
            case 1 -> new float[]{quadSize * index, quadSize, quadSize * (index + 1), quadSize * 2};
            case 2 -> new float[]{quadSize * index, quadSize * 2, quadSize * (index + 1), quadSize * 3};
            default -> null;
        };
    }

    private void drawQuad(VertexConsumer builder, Matrix4f matrix, float u1, float v1, float u2, float v2) {
        builder.vertex(matrix, -0.5f, 0.5f, 0.0F).color(1.0F, 1.0F, 1.0F, BASE_ALPHA).uv(u2, v2).uv2(157288880).overlayCoords(655360).endVertex();
        builder.vertex(matrix, 0.5f, 0.5f, 0.0F).color(1.0F, 1.0F, 1.0F, BASE_ALPHA).uv(u1, v2).uv2(157288880).overlayCoords(655360).endVertex();
        builder.vertex(matrix, 0.5f, -0.5f, 0.0F).color(1.0F, 1.0F, 1.0F, BASE_ALPHA).uv(u1, v1).uv2(157288880).overlayCoords(655360).endVertex();
        builder.vertex(matrix, -0.5f, -0.5f, 0.0F).color(1.0F, 1.0F, 1.0F, BASE_ALPHA).uv(u2, v1).uv2(157288880).overlayCoords(655360).endVertex();
    }
}
