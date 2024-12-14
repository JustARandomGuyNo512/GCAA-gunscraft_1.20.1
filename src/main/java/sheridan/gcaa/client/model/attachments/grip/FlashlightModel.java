package sheridan.gcaa.client.model.attachments.grip;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.events.RenderEvents;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.IDirectionalModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.attachments.grip.Flashlight;

@OnlyIn(Dist.CLIENT)
public class FlashlightModel implements IAttachmentModel, IDirectionalModel {
    protected ModelPart body;
    protected ModelPart light_rear;
    protected ModelPart light_far;
    protected ModelPart low;

    public FlashlightModel() {
        init();
    }

    protected void init() {
        this.body = StatisticModel.FLASHLIGHTS.get("normal");
        this.light_rear = body.getChild("light_rear_normal");
        this.light_far = body.getChild("light_far_normal");
        this.low = StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("flashlights").getChild("normal").meshing();
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        context.pushPose();
        initTranslation(attachmentRenderEntry, context, pose);
        boolean useLow = context.useLowQuality() && low != null;
        ModelPart bodyModel = useLow ? low : body;
        if (useLow) {
            low.copyFrom(body);
        }
        context.render(bodyModel, context.getBuffer(RenderType.entityCutout(useLow ?
                StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture :
                StatisticModel.FLASHLIGHTS.texture)));
        if (Flashlight.getFlashlightTurnOn(context.itemStack, context.gun) && context.isFirstPerson) {
            RenderEvents.callFlashlightEffect(context, light_rear, light_far, ((Flashlight) attachmentRenderEntry.attachment).getLuminance());
        }
        context.popPose();
    }

    @Override
    public ModelPart getRoot() {
        return body;
    }

    @Override
    public byte getDirection() {
        return LOWER;
    }
}
