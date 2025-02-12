package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.SniperModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class AwpModel extends SniperModel {
    protected ModelPart IS, front_IS, muzzle;
    public AwpModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/awp/awp.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/awp/awp.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/awp/awp.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        IS = main.getChild("IS");
        front_IS = main.getChild("front_IS");
        muzzle = main.getChild("muzzle");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        IS.visible = context.notHasScope();
        front_IS.visible = IS.visible;
        muzzle.visible = context.notHasMuzzle();
        super.renderGunModel(context);
    }
}
