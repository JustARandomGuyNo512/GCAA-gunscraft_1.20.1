package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.ModItems;

@OnlyIn(Dist.CLIENT)
public class Mp5Model extends CommonRifleModel {
    private ModelPart stock, stock_ar, handguard, handguard_rail;

    public Mp5Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/mp5/mp5.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/mp5/mp5.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/mp5/mp5.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        stock = main.getChild("stock");
        stock_ar = main.getChild("stock_ar");
        handguard = main.getChild("handguard");
        handguard_rail = main.getChild("handguard_railed");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        boolean stockIsArTube = context.attachmentIs("s_stock", ModItems.AR_STOCK_TUBE.get());
        if (stockIsArTube) {
            stock_ar.visible = true;
            stock.visible = false;
        } else {
            stock_ar.visible = false;
            stock.visible = true;
        }
        handguard.visible = context.notHasHandguard();
        handguard_rail.visible = !handguard.visible;
        super.renderGunModel(context);
    }
}
