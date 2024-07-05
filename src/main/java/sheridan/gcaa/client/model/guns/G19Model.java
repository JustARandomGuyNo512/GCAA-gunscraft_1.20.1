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
import sheridan.gcaa.client.animation.recoilAnimation.RecoilAnimationHandler;
import sheridan.gcaa.client.model.modelPart.*;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class G19Model extends GCAAStyleGunModel{
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
    private final ModelPart reloading_arm;
    private final ModelPart _reloading;
    private final ModelPart gun;

    private final AnimationDefinition recoil;
    private final AnimationDefinition shoot;

    public G19Model() {
        this.root = ArsenalLib.loadBedRockGunModel(
                new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.geo.json"))
                .bakeRoot().getChild("root");
        left_arm = root.getChild("left_arm");
        gun = root.getChild("gun");
        right_arm = root.getChild("right_arm");
        _reloading = gun.getChild("_reloading");
        reloading_arm = _reloading.getChild("reloading_arm");
        barrel = gun.getChild("barrel").meshing();
        muzzle_point = barrel.getChild("muzzle_point");
        grid = gun.getChild("grid").meshing();
        slide = gun.getChild("slide").meshing();
        mag = _reloading.getChild("mag").meshing();
        mag_point = mag.getChild("mag_point");

        Map<String, AnimationDefinition> animations = ArsenalLib.loadBedRockAnimation(new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.animation.json"));
        recoil = animations.get("recoil");
        shoot = animations.get("shoot");
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
    public ModelPart getReloadingLayer() {
        return _reloading;
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
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson) {
            RecoilAnimationHandler.INSTANCE.handleRecoil(this);
            KeyframeAnimations.animate(this, shoot, Clients.mainHandStatus.lastShoot, 0, 1, KeyframeAnimations.DEFAULT_DIRECTION);
        }
    }

    @Override
    protected void afterRender(GunRenderContext gunRenderContext) {
        root.resetPose();
        slide.resetPose();
        barrel.resetPose();
    }

    @Override
    public void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        context.render(vertexConsumer, barrel, slide, grid);
        context.pushPose().translateAndRotateTo(_reloading);
        context.render(mag, vertexConsumer);
        context.popPose();
    }

    @Override
    public void renderAttachmentsModel(GunRenderContext context) {

    }

    @Override
    protected boolean longArm() {
        return false;
    }

    @Override
    public void handleGunTranslate(PoseStack poseStack) {
        root.translateAndRotate(poseStack);
        gun.translateAndRotate(poseStack);
    }

    @Override
    public AnimationDefinition getRecoilAnimation() {
        return recoil;
    }
}
