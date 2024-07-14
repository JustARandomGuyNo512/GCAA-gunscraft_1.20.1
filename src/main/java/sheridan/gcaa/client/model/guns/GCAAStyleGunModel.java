package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public abstract class GCAAStyleGunModel extends HierarchicalModel<Entity> implements IGunModel {
    public static final String SLOT_PREFIX = "s_";
    public final ModelPart root;
    public final ModelPart gun;
    public final ModelPart left_arm;
    public final ModelPart right_arm;
    public ModelPart camera;

    public GCAAStyleGunModel(ModelPart root) {
        this.root = root;
        camera = this.root.getChild("camera");
        gun = this.root.getChild("gun");
        left_arm = this.gun.getChild("left_arm");
        right_arm = this.gun.getChild("right_arm");
        postInit(gun, root);
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
        return gun.getChild(SLOT_PREFIX + name);
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name) {
        ModelPart slot = getSlotPart(name);
        if (slot != null) {
            handleGunTranslate(poseStack);
            slot.translateAndRotate(poseStack);
        }
    }
}
