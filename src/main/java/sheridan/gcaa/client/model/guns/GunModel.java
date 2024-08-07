package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;

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
        PoseStack poseStack = gunRenderContext.poseStack;
        animationGlobal(gunRenderContext);
        root.translateAndRotate(poseStack);
        gun.translateAndRotate(poseStack);
        renderGunModel(gunRenderContext);
        renderAttachmentsModel(gunRenderContext);
        afterRender(gunRenderContext);
    }

    protected abstract void postInit(ModelPart gun, ModelPart root);
    protected abstract void renderGunModel(GunRenderContext context);
    protected abstract void renderAttachmentsModel(GunRenderContext context);
    protected abstract void animationGlobal(GunRenderContext gunRenderContext);
    protected abstract void afterRender(GunRenderContext gunRenderContext);

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
    public ModelPart root() {
        return root;
    }

    @Override
    public ModelPart getSlotPart(String name) {
        return gun.getChild(name);
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name) {
        ModelPart slot = getSlotPart(name);
        if (slot != null) {
            handleGunTranslate(poseStack);
            slot.translateAndRotate(poseStack);
        }
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
}
