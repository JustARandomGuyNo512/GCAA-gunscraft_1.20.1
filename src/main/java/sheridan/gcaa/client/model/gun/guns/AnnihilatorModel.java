package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class AnnihilatorModel extends CommonRifleModel {
    public AnnihilatorModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/annihilator/annihilator.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/annihilator/annihilator.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/annihilator/annihilator.png"));
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson) {
            AnimationHandler.INSTANCE.applyReload(this);
            AnimationHandler.INSTANCE.applyRecoil(this);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    @Override
    public AnimationDefinition getRecoil(GunRenderContext context) {
        return animations.get("shoot");
    }
}
