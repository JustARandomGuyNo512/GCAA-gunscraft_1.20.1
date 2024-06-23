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
public class AkmModel extends GenericGunModel{
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
        this.root = ArsenalLib.loadBedRockGunModel(
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
    public void handleGunTranslate(PoseStack poseStack) {
        root.translateAndRotate(poseStack);
        gun_arm.translateAndRotate(poseStack);
        gun.translateAndRotate(poseStack);
    }

    @Override
    public ModelPart getRoot() {
        return root;
    }

    @Override
    public ModelPart getGunArmLayer() {
        return gun_arm;
    }

    @Override
    public ModelPart getGunLayer() {
        return gun;
    }

    @Override
    public ModelPart getLeftArm() {
        return left_arm;
    }

    @Override
    public ModelPart getRightArm() {
        return right_arm;
    }

    @Override
    public void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        context.render(barrel, vertexConsumer);
        context.render(rail_set, vertexConsumer);
        context.render(slide, vertexConsumer);
        context.render(muzzle, vertexConsumer);
        context.render(handguard, vertexConsumer);
        context.render(IS, vertexConsumer);
        context.render(dust_cover, vertexConsumer);
        context.render(mag, vertexConsumer);
        context.render(grip, vertexConsumer);
        context.render(safety, vertexConsumer);
        context.render(body, vertexConsumer);
        context.render(stock, vertexConsumer);
    }

    @Override
    public void renderAttachmentsModel(GunRenderContext context) {

    }

    @Override
    protected boolean longArm() {
        return true;
    }
}
