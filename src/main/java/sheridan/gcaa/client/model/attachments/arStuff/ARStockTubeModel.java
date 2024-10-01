package sheridan.gcaa.client.model.attachments.arStuff;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.ISlotProviderModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.statistic.ARStuff1;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class ARStockTubeModel implements IAttachmentModel, ISlotProviderModel {
    private final ModelPart stock_tube;
    private final ModelPart s_stock_tube;
    private final ResourceLocation texture = ARStuff1.TEXTURE;

    public ARStockTubeModel() {
        stock_tube = ARStuff1.get("stock_tube");
        s_stock_tube = stock_tube.getChild("s_stock_tube");
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName) {
        if (hasSlot(modelSlotName)) {
            stock_tube.translateAndRotate(poseStack);
            s_stock_tube.translateAndRotate(poseStack);
        }
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return modelSlotName.equals("s_stock_tube");
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        stock_tube.copyFrom(pose);
        context.render(stock_tube, context.getBuffer(RenderType.entityCutout(texture)));
        context.translateTo(s_stock_tube);
        context.renderEntry(attachmentRenderEntry.getChild("s_stock_tube"), s_stock_tube);
        stock_tube.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return stock_tube;
    }
}
