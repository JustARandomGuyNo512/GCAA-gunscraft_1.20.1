package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class AkmModel extends GunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.png");
    private ModelPart
            barrel, rail_set, slide,
            muzzle, handguard, IS,
            dust_cover, mag, grip,
            safety, body, stock, bullet;


    private final AnimationDefinition recoil;
    private final AnimationDefinition shoot;

    public AkmModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.animation.json"));
        recoil = animations.get("recoil");
        shoot = animations.get("shoot");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        barrel = gun.getChild("barrel").meshing();
        rail_set = gun.getChild("rail_set").meshing();
        slide = gun.getChild("slide").meshing();
        muzzle = gun.getChild("muzzle").meshing();
        handguard = gun.getChild("handguard").meshing();
        IS = gun.getChild("IS").meshing();
        dust_cover = gun.getChild("dust_cover").meshing();
        grip = gun.getChild("grip").meshing();
        safety = gun.getChild("safety").meshing();
        body = gun.getChild("body").meshing();
        stock = gun.getChild("stock").meshing();
        mag = gun.getChild("mag").meshing();
        bullet = mag.getChild("bullet").meshing();
    }

    @Override
    public void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        bullet.visible = ReloadingHandler.isReloading() || ReloadingHandler.disFromLastReload(1000);
        context.renderIf(muzzle, vertexConsumer, !context.hasMuzzle());
        context.renderIf(mag, vertexConsumer, !context.hasMag());
        context.renderIf(handguard, vertexConsumer, !context.hasHandguard());
        context.renderIf(grip, vertexConsumer, !context.hasGrip());
        context.renderIf(stock, vertexConsumer, !context.hasStock());
        context.renderIf(dust_cover, vertexConsumer, !context.has("dust_cover"));
        context.render(vertexConsumer, barrel, rail_set, slide, IS, safety, body);
        context.renderArmLong(left_arm, false);
        context.renderArmLong(right_arm, true);
    }

    @Override
    public void renderAttachmentsModel(GunRenderContext context) {
        context.renderAllAttachmentsLeft(gun);
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext gunRenderContext) {
        if (gunRenderContext.isFirstPerson || gunRenderContext.isThirdPerson()) {
            KeyframeAnimations.animate(this, recoil, gunRenderContext.lastShoot,1);
            KeyframeAnimations.animate(this, shoot, gunRenderContext.lastShoot,1);
            if (gunRenderContext.isFirstPerson) {
                AnimationHandler.INSTANCE.applyReload(this);
                CameraAnimationHandler.INSTANCE.mix(camera);
            }
        }
    }

    @Override
    protected void afterRender(GunRenderContext gunRenderContext) {
        gun.resetPose();
        root.resetPose();
        slide.resetPose();
        left_arm.resetPose();
        right_arm.resetPose();
        camera.resetPose();
        mag.resetPose();
    }

}
