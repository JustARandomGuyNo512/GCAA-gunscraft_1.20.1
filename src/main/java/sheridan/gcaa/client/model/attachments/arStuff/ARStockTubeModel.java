package sheridan.gcaa.client.model.attachments.arStuff;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.ISlotProviderModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class ARStockTubeModel implements IAttachmentModel, ISlotProviderModel {
    private final ModelPart stock_tube;
    private final ModelPart s_stock_tube;
    private final ModelPart low;
    private final ResourceLocation texture = StatisticModel.AR_STUFF1.texture;
    private final ResourceLocation texture_low = new ResourceLocation(GCAA.MODID, "model_assets/attachments/ar_stuff/gas_block_stock_tube_low.png");


    public ARStockTubeModel() {
        stock_tube = StatisticModel.AR_STUFF1.get("stock_tube");
        s_stock_tube = stock_tube.getChild("s_stock_tube");
        low = StatisticModel.AR_STUFF1_LOW.get("stock_tube_low");
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName, IGun gun) {
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
        if (context.useLowQuality()) {
            low.copyFrom(stock_tube);
            context.render(low, context.getBuffer(RenderType.entityCutout(StatisticModel.AR_STUFF1_LOW.texture)));
        } else {
            context.render(stock_tube, context.getBuffer(RenderType.entityCutout(texture)));
        }
        context.translateTo(stock_tube);
        context.renderEntry(attachmentRenderEntry.getChild("s_stock_tube"), s_stock_tube);
        stock_tube.resetPose();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return stock_tube;
    }
}
