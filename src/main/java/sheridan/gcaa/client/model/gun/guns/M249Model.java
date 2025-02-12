package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.MGModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class M249Model extends MGModel {
    private ModelPart muzzle, handguard, railed_handguard;

    public M249Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/m249/m249.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m249/m249.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m249/m249.png"),
                11, 2400, true);
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        muzzle = main.getChild("muzzle");
        handguard = main.getChild("handguard");
        railed_handguard = main.getChild("railed_handguard");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        handguard.visible = context.notHasHandguard();
        muzzle.visible = context.notHasMuzzle();
        railed_handguard.visible = !handguard.visible;
        super.renderGunModel(context);
    }
}
