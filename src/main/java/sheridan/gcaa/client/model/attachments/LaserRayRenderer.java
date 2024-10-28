package sheridan.gcaa.client.model.attachments;
// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.modelPart.*;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.RenderTypes;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class LaserRayRenderer<T extends Entity> extends EntityModel<T> {
    private static final Map<String, ModelPart> MODELS = new HashMap<>();
    private static final LaserRayRenderer<?> INSTANCE = new LaserRayRenderer<>(LaserRayRenderer.createBodyLayer().bakeRoot());
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "textures/fx/laser.png");
    public static final String GREEN = "green";
    public static final String RED = "red";
    public static final String BLUE = "blue";

    private final ModelPart root;

    private LaserRayRenderer(ModelPart root) {
        this.root = root;
        MODELS.put(GREEN, root.getChild("green"));
        MODELS.put(RED, root.getChild("red"));
        MODELS.put(BLUE, root.getChild("blue"));
    }

    private static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("green", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -16.0F, 1.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.7854F));
        partdefinition.addOrReplaceChild("red", CubeListBuilder.create().texOffs(0, 17).addBox(-0.5F, -0.5F, -16.0F, 1.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.7854F));
        partdefinition.addOrReplaceChild("blue", CubeListBuilder.create().texOffs(0, 34).addBox(-0.5F, -0.5F, -16.0F, 1.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.7854F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static void render(GunRenderContext context, ModelPart pose, String color, boolean longRay) {
        ModelPart part = INSTANCE.get(color);
        if (part != null) {
            part.copyFrom(pose);
            VertexConsumer vertexConsumer = context.getBuffer(RenderTypes.getMuzzleFlash(TEXTURE));
            part.zScale = longRay ? 2.7f : 2f;
            part.xScale = 0.35f;
            part.yScale = 0.35f;
            part.zRot = 0.7854F;
            context.pushPose().translateTo(INSTANCE.root).render(part, 157288880, 655360, 0.9f, vertexConsumer);
            context.popPose();
            part.resetPose();
        }
    }

    private ModelPart get(String color) {
        return MODELS.get(color);
    }

    @Override
    public void setupAnim(@NotNull T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {}

    @Override
    public void renderToBuffer(@NotNull PoseStack pPoseStack, @NotNull VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {}
}