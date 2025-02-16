package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.MGModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.attachments.Attachment;

@OnlyIn(Dist.CLIENT)
public class M60E4Model extends MGModel {
    public M60E4Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/m60e4/m60e4.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m60e4/m60e4.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m60e4/m60e4.png"),
                9, 2400, false);
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        bindSlotReplacement(Attachment.MUZZLE, "muzzle");
        bindSlotReplacement(GunRenderContext.SCOPE_ALL, "IS");
    }
}
