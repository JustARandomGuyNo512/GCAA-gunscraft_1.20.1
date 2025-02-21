package sheridan.gcaa.client.model.gun;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class SniperModel extends AutoMagPositionModel {
    protected ModelPart exp_mag, exp_mag_bullet, bullet;
    protected AnimationDefinition recoil, recoil_ads;
    public SniperModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture, @Nullable ResourceLocation lowQualityModelPath, @Nullable ResourceLocation lowQualityTexture) {
        super(modelPath, animationPath, texture, lowQualityModelPath, lowQualityTexture);
    }

    public SniperModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture) {
        super(modelPath, animationPath, texture);
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        recoil = animations.get("recoil");
        recoil_ads = animations.get("recoil_ads");
        exp_mag = main.getChild("exp_mag");
        exp_mag_bullet = exp_mag.getChild("exp_mag_bullet");
        bullet = mag.getChild("bullet");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer defaultVertex = getDefaultVertex(context);
        mag.visible = context.notHasMag();
        exp_mag.visible = !mag.visible;
        if (mag.visible) {
            bullet.visible = ReloadingHandler.isReloading();
        } else {
            exp_mag_bullet.visible = ReloadingHandler.isReloading();
        }
        context.render(main, defaultVertex);
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
        }
    }

    @Override
    protected void animationGlobal(GunRenderContext gunRenderContext) {
        if (gunRenderContext.isFirstPerson) {
            AnimationHandler.INSTANCE.applyRecoil(this);
            AnimationHandler.INSTANCE.applyReload(this);
            AnimationHandler.INSTANCE.applyHandAction(this);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    @Override
    public AnimationDefinition getRecoil(GunRenderContext context) {
        return (Clients.isInAds() && Clients.getAdsProgress() > 0.5f) ? recoil_ads : recoil;
    }
}
