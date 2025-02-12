package sheridan.gcaa.client.model.gun;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class DMRModel extends CommonRifleModel{
    private AnimationDefinition recoil;
    protected float adsRecoilAnimationScaleAds = 0.35f;

    public DMRModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture,
                    @Nullable ResourceLocation lowQualityModelPath, @Nullable ResourceLocation lowQualityTexture) {
        super(modelPath, animationPath, texture, lowQualityModelPath, lowQualityTexture);
    }

    public DMRModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture) {
        super(modelPath, animationPath, texture);
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        recoil = animations.get("recoil");
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson && recoil != null) {
            float scale = Mth.lerp(Clients.getAdsProgress(), 1f, adsRecoilAnimationScaleAds);
            KeyframeAnimations.animate(this, recoil, context.lastShoot, scale);
        }
        super.animationGlobal(context);
    }
}
