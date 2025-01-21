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
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class RailPanelShortModel implements IAttachmentModel, IDirectionalModel {
    private final ModelPart panel;

    public RailPanelShortModel() {
        panel = StatisticModel.RAIL_PANELS.get("short");
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        context.pushPose();
        initTranslation(attachmentRenderEntry, context, pose);
        context.render(panel, context.getBuffer(RenderTypes.getCutOutMipmap(StatisticModel.RAIL_PANELS.texture)));
        context.popPose();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return panel;
    }

    @Override
    public byte getDirection() {
        return LOWER;
    }
}
