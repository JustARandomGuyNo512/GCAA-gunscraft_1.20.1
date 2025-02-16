package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.gun.fireModes.Auto;

@OnlyIn(Dist.CLIENT)
public class AkmModel extends CommonRifleModel {
    private ModelPart safety;

    public AkmModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.png"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm_low.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm_low.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        safety = main.getChild("safety");
        bindSlotReplacement("dust_cover", "dust_cover");
        bindSlotReplacement(Attachment.MUZZLE, "muzzle");
        bindSlotReplacement(Attachment.HANDGUARD, "handguard");
        bindSlotReplacement(Attachment.STOCK, "stock");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        if (context.isFirstPerson) {
            safety.xRot = Clients.MAIN_HAND_STATUS.fireMode == Auto.AUTO ? 0.2181661564992911875f : 0.436332312998582375f;
        } else {
            safety.xRot = 0;
        }
        super.renderGunModel(context);
    }
}
