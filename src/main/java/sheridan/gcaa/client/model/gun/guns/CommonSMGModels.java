package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class CommonSMGModels {
    public static final CommonRifleModel MP5_MODEL = new CommonRifleModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/mp5/mp5.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/mp5/mp5.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/mp5/mp5.png"));

    public static final CommonRifleModel ANNIHILATOR_MODEL = new CommonRifleModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/annihilator/annihilator.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/annihilator/annihilator.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/annihilator/annihilator.png")) {
        @Override
        public AnimationDefinition getRecoil(GunRenderContext context) {
            return Clients.getAdsProgress() > 0.5f ? animations.get("recoil_ads") : animations.get("recoil");
        }

        @Override
        protected void animationGlobal(GunRenderContext context) {
            super.animationGlobal(context);
            if (context.isFirstPerson) {
                AnimationHandler.INSTANCE.applyRecoil(this);
            }
        }
    };
}
