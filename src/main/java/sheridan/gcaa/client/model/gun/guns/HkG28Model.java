package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.gun.DMRModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class HkG28Model extends DMRModel {
    private ModelPart IS_front, muzzle, stock, sub_rail_left, sub_rail_right, sub_rail_down, IS;

    public HkG28Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        IS_front = main.getChild("handguard").getChild("IS_front");
        muzzle = main.getChild("muzzle");
        stock = main.getChild("stock");
        IS = main.getChild("IS");
        sub_rail_left = main.getChild("handguard").getChild("sub_rail_left");
        sub_rail_right = main.getChild("handguard").getChild("sub_rail_right");
        sub_rail_down = main.getChild("handguard").getChild("sub_rail_down");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        IS_front.xRot = context.notContainsScope() ? 0 : 1.57079632679489655f;
        IS.visible = context.notContainsScope();
        muzzle.visible = context.notHasMuzzle();
        stock.visible = context.notHasStock();
        sub_rail_left.visible = context.has("handguard_front_left");
        sub_rail_right.visible = context.has("handguard_front_right");
        sub_rail_down.visible = context.has("handguard_front");
        super.renderGunModel(context);
    }

    @Override
    protected VertexConsumer getDefaultVertex(GunRenderContext context) {
        return context.solidMipMap(texture);
    }
}
