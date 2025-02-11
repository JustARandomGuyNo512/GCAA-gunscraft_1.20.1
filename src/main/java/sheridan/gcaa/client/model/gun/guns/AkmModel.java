package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.fireModes.Auto;

@OnlyIn(Dist.CLIENT)
public class AkmModel extends CommonRifleModel {
    private ModelPart muzzle, handguard, dust_cover, grip, safety, stock, dust_cover_low, mag_low;

    public AkmModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.png"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm_low.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm_low.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        muzzle = main.getChild("muzzle");
        handguard = main.getChild("handguard");
        dust_cover = main.getChild("dust_cover");
        grip = main.getChild("grip");
        safety = main.getChild("safety");
        stock = main.getChild("stock");
        dust_cover_low = lowQualityMain.getChild("dust_cover");
        mag_low = lowQualityMain.getChild("mag");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        if (context.isFirstPerson) {
            safety.xRot = Clients.MAIN_HAND_STATUS.fireMode == Auto.AUTO ? 0.2181661564992911875f : 0.436332312998582375f;
        } else {
            safety.xRot = 0;
        }
        muzzle.visible = context.notHasMuzzle();
        handguard.visible = context.notHasHandguard();
        dust_cover.visible = !context.has("dust_cover");
        grip.visible = context.notHasGrip();
        stock.visible = context.notHasStock();
        super.renderGunModel(context);
    }

    @Override
    protected void renderGunModelLowQuality(GunRenderContext context) {
        dust_cover_low.visible = !context.has("dust_cover");
        mag_low.visible = context.notHasMag();
        super.renderGunModelLowQuality(context);
    }
}
