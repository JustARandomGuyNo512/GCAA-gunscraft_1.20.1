package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.modelPart.*;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class G19Model extends HierarchicalModel<Entity> implements IGunModel{
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.png");
    private final ModelPart root;
    private final ModelPart barrel;
    private final ModelPart muzzle_point;
    private final ModelPart grid;
    private final ModelPart slide;
    private final ModelPart mag;
    private final ModelPart mag_point;
    private final ModelPart right_arm;
    private final ModelPart left_arm;

    private final ModelPart gun_arm;
    private final ModelPart gun;

    public G19Model() {
        this.root = ArsenalLib.loadBedRockGunModel(
                new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.geo.json"))
                .bakeRoot().getChild("root");
        left_arm = root.getChild("left_arm");
        gun_arm = root.getChild("gun_arm");
        gun = gun_arm.getChild("gun");
        right_arm = gun_arm.getChild("right_arm");
        barrel = gun.getChild("barrel").meshing();
        muzzle_point = barrel.getChild("muzzle_point");
        grid = gun.getChild("grid").meshing();
        slide = gun.getChild("slide").meshing();
        mag = gun.getChild("mag").meshing();
        mag_point = mag.getChild("mag_point");
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void render(GunRenderContext gunRenderContext) {
        VertexConsumer vertexConsumer = gunRenderContext.bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        PoseStack poseStack = gunRenderContext.poseStack;
        root.translateAndRotate(poseStack);
        poseStack.pushPose();
        gun_arm.translateAndRotate(poseStack);
        poseStack.pushPose();
        gun.translateAndRotate(poseStack);
        gunRenderContext.render(barrel, vertexConsumer);
        gunRenderContext.render(slide, vertexConsumer);
        gunRenderContext.render(grid, vertexConsumer);
        gunRenderContext.render(mag, vertexConsumer);
        poseStack.popPose();
        gunRenderContext.renderArm(right_arm, true);
        poseStack.popPose();
        gunRenderContext.renderArm(left_arm, false);
    }

    @Override
    public void handleGunTranslate(PoseStack poseStack) {
        root.translateAndRotate(poseStack);
        gun_arm.translateAndRotate(poseStack);
        gun.translateAndRotate(poseStack);
    }
}
