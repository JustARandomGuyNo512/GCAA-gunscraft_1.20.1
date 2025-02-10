package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class M4a1Model extends CommonRifleModel {
    private ModelPart IS, muzzle, handguard, front_IS, stock;

    public M4a1Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        IS = main.getChild("IS");
        muzzle = main.getChild("muzzle");
        handguard = main.getChild("handguard");
        front_IS = main.getChild("front_IS");
        stock = main.getChild("stock");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        IS.visible = context.notContainsScope();
        muzzle.visible = context.notHasMuzzle();
        handguard.visible = context.notHasHandguard();
        front_IS.visible = !context.has("gas_block");
        stock.visible = context.notHasStock();
        super.renderGunModel(context);
    }
}
