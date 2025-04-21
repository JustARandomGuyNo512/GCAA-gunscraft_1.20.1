package sheridan.gcaa.client.model.attachments.sight;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.CommonSightModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;

@OnlyIn(Dist.CLIENT)
public class MicroRedDotModel extends CommonSightModel {
    public MicroRedDotModel() {
        super(StatisticModel.SIGHTS1.get("red_dot_pistol"), StatisticModel.SIGHTS1.texture,
                StatisticModel.RED_DOT_CROSSHAIR,
                null, "red_dot_pistol_body", "crosshair_pistol", 0.015f);
        this.setLow(StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("sights1").getChild("red_dot_pistol"),
                StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture);
    }
}
