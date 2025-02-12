package sheridan.gcaa.client.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class PumpShotGunModel extends AutoShotGunModel{
    protected ModelPart handguard;

    public PumpShotGunModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture, @Nullable ResourceLocation lowQualityModelPath, @Nullable ResourceLocation lowQualityTexture) {
        super(modelPath, animationPath, texture, lowQualityModelPath, lowQualityTexture);
    }

    public PumpShotGunModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture) {
        super(modelPath, animationPath, texture);
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        handguard = main.getChild("handguard");
    }


    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson) {
            AnimationHandler.INSTANCE.applyRecoil(this);
            AnimationHandler.INSTANCE.applyReload(this);
            AnimationHandler.INSTANCE.applyHandAction(this);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name, IGun gun) {
        if ("s_handguard".equals(name)) {
            handleGunTranslate(poseStack);
            handguard.translateAndRotate(poseStack);
            return;
        }
        super.handleSlotTranslate(poseStack, name, gun);
    }

    @Override
    protected void renderGunModelLowQuality(GunRenderContext context) {}
}
