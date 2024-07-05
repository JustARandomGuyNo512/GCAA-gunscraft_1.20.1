package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.model.modelPart.*;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AkmModel extends GCAAStyleGunModel{
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.png");
    private final ModelPart root;
    private final ModelPart gun;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart _reloading;
    private final ModelPart reloading_arm;

    private final ModelPart barrel, rail_set, slide,
            muzzle, handguard, IS,
            dust_cover, mag, grip,
            safety, body, stock;

    private final AnimationDefinition recoil;
    private final AnimationDefinition shoot;

    public AkmModel() {
        this.root = ArsenalLib.loadBedRockGunModel(
                        new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.geo.json"))
                .bakeRoot().getChild("root");
        gun = root.getChild("gun");
        right_arm = root.getChild("right_arm");
        left_arm = root.getChild("left_arm");
        _reloading = gun.getChild("_reloading");
        mag = _reloading.getChild("mag");
        reloading_arm = _reloading.getChild("reloading_arm");
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

        Map<String, AnimationDefinition> animations = ArsenalLib.loadBedRockAnimation(new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.animation.json"));
        recoil = animations.get("recoil");
        shoot = animations.get("shoot");
    }

    @Override
    public void handleGunTranslate(PoseStack poseStack) {
        root.translateAndRotate(poseStack);
        gun.translateAndRotate(poseStack);
    }

    @Override
    public AnimationDefinition getRecoilAnimation() {
        return null;
    }

    @Override
    public ModelPart getRoot() {
        return root;
    }

    @Override
    public ModelPart getGunLayer() {
        return gun;
    }

    @Override
    public ModelPart getReloadingArm() {
        return reloading_arm;
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
    protected void animationGlobal(GunRenderContext gunRenderContext) {
        if (gunRenderContext.isFirstPerson) {
            KeyframeAnimations.animate(this, recoil, Clients.mainHandStatus.lastShoot, 0, 1, KeyframeAnimations.DEFAULT_DIRECTION);
            KeyframeAnimations.animate(this, shoot, Clients.mainHandStatus.lastShoot, 0, 1, KeyframeAnimations.DEFAULT_DIRECTION);
        }
    }

    @Override
    public void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        context.render(vertexConsumer, barrel, rail_set, slide, muzzle, handguard, IS, dust_cover, grip, safety, body, stock);
        context.pushPose().translateAndRotateTo(_reloading);
        context.render(mag, vertexConsumer);
        context.popPose();
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    public void renderAttachmentsModel(GunRenderContext context) {

    }

    @Override
    protected void afterRender(GunRenderContext gunRenderContext) {
        root.resetPose();
        slide.resetPose();
    }

    @Override
    public ModelPart getReloadingLayer() {
        return _reloading;
    }

    @Override
    protected boolean longArm() {
        return true;
    }
}
