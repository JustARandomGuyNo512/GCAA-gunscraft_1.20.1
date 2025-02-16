package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.attachments.Attachment;

@OnlyIn(Dist.CLIENT)
public class M4a1Model extends CommonRifleModel {
    public M4a1Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        bindSlotReplacement(GunRenderContext.SCOPE_ALL, "IS");
        bindSlotReplacement(Attachment.MUZZLE, "muzzle");
        bindSlotReplacement(Attachment.HANDGUARD, "handguard");
        bindSlotReplacement("gas_block", "front_IS");
        bindSlotReplacement(Attachment.STOCK, "stock");
    }
}
