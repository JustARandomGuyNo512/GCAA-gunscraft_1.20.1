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
public class Ak12Model extends CommonRifleModel {
    public Ak12Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        bindSlotReplacement(GunRenderContext.SCOPE_ALL, "IS");
        bindSlotReplacement(Attachment.MUZZLE, "muzzle");
        bindSlotReplacement(Attachment.STOCK, "stock");
        bindSlotReplacement("handguard_left", "handguard/rail");
        bindSlotReplacement("handguard_right", "handguard/rail");
    }
}
