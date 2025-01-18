package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class FnBallistaModel extends GunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.png");
    private AnimationDefinition recoil, recoil_ads;
    private ModelPart barrel, rail, body, grip, bolt, bolt_back_part, muzzle, mag, mag_exp, IS, bullet, exp_mag_bullet;

    public FnBallistaModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.animation.json"));
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        recoil = animations.get("recoil");
        recoil_ads = animations.get("recoil_ads");
        barrel = gun.getChild("barrel").meshing();
        rail = gun.getChild("rail").meshing();
        body = gun.getChild("body").meshing();
        grip = gun.getChild("grip").meshing();
        bolt = gun.getChild("bolt").meshing();
        bolt_back_part = gun.getChild("bolt_back_part").meshing();
        muzzle = gun.getChild("muzzle").meshing();
        mag = gun.getChild("mag").meshing();
        mag_exp = gun.getChild("mag_exp").meshing();
        IS = gun.getChild("IS").meshing();
        bullet = mag.getChild("bullet").meshing();
        exp_mag_bullet = mag_exp.getChild("exp_mag_bullet").meshing();
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.solid(TEXTURE);
        bullet.visible = context.shouldBulletRender(1500);
        exp_mag_bullet.visible = bullet.visible;
        IS.visible = context.notHasScope();
        muzzle.visible = context.notHasMuzzle();
        mag.visible = context.notHasMag();
        mag_exp.visible = !mag.visible;
        context.render(vertexConsumer, barrel, bolt, bolt_back_part, body, muzzle, mag, mag_exp, IS, grip);
        vertexConsumer = context.solidMipMap(TEXTURE);
        context.render(vertexConsumer, rail);
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
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
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        root.resetPose();
        gun.resetPose();
        left_arm.resetPoseAll();
        right_arm.resetPoseAll();
        bolt.resetPose();
        bolt_back_part.resetPose();
        mag.resetPose();
        mag_exp.resetPose();
        camera.resetPose();
    }

    @Override
    public AnimationDefinition getRecoil(GunRenderContext context) {
        return (Clients.isInAds() && Clients.getAdsProgress() > 0.5f) ? recoil_ads : recoil;
    }
}
