package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.modelPart.*;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class PlayerArmRenderer {
    public static PlayerArmRenderer INSTANCE = new PlayerArmRenderer();
    private final Map<String, ModelPart[]> parts;

    PlayerArmRenderer() {
        parts = new HashMap<>();
        ModelPart root = createBodyLayer().bakeRoot();
        ModelPart left_arm_slim = root.getChild("left_arm_slim");
        ModelPart right_arm_slim = root.getChild("right_arm_slim");
        ModelPart left_sleeve_slim = root.getChild("left_sleeve_slim");
        ModelPart right_sleeve_slim = root.getChild("right_sleeve_slim");
        ModelPart left_arm = root.getChild("left_arm");
        ModelPart right_arm = root.getChild("right_arm");
        ModelPart left_sleeve = root.getChild("left_sleeve");
        ModelPart right_sleeve = root.getChild("right_sleeve");
        parts.put("left_arm_slim", new ModelPart[]{left_arm_slim, left_sleeve_slim});
        parts.put("right_arm_slim", new ModelPart[]{right_arm_slim, right_sleeve_slim});
        parts.put("left_arm", new ModelPart[]{left_arm, left_sleeve});
        parts.put("right_arm", new ModelPart[]{right_arm, right_sleeve});
    }

    private static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("left_arm_slim", CubeListBuilder.create().texOffs(96, 144).addBox(-4.45F, 0.0F, -6.0F, 9.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm_slim", CubeListBuilder.create().texOffs(120, 48).addBox(-4.55F, 0.0F, -6.0F, 9.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_sleeve_slim", CubeListBuilder.create().texOffs(144, 143).addBox(-4.45F, 0.0F, -6.0F, 9.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_sleeve_slim", CubeListBuilder.create().texOffs(120, 96).addBox(-4.45F, 0.0F, -6.0F, 9.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(96, 144).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(120, 48).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(144, 144).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(120, 96).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 192, 192);
    }

    public void render(ModelPart posePart, int light, int overlay, boolean mainHand, MultiBufferSource bufferSource, PoseStack poseStack) {
        render(posePart, light, overlay, 1, 1, 1, mainHand, bufferSource, poseStack);
    }

    public void render(ModelPart posePart, int light, int overlay, float scale, boolean mainHand, MultiBufferSource bufferSource, PoseStack poseStack) {
        render(posePart, light, overlay, scale, scale, scale, mainHand, bufferSource, poseStack);
    }

    public void render(ModelPart posePart, int light, int overlay, float sx, float sy, float sz, boolean mainHand, MultiBufferSource bufferSource, PoseStack poseStack) {
        AbstractClientPlayer abstractClientPlayer = Minecraft.getInstance().player;
        if (abstractClientPlayer != null) {
            boolean isPlayerModelSlim = "slim".equals(abstractClientPlayer.getModelName());
            ResourceLocation playerTexture = abstractClientPlayer.getSkinTextureLocation();
            String name = mainHand ? "right_arm" : "left_arm";
            if (isPlayerModelSlim) {
                name += "_slim";
            }
            ModelPart[] models = parts.get(name);
            if (models != null) {
                ModelPart arm = models[0];
                ModelPart sleeve = models[1];
                if (arm != null && sleeve != null) {
                    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(playerTexture));
                    arm.copyFrom(posePart);
                    arm.yScale = sy;
                    arm.xScale = sx;
                    arm.zScale = sz;
                    arm.render(poseStack, vertexConsumer, light, overlay, 1, 1, 1, 1);
                    sleeve.copyFrom(posePart);
                    sleeve.xScale = 1.2F * sx;
                    sleeve.yScale = sy;
                    sleeve.zScale = 1.2F * sz;
                    sleeve.render(poseStack, vertexConsumer, light, overlay, 1, 1, 1, 1);
                    arm.resetPose();
                    sleeve.resetPose();
                }
            }
        }
    }

}
