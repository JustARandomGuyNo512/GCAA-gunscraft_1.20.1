package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.SniperModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.items.attachments.Attachment;

@OnlyIn(Dist.CLIENT)
public class AwpModel extends SniperModel {
    public AwpModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/awp/awp.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/awp/awp.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/awp/awp.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        bindSlotReplacement(Attachment.SCOPE, "IS", "front_IS");
        bindSlotReplacement(Attachment.MUZZLE, "muzzle");
    }
}
