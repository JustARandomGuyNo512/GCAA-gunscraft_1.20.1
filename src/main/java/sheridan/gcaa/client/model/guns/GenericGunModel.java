package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public abstract class GenericGunModel extends HierarchicalModel<Entity> implements IGunModel{
    @Override
    public void render(GunRenderContext gunRenderContext) {
        boolean longArm = longArm();
        PoseStack poseStack = gunRenderContext.poseStack;
        root().translateAndRotate(poseStack);
        poseStack.pushPose();
        getGunArmLayer().translateAndRotate(poseStack);
        renderArm(longArm, gunRenderContext, true);
        getGunLayer().translateAndRotate(poseStack);
        renderGunModel(gunRenderContext);
        renderAttachmentsModel(gunRenderContext);
        poseStack.popPose();
        renderArm(longArm, gunRenderContext, false);
    }

    final void renderArm(boolean longArm, GunRenderContext gunRenderContext, boolean mainHand) {
        ModelPart arm = mainHand ? getRightArm() : getLeftArm();
        if (longArm) {
            gunRenderContext.renderArmLong(arm, mainHand);
        } else {
            gunRenderContext.renderArm(arm, mainHand);
        }
    }

    @Override
    public void handleGunTranslate(PoseStack poseStack) {}

    @Override
    public final ModelPart root() {
        return getRoot();
    }

    public abstract ModelPart getRoot();

    public abstract ModelPart getGunArmLayer();

    public abstract ModelPart getGunLayer();

    public abstract ModelPart getLeftArm();

    public abstract ModelPart getRightArm();

    public abstract void renderGunModel(GunRenderContext context);
    public abstract void renderAttachmentsModel(GunRenderContext context);

    protected abstract boolean longArm();
}
