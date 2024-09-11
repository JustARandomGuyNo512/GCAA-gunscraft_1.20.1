package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
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
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class AwpModel extends GunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/awp/awp.png");
    private final AnimationDefinition recoil, recoil_ads;
    private ModelPart barrel, front_IS, IS, body, bolt, bolt_back_part, pin, mag, exp_mag, bullet, muzzle;
    private ModelPart s_scope, s_muzzle;

    public AwpModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/awp/awp.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/awp/awp.animation.json"));
        recoil = animations.get("recoil");
        recoil_ads = animations.get("recoil_ads");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        barrel = gun.getChild("barrel").meshing();
        front_IS = gun.getChild("front_IS").meshing();
        IS = gun.getChild("IS").meshing();
        body = gun.getChild("body").meshing();
        bolt = gun.getChild("bolt").meshing();
        bolt_back_part = gun.getChild("bolt_back_part").meshing();
        pin = gun.getChild("pin").meshing();
        mag = gun.getChild("mag").meshing();
        exp_mag = gun.getChild("exp_mag").meshing();
        bullet = gun.getChild("bullet").meshing();
        muzzle = gun.getChild("muzzle").meshing();

        s_scope = gun.getChild("s_scope");
        s_muzzle = gun.getChild("s_muzzle");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        context.renderIf(vertexConsumer, !context.hasScope(), front_IS, IS);
        context.renderIfOrElse(exp_mag, mag, context.hasMag(), vertexConsumer);
        context.render(vertexConsumer, barrel, bolt, bolt_back_part, body, pin, bullet, muzzle);
        context.renderArmLong(left_arm, false);
        context.renderArmLong(right_arm, true);
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        context.renderAllAttachmentsLeft(gun);
    }

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext gunRenderContext) {
        if (gunRenderContext.isFirstPerson) {
            AnimationHandler.INSTANCE.applyRecoil(this);
            AnimationHandler.INSTANCE.applyReload(this);
            AnimationHandler.INSTANCE.applyHandAction(this);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    @Override
    public AnimationDefinition getRecoil() {
        return (Clients.isInAds() && Clients.getAdsProgress() > 0.5f) ? recoil_ads : recoil;
    }


    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name) {
        super.handleSlotTranslate(poseStack, name);
    }

    @Override
    protected void afterRender(GunRenderContext gunRenderContext) {
        root.resetPose();
        gun.resetPose();
        left_arm.resetPose();
        right_arm.resetPose();
        bolt.resetPose();
        bolt_back_part.resetPose();
        pin.resetPose();
        mag.resetPose();
        exp_mag.resetPose();
        camera.resetPose();
    }
}
