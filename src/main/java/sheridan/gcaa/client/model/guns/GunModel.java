package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.Map;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public abstract class GunModel extends HierarchicalModel<Entity> implements IGunModel {
    public final ModelPart root;
    public final ModelPart gun;
    public final ModelPart left_arm;
    public final ModelPart right_arm;
    public ModelPart camera;
    Map<String, AnimationDefinition> animations;

    public GunModel(ResourceLocation modelPath, ResourceLocation animationPath) {
        this.root = ArsenalLib.loadBedRockGunModel(modelPath).bakeRoot().getChild("root");
        camera = this.root.getChild("camera");
        gun = this.root.getChild("gun");
        left_arm = this.gun.getChild("left_arm");
        right_arm = this.gun.getChild("right_arm");
        postInit(gun, root);
        animations = ArsenalLib.loadBedRockAnimationWithSound(animationPath);
    }

    @Override
    public void render(GunRenderContext gunRenderContext) {
        if (gunRenderContext.isFirstPerson) {
            Object original = gunRenderContext.getLocalSaved(GunRenderContext.ORIGINAL_GUN_VIEW_POSE_FP);
            if (original instanceof PoseStack stack) {
                handleGunTranslate(stack);
                gunRenderContext.saveInLocal(GunRenderContext.ORIGINAL_GUN_VIEW_POSE_FP, stack);
            }
        }
        animationGlobal(gunRenderContext);
        handleGunTranslate(gunRenderContext.poseStack);
        renderGunModel(gunRenderContext);
        renderAttachmentsModel(gunRenderContext);
        renderPostEffect(gunRenderContext);
        afterRender(gunRenderContext);
    }

    protected abstract void postInit(ModelPart gun, ModelPart root);
    protected abstract void renderGunModel(GunRenderContext context);
    protected abstract void renderAttachmentsModel(GunRenderContext context);
    protected abstract void renderPostEffect(GunRenderContext context);
    protected abstract void animationGlobal(GunRenderContext context);
    protected abstract void afterRender(GunRenderContext context);

    @Override
    public void handleGunTranslate(PoseStack poseStack) {
        root.translateAndRotate(poseStack);
        gun.translateAndRotate(poseStack);
    }

    @Override
    public AnimationDefinition getRecoil() {
        return null;
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name) {
        ModelPart slot = gun.getChild(name);
        handleGunTranslate(poseStack);
        slot.translateAndRotate(poseStack);
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return gun.hasChildRecursive(modelSlotName);
    }

    @Override
    public AnimationDefinition getReload() {
        return animations.get("reload");
    }

    @Override
    public AnimationDefinition getFullReload() {
        return animations.get("full_reload");
    }

    @Override
    public AnimationDefinition get(String name) {
        return animations.get(name);
    }

    @Override
    public Optional<ModelPart> getAnyDescendantWithName(String pName) {
        return super.getAnyDescendantWithName(pName);
    }

    protected void defaultAssaultRifleAnimation(GunRenderContext gunRenderContext, AnimationDefinition recoil, AnimationDefinition shoot)  {
        if (gunRenderContext.isFirstPerson || gunRenderContext.isThirdPerson()) {
            KeyframeAnimations.animate(this, recoil, gunRenderContext.lastShoot,1);
            KeyframeAnimations.animate(this, shoot, gunRenderContext.lastShoot,1);
            if (gunRenderContext.isFirstPerson) {
                AnimationHandler.INSTANCE.applyReload(this);
                CameraAnimationHandler.INSTANCE.mix(camera);
            }
        }
    }
}
