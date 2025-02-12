package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.SniperModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class FnBallistaModel extends SniperModel {
    private ModelPart IS, muzzle;
    public FnBallistaModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        IS = main.getChild("IS");
        muzzle = main.getChild("muzzle");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        IS.visible = context.notContainsScope();
        muzzle.visible = context.notHasMuzzle();
        super.renderGunModel(context);
    }
}
