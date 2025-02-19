package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.guns.Beretta686;

@OnlyIn(Dist.CLIENT)
public class Beretta686Model extends GunModel {
    private AnimationDefinition recoil, recoil_volley;
    public Beretta686Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/beretta_686/beretta_686.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/beretta_686/beretta_686.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/beretta_686/beretta_686.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        recoil = animations.get("recoil");
        recoil_volley = animations.get("recoil_volley");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = getDefaultVertex(context);
        context.render(vertexConsumer, main);
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
        }
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
    protected void renderGunModelLowQuality(GunRenderContext context) {}

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        float scale = 1;
        if (context.isFirstPerson && System.currentTimeMillis() - context.lastShoot < 100) {
            if (Clients.MAIN_HAND_STATUS.fireMode instanceof Beretta686.Volley && context.ammoLeft >= 1) {
                scale *= 1.8f;
            }
        }
        context.renderMuzzleFlash(scale);
    }

    @Override
    public AnimationDefinition getRecoil(GunRenderContext context) {
        return (Clients.MAIN_HAND_STATUS.fireMode instanceof Beretta686.Volley && context.ammoLeft >= 1) ? recoil_volley : recoil;
    }
}
