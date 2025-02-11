package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class AnnihilatorModel extends GunModel {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/annihilator/annihilator.png");
    private AnimationDefinition shoot, shoot_ads;
    private ModelPart main, slide, mag;

    public AnnihilatorModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/annihilator/annihilator.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/annihilator/annihilator.animation.json"));
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        shoot = animations.get("shoot");
        shoot_ads = animations.get("shoot_ads");
        main = gun.getChild("main_part");
        slide = main.getChild("slide").meshing();
        mag = main.getChild("mag").meshing();
        mag.getChild("bullet").meshing();
        main.getChild("body").meshing();
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.solid(TEXTURE);
        if (context.ammoLeft == 0 && !ReloadingHandler.isReloading()) {
            slide.addZ(16);
        }
        context.render(main, vertexConsumer);
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
        }
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {}

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        context.renderBulletShell();
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson) {
            AnimationHandler.INSTANCE.applyReload(this);
            AnimationHandler.INSTANCE.applyRecoil(this);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        left_arm.resetPoseAll();
        right_arm.resetPoseAll();
        camera.resetPose();
        gun.resetPoseAll();
    }

    @Override
    public AnimationDefinition getRecoil(GunRenderContext context) {
        return (Clients.isInAds() && Clients.getAdsProgress() > 0.5f) ? shoot_ads : shoot;
    }
}
