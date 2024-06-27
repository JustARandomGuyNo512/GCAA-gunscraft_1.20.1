package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public abstract class GCAAStyleGunModel extends HierarchicalModel<Entity> implements IGunModel {
    @Override
    public void render(GunRenderContext gunRenderContext) {
        PoseStack poseStack = gunRenderContext.poseStack;
        getRoot().translateAndRotate(poseStack);
        ModelPart leftArm = ReloadingHandler.INSTANCE.reloading() ? getReloadingArm() : getLeftArm();
        boolean longArm = longArm();
        if (longArm) {
            gunRenderContext.renderArmLong(getRightArm(), true);
            gunRenderContext.renderArmLong(leftArm, false);
        } else {
            gunRenderContext.renderArm(getRightArm(), true);
            gunRenderContext.renderArm(leftArm, false);
        }
        getGunLayer().translateAndRotate(poseStack);
        renderGunModel(gunRenderContext);
        renderAttachmentsModel(gunRenderContext);
    }

    @Override
    public void handleGunTranslate(PoseStack poseStack) {}

    @Override
    public ModelPart root() {
        return getRoot();
    }

    public abstract ModelPart getRoot();

    public abstract ModelPart getLeftArm();

    public abstract ModelPart getRightArm();

    public abstract ModelPart getGunLayer();

    public abstract ModelPart getReloadingArm();

    protected abstract boolean longArm();

    public abstract void renderGunModel(GunRenderContext context);
    public abstract void renderAttachmentsModel(GunRenderContext context);
}
