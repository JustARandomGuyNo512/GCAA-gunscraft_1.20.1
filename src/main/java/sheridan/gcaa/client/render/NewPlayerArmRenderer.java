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
public class NewPlayerArmRenderer {
    public static final NewPlayerArmRenderer INSTANCE = new NewPlayerArmRenderer(NewPlayerArmRenderer.createBodyLayer().bakeRoot());
    private final ModelPart left_arm;
    private final ModelPart left_arm_slim;
    private final ModelPart LSS;
    private final ModelPart left_arm_normal;
    private final ModelPart LS;
    private final ModelPart right_arm;
    private final ModelPart right_arm_slim;
    private final ModelPart RSS;
    private final ModelPart right_arm_normal;
    private final ModelPart RS;

    private final Map<String, ModelPart[]> oldStyleParts;

    public NewPlayerArmRenderer(ModelPart root) {
        this.left_arm = root.getChild("left_arm");
        this.left_arm_slim = this.left_arm.getChild("left_arm_slim");
        this.LSS = this.left_arm_slim.getChild("LSS");
        this.left_arm_normal = this.left_arm.getChild("left_arm_normal");
        this.LS = this.left_arm_normal.getChild("LS");
        this.right_arm = root.getChild("right_arm");
        this.right_arm_slim = this.right_arm.getChild("right_arm_slim");
        this.RSS = this.right_arm_slim.getChild("RSS");
        this.right_arm_normal = this.right_arm.getChild("right_arm_normal");
        this.RS = this.right_arm_normal.getChild("RS");

        oldStyleParts = new HashMap<>();
        ModelPart oldStyle = createOldStyleBodyLayer().bakeRoot();
        ModelPart left_arm_slim = oldStyle.getChild("left_arm_slim");
        ModelPart right_arm_slim = oldStyle.getChild("right_arm_slim");
        ModelPart left_sleeve_slim = oldStyle.getChild("left_sleeve_slim");
        ModelPart right_sleeve_slim = oldStyle.getChild("right_sleeve_slim");
        ModelPart left_arm = oldStyle.getChild("left_arm");
        ModelPart right_arm = oldStyle.getChild("right_arm");
        ModelPart left_sleeve = oldStyle.getChild("left_sleeve");
        ModelPart right_sleeve = oldStyle.getChild("right_sleeve");
        oldStyleParts.put("left_arm_slim", new ModelPart[]{left_arm_slim, left_sleeve_slim});
        oldStyleParts.put("right_arm_slim", new ModelPart[]{right_arm_slim, right_sleeve_slim});
        oldStyleParts.put("left_arm", new ModelPart[]{left_arm, left_sleeve});
        oldStyleParts.put("right_arm", new ModelPart[]{right_arm, right_sleeve});
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition left_arm_slim = left_arm.addOrReplaceChild("left_arm_slim", CubeListBuilder.create(), PartPose.offset(0.0F, 48.0F, 0.0F));
        left_arm_slim.addOrReplaceChild("LAS", CubeListBuilder.create().texOffs(128, 192).addBox(-6.0F, -48.0F, -8.0F, 12.0F, 48.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        left_arm_slim.addOrReplaceChild("LSS", CubeListBuilder.create().texOffs(160, 128).addBox(-6.0F, -48.0F, -8.0F, 12.0F, 48.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_arm_normal = left_arm.addOrReplaceChild("left_arm_normal", CubeListBuilder.create(), PartPose.offset(0.0F, 48.0F, 0.0F));
        left_arm_normal.addOrReplaceChild("LA", CubeListBuilder.create().texOffs(128, 192).addBox(-8.0F, -48.0F, -8.0F, 16.0F, 48.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        left_arm_normal.addOrReplaceChild("LS", CubeListBuilder.create().texOffs(160, 128).addBox(-8.0F, -48.0F, -8.0F, 16.0F, 48.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition right_arm_slim = right_arm.addOrReplaceChild("right_arm_slim", CubeListBuilder.create(), PartPose.offset(0.0F, 48.0F, 0.0F));
        right_arm_slim.addOrReplaceChild("RAS", CubeListBuilder.create().texOffs(160, 64).addBox(-6.0F, -48.0F, -8.0F, 12.0F, 48.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        right_arm_slim.addOrReplaceChild("RSS", CubeListBuilder.create().texOffs(192, 192).addBox(-6.0F, -48.0F, -8.0F, 12.0F, 48.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_arm_normal = right_arm.addOrReplaceChild("right_arm_normal", CubeListBuilder.create(), PartPose.offset(0.0F, 48.0F, 0.0F));
        right_arm_normal.addOrReplaceChild("RA", CubeListBuilder.create().texOffs(160, 64).addBox(-8.0F, -48.0F, -8.0F, 16.0F, 48.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        right_arm_normal.addOrReplaceChild("RS", CubeListBuilder.create().texOffs(192, 192).addBox(-8.0F, -48.0F, -8.0F, 16.0F, 48.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    private static LayerDefinition createOldStyleBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("left_arm_slim", CubeListBuilder.create().texOffs(96, 144).addBox(-4.45F, 0.0F, -6.0F, 9.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm_slim", CubeListBuilder.create().texOffs(120, 48).addBox(-4.55F, 0.0F, -6.0F, 9.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_sleeve_slim", CubeListBuilder.create().texOffs(144, 144).addBox(-4.45F, 0.0F, -6.0F, 9.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_sleeve_slim", CubeListBuilder.create().texOffs(120, 96).addBox(-4.45F, 0.0F, -6.0F, 9.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(96, 144).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(120, 48).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(144, 144).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(120, 96).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 192, 192);
    }

    public static boolean isSlim() {
        AbstractClientPlayer abstractClientPlayer = Minecraft.getInstance().player;
        return abstractClientPlayer != null && "slim".equals(abstractClientPlayer.getModelName());
    }

    public void renderByPose(int light, int overlay, boolean mainHand, MultiBufferSource bufferSource, PoseStack innerLayerStack) {
        AbstractClientPlayer abstractClientPlayer = Minecraft.getInstance().player;
        if (abstractClientPlayer != null) {
            boolean isPlayerModelSlim = isSlim(abstractClientPlayer);
            ResourceLocation playerTexture = abstractClientPlayer.getSkinTextureLocation();
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(playerTexture));
            if (mainHand) {
                if (isPlayerModelSlim) {
                    RSS.xScale = 1.12f;
                    RSS.yScale = 1.12f;
                    RSS.zScale = 1.12f;
                    right_arm_slim.render(innerLayerStack, vertexConsumer, light, overlay, 1, 1, 1, 1, false);
                } else {
                    RS.xScale = 1.12f;
                    RS.yScale = 1.12f;
                    RS.zScale = 1.12f;
                    right_arm_normal.render(innerLayerStack, vertexConsumer, light, overlay, 1, 1, 1, 1, false);
                }
            } else {
                if (isPlayerModelSlim) {
                    LSS.xScale = 1.12f;
                    LSS.yScale = 1.12f;
                    LSS.zScale = 1.12f;
                    left_arm_slim.render(innerLayerStack, vertexConsumer, light, overlay, 1, 1, 1, 1, false);
                } else {
                    LS.xScale = 1.12f;
                    LS.yScale = 1.12f;
                    LS.zScale = 1.12f;
                    left_arm_normal.render(innerLayerStack, vertexConsumer, light, overlay, 1, 1, 1, 1, false);
                }
            }
        }
    }

    public void renderByLayer(ModelPart poseLayer, float sx, float sy, float sz, int light, int overlay, boolean mainHand, MultiBufferSource bufferSource, PoseStack poseStack) {
        AbstractClientPlayer abstractClientPlayer = Minecraft.getInstance().player;
        if (abstractClientPlayer != null) {
            boolean isPlayerModelSlim = isSlim(abstractClientPlayer);
            ResourceLocation playerTexture = abstractClientPlayer.getSkinTextureLocation();
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(playerTexture));
            if (mainHand) {
                if (isPlayerModelSlim) {
                    copyPoseRightSlim(poseLayer, sx, sy, sz);
                } else {
                    copyPoseRightNormal(poseLayer, sx, sy, sz);
                }
                right_arm.render(poseStack, vertexConsumer, light, overlay, 1, 1, 1, 1, true);
                right_arm.forceResetPoseAll();
            } else {
                if (isPlayerModelSlim) {
                    copyPoseLeftSlim(poseLayer, sx, sy, sz);
                } else {
                    copyPoseLeftNormal(poseLayer, sx, sy, sz);
                }
                left_arm.render(poseStack, vertexConsumer, light, overlay, 1, 1, 1, 1, true);
                left_arm.forceResetPoseAll();
            }
        }
    }

    private boolean isSlim(AbstractClientPlayer abstractClientPlayer) {
        return "slim".equals(abstractClientPlayer.getModelName());
    }

    public void renderOldStylePistolByLayer(ModelPart poseLayer, boolean mainHand, int light, int overlay, MultiBufferSource bufferSource, PoseStack poseStack, boolean usePoseLayer) {
        AbstractClientPlayer abstractClientPlayer = Minecraft.getInstance().player;
        if (abstractClientPlayer != null) {
            boolean isPlayerModelSlim = isSlim(abstractClientPlayer);
            ResourceLocation playerTexture = abstractClientPlayer.getSkinTextureLocation();
            String name = mainHand ? "right_arm" : "left_arm";
            if (isPlayerModelSlim) {
                name += "_slim";
            }
            ModelPart[] models = oldStyleParts.get(name);
            if (models != null) {
                ModelPart arm = models[0];
                ModelPart sleeve = models[1];
                if (arm != null && sleeve != null) {
                    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(playerTexture));
                    if (usePoseLayer) {
                        arm.copyFrom(poseLayer);
                    } else {
                        poseStack.pushPose();
                    }
                    arm.render(poseStack, vertexConsumer, light, overlay, 1, 1, 1, 1, usePoseLayer);
                    if (usePoseLayer) {
                        sleeve.copyFrom(poseLayer);
                        sleeve.xScale = 1.12F;
                        sleeve.yScale = 1.12f;
                        sleeve.zScale = 1.12F;
                    } else {
                        poseStack.scale(1.12F, 1,1.12F);
                    }
                    sleeve.render(poseStack, vertexConsumer, light, overlay, 1, 1, 1, 1, usePoseLayer);
                    if (!usePoseLayer) {
                        poseStack.popPose();
                    } else {
                        arm.resetPose();
                        sleeve.resetPose();
                    }
                }
            }
        }
    }

    private void copyPoseRightNormal(ModelPart poseLayer, float sx, float sy, float sz) {
        right_arm_normal.visible = true;
        right_arm.copyFrom(poseLayer);
        right_arm.xScale *= sx;
        right_arm.yScale *= sy;
        right_arm.zScale *= sz;
        right_arm_normal.copyFrom(poseLayer.getChild("right_arm_normal"));
        RS.xScale = 1.12f;
        RS.yScale = 1.12f;
        RS.zScale = 1.12f;
        right_arm_slim.visible = false;
    }

    private void copyPoseRightSlim(ModelPart poseLayer, float sx, float sy, float sz) {
        right_arm_slim.visible = true;
        right_arm.copyFrom(poseLayer);
        right_arm.xScale *= sx;
        right_arm.yScale *= sy;
        right_arm.zScale *= sz;
        right_arm_slim.copyFrom(poseLayer.getChild("right_arm_slim"));
        RSS.xScale = 1.12f;
        RSS.yScale = 1.12f;
        RSS.zScale = 1.12f;
        right_arm_normal.visible = false;
    }

    private void copyPoseLeftNormal(ModelPart poseLayer, float sx, float sy, float sz) {
        left_arm_normal.visible = true;
        left_arm.copyFrom(poseLayer);
        left_arm.xScale *= sx;
        left_arm.yScale *= sy;
        left_arm.zScale *= sz;
        left_arm_normal.copyFrom(poseLayer.getChild("left_arm_normal"));
        LS.xScale = 1.12f;
        LS.yScale = 1.12f;
        LS.zScale = 1.12f;
        left_arm_slim.visible = false;
    }

    private void copyPoseLeftSlim(ModelPart poseLayer, float sx, float sy, float sz) {
        left_arm_slim.visible = true;
        left_arm.copyFrom(poseLayer);
        left_arm.xScale *= sx;
        left_arm.yScale *= sy;
        left_arm.zScale *= sz;
        left_arm_slim.copyFrom(poseLayer.getChild("left_arm_slim"));
        LSS.xScale = 1.12f;
        LSS.yScale = 1.12f;
        LSS.zScale = 1.12f;
        left_arm_normal.visible = false;
    }
}
