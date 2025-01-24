package sheridan.gcaa.client.model.attachments.stock;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class UBRStockModel implements IAttachmentModel {
    private final ModelPart root;
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/stocks/ubr_stock.png");

    public UBRStockModel() {
        this.root = ArsenalLib.loadBedRockGunModel(
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/stocks/ubr_stock.geo.json")).bakeRoot().getChild("root").meshing();
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        root.copyFrom(pose);
        context.render(root,  context.solid(TEXTURE));
        root.resetPose();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return root;
    }
}
