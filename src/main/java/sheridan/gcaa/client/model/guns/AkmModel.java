package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.modelPart.*;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.AdventurersArsenalLib;

@OnlyIn(Dist.CLIENT)
public class AkmModel extends HierarchicalModel<Entity> implements IGunModel{
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.png");
    private final ModelPart root;
    private final ModelPart gun_arm;
    private final ModelPart gun;
    private final ModelPart left_arm;
    private final ModelPart right_arm;

    private final ModelPart barrel, rail_set, slide,
            muzzle, handguard, IS,
            dust_cover, mag, grip,
            safety, body, stock;
    public AkmModel() {
        this.root = AdventurersArsenalLib.loadBedRockGunModel(
                        new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.geo.json"))
                .bakeRoot().getChild("root");
        this.gun_arm = root.getChild("gun_arm");
        this.gun = gun_arm.getChild("gun");
        this.right_arm = gun_arm.getChild("right_arm");
        this.left_arm = root.getChild("left_arm");
        this.barrel = gun.getChild("barrel").meshing();
        this.rail_set = gun.getChild("rail_set").meshing();
        this.slide = gun.getChild("slide").meshing();
        this.muzzle = gun.getChild("muzzle").meshing();
        this.handguard = gun.getChild("handguard").meshing();
        this.IS = gun.getChild("IS").meshing();
        this.dust_cover = gun.getChild("dust_cover").meshing();
        this.mag = gun.getChild("mag").meshing();
        this.grip = gun.getChild("grip").meshing();
        this.safety = gun.getChild("safety").meshing();
        this.body = gun.getChild("body").meshing();
        this.stock = gun.getChild("stock").meshing();
    }

    @Override
    public void render(GunRenderContext gunRenderContext) {
        PoseStack poseStack = gunRenderContext.poseStack;
        root.translateAndRotate(poseStack);
        poseStack.pushPose();
        gun_arm.translateAndRotate(poseStack);
        gunRenderContext.renderArmLong(right_arm, true);
        gun.translateAndRotate(poseStack);
        VertexConsumer vertexConsumer = gunRenderContext.bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        gunRenderContext.render(barrel, vertexConsumer);
        gunRenderContext.render(rail_set, vertexConsumer);
        gunRenderContext.render(slide, vertexConsumer);
        gunRenderContext.render(muzzle, vertexConsumer);
        gunRenderContext.render(handguard, vertexConsumer);
        gunRenderContext.render(IS, vertexConsumer);
        gunRenderContext.render(dust_cover, vertexConsumer);
        gunRenderContext.render(mag, vertexConsumer);
        gunRenderContext.render(grip, vertexConsumer);
        gunRenderContext.render(safety, vertexConsumer);
        gunRenderContext.render(body, vertexConsumer);
        gunRenderContext.render(stock, vertexConsumer);
        poseStack.popPose();
        gunRenderContext.renderArmLong(left_arm, false);
    }

    @Override
    public void handleGunTranslate(PoseStack poseStack) {
        root.translateAndRotate(poseStack);
        gun_arm.translateAndRotate(poseStack);
        gun.translateAndRotate(poseStack);
    }

    @Override
    public ModelPart root() {
        return root;
    }
}
