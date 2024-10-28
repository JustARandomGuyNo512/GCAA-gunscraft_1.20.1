package sheridan.gcaa.client.model.attachments.grip;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.IDirectionalModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.RenderTypes;

@OnlyIn(Dist.CLIENT)
public class RailPanelModel implements IAttachmentModel, IDirectionalModel {
    private final ModelPart panel;

    public RailPanelModel() {
        panel = StatisticModel.RAIL_PANELS.get("normal");
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        context.pushPose();
        initTranslation(attachmentRenderEntry, context, pose);
        context.render(panel, context.getBuffer(RenderTypes.getCutOutMipmap(StatisticModel.RAIL_PANELS.texture)));
        context.popPose();
    }

    @Override
    public ModelPart getRoot() {
        return panel;
    }

    @Override
    public byte getDirection() {
        return LOWER;
    }
}
