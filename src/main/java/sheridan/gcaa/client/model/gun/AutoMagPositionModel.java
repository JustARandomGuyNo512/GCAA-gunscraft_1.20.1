package sheridan.gcaa.client.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;

public abstract class AutoMagPositionModel extends GunModel{
    public AutoMagPositionModel(ResourceLocation modelPath, ResourceLocation animationPath) {
        super(modelPath, animationPath);
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return "s_mag".equals(modelSlotName) || super.hasSlot(modelSlotName);
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name, IGun gun)  {
        if (name.equals("s_mag")) {
            handleGunTranslate(poseStack);
            getMag().translateAndRotate(poseStack);
            return;
        }
        super.handleSlotTranslate(poseStack, name, gun);
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        context.renderMagAttachmentIf(getMag(), !context.notHasMag());
    }

    protected abstract ModelPart getMag();
}
