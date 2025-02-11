package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class Ak12Model extends CommonRifleModel {
    private ModelPart IS, muzzle, stock, rail;

    public Ak12Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        IS = main.getChild("IS");
        muzzle = main.getChild("muzzle");
        stock = main.getChild("stock");
        rail = main.getChild("handguard").getChild("rail");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        IS.visible = context.notContainsScope();
        muzzle.visible = context.notHasMuzzle();
        stock.visible = context.notHasStock();
        rail.visible = context.has("handguard_left") || context.has("handguard_right");
        super.renderGunModel(context);
    }

}
