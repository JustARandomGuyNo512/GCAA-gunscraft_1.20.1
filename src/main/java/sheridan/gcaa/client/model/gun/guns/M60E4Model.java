package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.MGModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class M60E4Model extends MGModel {
    private ModelPart IS, muzzle;
    public M60E4Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/m60e4/m60e4.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m60e4/m60e4.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m60e4/m60e4.png"),
                9, 2400, false);
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        IS = main.getChild("IS");
        muzzle = main.getChild("muzzle");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        IS.xRot = context.notContainsScope() ? 0 : 1.57079632679489655f;
        muzzle.visible = context.notHasMuzzle();
        super.renderGunModel(context);
    }
}
