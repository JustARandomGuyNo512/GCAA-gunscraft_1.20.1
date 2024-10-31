package sheridan.gcaa.client.model.attachments.grip;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.IDirectionalModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;

@OnlyIn(Dist.CLIENT)
public class MicroFlashlightModel extends FlashlightModel implements IAttachmentModel, IDirectionalModel {
    @Override
    protected void init() {
        this.body = StatisticModel.FLASHLIGHTS.get("micro");
        this.light_rear = body.getChild("light_rear");
        this.light_far = body.getChild("light_far");
    }
}
