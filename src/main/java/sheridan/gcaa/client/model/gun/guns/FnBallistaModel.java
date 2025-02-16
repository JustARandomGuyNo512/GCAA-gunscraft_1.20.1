package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.SniperModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.attachments.Attachment;

@OnlyIn(Dist.CLIENT)
public class FnBallistaModel extends SniperModel {
    public FnBallistaModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        bindSlotReplacement(GunRenderContext.SCOPE_ALL, "IS");
        bindSlotReplacement(Attachment.MUZZLE, "muzzle");
    }
}
