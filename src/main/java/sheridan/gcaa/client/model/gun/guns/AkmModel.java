package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.LodGunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.NewPlayerArmRenderer;
import sheridan.gcaa.items.gun.fireModes.Auto;

@OnlyIn(Dist.CLIENT)
public class AkmModel extends LodGunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.png");
    private final ResourceLocation TEXTURE_LOW = new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm_low.png");
    private ModelPart
            slide, muzzle, handguard,
            dust_cover, mag, grip,
            safety, body, stock, bullet;

    private ModelPart
            slide_low, muzzle_low, handguard_low,
            dust_cover_low, mag_low, grip_low,
            safety_low, body_low, stock_low;

    private final AnimationDefinition shoot;

    public AkmModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm_low.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.animation.json"));
        shoot = animations.get("shoot");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        slide = gun.getChild("slide").meshing();
        muzzle = gun.getChild("muzzle").meshing();
        handguard = gun.getChild("handguard").meshing();
        dust_cover = gun.getChild("dust_cover").meshing();
        grip = gun.getChild("grip").meshing();
        safety = gun.getChild("safety").meshing();
        body = gun.getChild("body").meshing();
        stock = gun.getChild("stock").meshing();
        mag = gun.getChild("mag").meshing();
        bullet = mag.getChild("bullet").meshing();
    }

    @Override
    protected void postInitLowQuality(ModelPart lowQualityGun, ModelPart lowQualityRoot) {
        slide_low = lowQualityGun.getChild("slide").meshing();
        muzzle_low = lowQualityGun.getChild("muzzle").meshing();
        handguard_low = lowQualityGun.getChild("handguard").meshing();
        dust_cover_low = lowQualityGun.getChild("dust_cover").meshing();
        mag_low = lowQualityGun.getChild("mag").meshing();
        grip_low = lowQualityGun.getChild("grip").meshing();
        safety_low = lowQualityGun.getChild("safety").meshing();
        body_low = lowQualityGun.getChild("body").meshing();
        stock_low = lowQualityGun.getChild("stock").meshing();
    }

    @Override
    protected void renderGunNormal(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.solid(TEXTURE);
        if (context.isFirstPerson) {
            safety.xRot = Clients.MAIN_HAND_STATUS.fireMode == Auto.AUTO ? 0.2181661564992911875f : 0.436332312998582375f;
        } else {
            safety.xRot = 0;
        }
        bullet.visible = context.shouldBulletRender();
        context.renderIf(muzzle, vertexConsumer, context.notHasMuzzle());
        context.renderIf(mag, vertexConsumer, context.notHasMag());
        context.renderIf(handguard, vertexConsumer, context.notHasHandguard());
        context.renderIf(grip, vertexConsumer, context.notHasGrip());
        context.renderIf(stock, vertexConsumer, context.notHasStock());
        context.renderIf(dust_cover, vertexConsumer, !context.has("dust_cover"));
        context.render(vertexConsumer, slide, safety, body);
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
        }
    }

    @Override
    protected void renderGunLow(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE_LOW));
        context.renderIf(muzzle_low, vertexConsumer, context.notHasMuzzle());
        context.renderIf(mag_low, vertexConsumer, context.notHasMag());
        context.renderIf(handguard_low, vertexConsumer, context.notHasHandguard());
        context.renderIf(grip_low, vertexConsumer, context.notHasGrip());
        context.renderIf(stock_low, vertexConsumer, context.notHasStock());
        context.renderIf(dust_cover_low, vertexConsumer, !context.has("dust_cover"));
        context.render(vertexConsumer, slide_low, safety_low, body_low);
    }

    @Override
    public void renderAttachmentsModel(GunRenderContext context) {
        if (context.useLowQuality()) {
            context.renderAllAttachmentsLeft(lowQualityGun);
        } else {
            context.renderMagAttachmentIf(mag, !context.notHasMag());
            context.renderAllAttachmentsLeft(gun);
        }
    }

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        context.renderBulletShell();
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext gunRenderContext) {
        defaultAssaultRifleAnimation(gunRenderContext, shoot);
        if (gunRenderContext.useLowQuality()) {
            slide_low.copyFrom(slide);
        }
    }

    @Override
    protected void afterRender(GunRenderContext gunRenderContext) {
        gun.resetPose();
        root.resetPose();
        slide.resetPose();
        left_arm.resetPoseAll();
        right_arm.resetPoseAll();
        camera.resetPose();
        mag.resetPose();
        slide_low.resetPose();
    }
}
