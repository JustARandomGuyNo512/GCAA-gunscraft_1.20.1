package sheridan.gcaa.client.model.attachments.stocks;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class CTRStockModel implements IAttachmentModel {
    private final ModelPart root;
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/stocks/ctr_stock.png");

    public CTRStockModel() {
        this.root = ArsenalLib.loadBedRockGunModel(
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/stocks/ctr_stock.geo.json")).bakeRoot().getChild("root").meshing();
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        root.copyFrom(pose);
        context.render(root, vertexConsumer);
        root.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return root;
    }
}
