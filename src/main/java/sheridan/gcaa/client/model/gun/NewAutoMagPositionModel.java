package sheridan.gcaa.client.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public abstract class NewAutoMagPositionModel extends NewGunModel{
    protected ModelPart mag;

    public NewAutoMagPositionModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture, @Nullable ResourceLocation lowQualityModelPath, @Nullable ResourceLocation lowQualityTexture) {
        super(modelPath, animationPath, texture, lowQualityModelPath, lowQualityTexture);
    }

    public NewAutoMagPositionModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture) {
        super(modelPath, animationPath, texture);
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        mag = main.getChild("mag");
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        if ("s_mag".equals(modelSlotName)) {
            return true;
        }
        return super.hasSlot(modelSlotName);
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name, IGun gun)  {
        if (name.equals("s_mag")) {
            handleGunTranslate(poseStack);
            mag.translateAndRotate(poseStack);
            return;
        }
        super.handleSlotTranslate(poseStack, name, gun);
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        context.renderMagAttachmentIf(mag, !context.notHasMag());
        super.renderAttachmentsModel(context);
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        super.afterRender(context);
        mag.resetPose();
    }
}
