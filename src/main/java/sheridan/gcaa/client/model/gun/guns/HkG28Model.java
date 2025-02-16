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
import sheridan.gcaa.items.attachments.Attachment;

@OnlyIn(Dist.CLIENT)
public class HkG28Model extends DMRModel {
    private ModelPart IS_front;

    public HkG28Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        bindSlotReplacement(GunRenderContext.SCOPE_ALL,  "IS");
        bindSlotReplacement(Attachment.STOCK,  "stock");
        bindSlotReplacement(Attachment.MUZZLE,  "muzzle");
        bindSlotReplacement("handguard_front_left", false,  "handguard/sub_rail_left");
        bindSlotReplacement("handguard_front_right", false,  "handguard/sub_rail_right");
        bindSlotReplacement("handguard_front", false,  "handguard/sub_rail_down");
        IS_front = main.getChild("handguard").getChild("IS_front");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        IS_front.xRot = context.notContainsScope() ? 0 : 1.57079632679489655f;
        super.renderGunModel(context);
    }

    @Override
    protected VertexConsumer getDefaultVertex(GunRenderContext context) {
        return context.solidMipMap(texture);
    }
}
