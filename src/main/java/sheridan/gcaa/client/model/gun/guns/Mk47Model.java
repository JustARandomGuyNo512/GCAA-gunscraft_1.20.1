package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class Mk47Model extends CommonRifleModel {
    private ModelPart handguard;

    public Mk47Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/mk47/mk47.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/mk47/mk47.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/mk47/mk47.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        handguard = main.getChild("handguard");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        if (handguard.visible) {
            context.render(handguard, context.solidNoCullMipMap(texture));
            handguard.visible = false;
        }
        super.renderGunModel(context);
    }
}
