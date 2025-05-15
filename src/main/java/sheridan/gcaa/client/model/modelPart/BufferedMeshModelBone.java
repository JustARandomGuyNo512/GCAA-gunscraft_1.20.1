package sheridan.gcaa.client.model.modelPart;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.gun.guns.RifleModels;

@OnlyIn(Dist.CLIENT)
public class BufferedMeshModelBone {
    public MeshedModelBone bone;
    public RenderType renderType;
    public BufferBuilder buffer = new BufferBuilder(256 * 256);
    public BufferBuilder.RenderedBuffer renderedBuffer;
    public VertexBuffer vertexBuffer;

    public void compile(PoseStack.Pose pose, int light, int overlay, float r, float g, float b, float a) {
        vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(buffer);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
        bone.compile(pose, vertexConsumer, light, overlay, r, g, b, a);
        renderedBuffer = buffer.end();
        vertexBuffer.bind();
        vertexBuffer.upload(renderedBuffer);
        VertexBuffer.unbind();
    }

    public void render() {
        if (vertexBuffer == null) {
            PoseStack poseStack = new PoseStack();
            compile(poseStack.last(), 15728880, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        }
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate(1, 1, 1);
        RenderSystem.applyModelViewMatrix();
        vertexBuffer.bind();
        vertexBuffer.draw();
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    static BufferedMeshModelBone TEST;
    public static void __start_test__() {
        CommonRifleModel mcxSpearModel = (CommonRifleModel) RifleModels.MCX_SPEAR_MODEL;
        MeshedModelBone meshedModelBone = mcxSpearModel.main.meshedModelBone;
        TEST = new BufferedMeshModelBone();
        TEST.bone = meshedModelBone;
        TEST.compile(new PoseStack().last(), 15728880, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        TEST.renderType = RenderType.entityCutout(mcxSpearModel.texture);
    }

    public static void __test_render__() {
        if (TEST != null) {
            TEST.render();
        }
    }
}
