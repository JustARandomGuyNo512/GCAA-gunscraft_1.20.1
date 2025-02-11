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
public class Vector45Model extends CommonRifleModel {
    private ModelPart exp_part, front_IS, IS, muzzle;

    public Vector45Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/vector_45/vector_45.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/vector_45/vector_45.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/vector_45/vector_45.png"));
        autoMagVisible = false;
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        front_IS = main.getChild("front_IS");
        IS = main.getChild("IS");
        muzzle = main.getChild("muzzle");
        exp_part = mag.getChild("exp_part");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        muzzle.visible = context.notHasMuzzle();
        IS.visible = context.notHasScope();
        front_IS.visible = IS.visible;
        boolean noMag = context.notHasMag();
        boolean showExp = context.attachmentIs("s_mag", ModItems.VECTOR_45_EXTEND_MAG.get());
        if (noMag || showExp) {
            mag.visible = true;
            exp_part.visible = showExp;
        } else {
            mag.visible = false;
        }
        super.renderGunModel(context);
    }

}
