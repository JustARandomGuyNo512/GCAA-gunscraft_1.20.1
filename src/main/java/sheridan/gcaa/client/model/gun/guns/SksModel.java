package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.model.gun.AutoShotGunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

public class SksModel extends AutoShotGunModel {
    private ModelPart bolt, bullet;
    public SksModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/sks/sks.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/sks/sks.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/sks/sks.png"), null, null);
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        bolt = main.getChild("bolt");
        bullet = main.getChild("bullet");
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        defaultAnimation(context);
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        if (context.isFirstPerson && context.ammoLeft == 0 && !ReloadingHandler.isReloading()) {
            bolt.setZ(bolt.getInitialPose().z + 14f);
        }
        bullet.visible = context.isFirstPerson && ReloadingHandler.isReloading() && context.ammoLeft > 0;
        super.renderGunModel(context);
    }
}
