package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.NewPlayerArmRenderer;

@OnlyIn(Dist.CLIENT)
public class Xm1014Model extends GunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.png");
    private final AnimationDefinition recoil, recoil_ads;
    private final AnimationDefinition shoot;

    private ModelPart IS, handguard, stock, barrel, mag, body, slide, ammo_track, reloading_arm, shell;

    public Xm1014Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.animation.json"));
        recoil = animations.get("recoil");
        shoot = animations.get("shoot");
        recoil_ads = animations.get("recoil_ads");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        this.IS = gun.getChild("IS").meshing();
        this.handguard = gun.getChild("handguard").meshing();
        this.stock = gun.getChild("stock").meshing();
        this.barrel = gun.getChild("barrel").meshing();
        this.mag = gun.getChild("mag").meshing();
        this.body = gun.getChild("body").meshing();
        this.slide = gun.getChild("slide").meshing();
        this.ammo_track = gun.getChild("ammo_track").meshing();
        this.reloading_arm = gun.getChild("reloading_arm").meshing();
        this.shell = reloading_arm.getChild("shell").meshing();
        this.reloading_arm.resetChildLayerName("right_arm_slim2", "right_arm_slim");
        this.reloading_arm.addChild("left_arm_slim", this.reloading_arm.getChild("right_arm_slim"));
        this.reloading_arm.resetChildLayerName("right_arm_normal2", "right_arm_normal");
        this.reloading_arm.addChild("left_arm_normal", this.reloading_arm.getChild("right_arm_normal"));
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        ModelPart leftArm = left_arm.xScale > 0 ? left_arm : reloading_arm;
        ModelPart rightArm = right_arm.xScale > 0 ? right_arm : reloading_arm;
        if (context.isFirstPerson && (leftArm == reloading_arm ||rightArm == reloading_arm)) {
            context.pushPose().translateTo(reloading_arm).render(shell, vertexConsumer);
            context.popPose();
        }
        context.render(vertexConsumer, IS, handguard, stock, barrel, mag, body, slide, ammo_track);
        if (context.isFirstPerson) {
            context.renderArm(rightArm, true);
            context.renderArm(leftArm, false);
        }
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        context.renderAllAttachmentsLeft(gun);
    }

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        context.renderBulletShell();
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson) {
            AnimationHandler.INSTANCE.applyRecoil(this);
            AnimationHandler.INSTANCE.applyReload(this);
            AnimationHandler.INSTANCE.applyHandAction(this);
            KeyframeAnimations.animate(this, shoot, context.lastShoot,1);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    @Override
    public AnimationDefinition getRecoil() {
        return Clients.getAdsProgress() > 0.5 ? recoil_ads : recoil;
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        gun.resetPose();
        slide.resetPose();
        left_arm.resetPoseAll();
        right_arm.resetPoseAll();
        reloading_arm.resetPoseAll();
        camera.resetPose();
    }
}
