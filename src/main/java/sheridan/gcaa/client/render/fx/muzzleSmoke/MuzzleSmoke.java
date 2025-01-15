package sheridan.gcaa.client.render.fx.muzzleSmoke;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import sheridan.gcaa.client.render.RenderTypes;

@OnlyIn(Dist.CLIENT)
public class MuzzleSmoke {
    public final int length;
    public final float size;
    public final float spread;
    public final Vector2f alphas;
    public final ResourceLocation texture;
    public final int columnNum;
    private boolean randomRotate = false;

    public MuzzleSmoke(int length, float size, float spread, Vector2f alphaLerp, ResourceLocation texture, int columnNum)  {
        this.length = length;
        this.size = size;
        this.spread = Math.max(1, spread) * this.size;
        this.alphas = alphaLerp;
        this.texture = texture;
        this.columnNum = Mth.clamp(columnNum, 1, 4);
    }

    public MuzzleSmoke randomRotate() {
        this.randomRotate = true;
        return this;
    }

    public boolean isRandomRotate() {
        return randomRotate;
    }

    public void render(long lastShoot, PoseStack poseStack, MultiBufferSource bufferSource, int randomSeed, int light) {
        long timeDist = System.currentTimeMillis() - lastShoot;
        if (timeDist < length) {
            VertexConsumer vertexConsumer = MuzzleSmokeRenderer.depthMask ?
                    bufferSource.getBuffer(RenderTypes.getMuzzleFlash(texture)) :
                    bufferSource.getBuffer(RenderTypes.getMuzzleFlashNotWriteDepth(texture));
            float progress = (float) timeDist / length;
            float size = Mth.lerp(progress, this.size, this.spread) * (0.833333333333333333f + randomSeed % 50 / 300f);
            float alpha = Mth.lerp(progress, alphas.x, alphas.y);
            int column = randomSeed % columnNum;
            int index = (int) (progress * 4);
            poseStack.pushPose();
            poseStack.translate(0, 0, - progress * 0.05f);
            if (randomRotate) {
                float angle = (randomSeed % 360) * 0.017453292519943295f;
                poseStack.mulPose(new Quaternionf().rotateZ(angle));
            }
            poseStack.scale(size, size, 1);
            draw(poseStack.last().pose(), vertexConsumer, alpha, column, index, light);
            poseStack.popPose();
        }
    }

    protected void draw(Matrix4f matrix, VertexConsumer vertexConsumer, float alpha, int column, int index, int light) {
        float u1 = index * 0.25f;
        float u2 = u1 + 0.25f;
        float v1 = column * 0.25f;
        float v2 = v1 + 0.25f;
        vertexConsumer.vertex(matrix, -0.5f, 0.5f, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(u1, v1).uv2(light).overlayCoords(655360).endVertex();
        vertexConsumer.vertex(matrix, 0.5f, 0.5f, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(u2, v1).uv2(light).overlayCoords(655360).endVertex();
        vertexConsumer.vertex(matrix, 0.5f, -0.5f, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(u2, v2).uv2(light).overlayCoords(655360).endVertex();
        vertexConsumer.vertex(matrix, -0.5f, -0.5f, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(u1, v2).uv2(light).overlayCoords(655360).endVertex();
    }
}
