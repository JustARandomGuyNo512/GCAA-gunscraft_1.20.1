package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.PumpShotGunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.attachments.Attachment;

@OnlyIn(Dist.CLIENT)
public class M870Model extends PumpShotGunModel {
    public M870Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/m870/m870.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m870/m870.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m870/m870.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        bindSlotReplacement(Attachment.MAG, "mag");
    }
}
