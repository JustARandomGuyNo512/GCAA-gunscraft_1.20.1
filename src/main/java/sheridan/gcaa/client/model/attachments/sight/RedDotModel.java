package sheridan.gcaa.client.model.attachments.sight;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.CommonSightModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;

@OnlyIn(Dist.CLIENT)
public class RedDotModel extends CommonSightModel {
    public RedDotModel() {
        super(StatisticModel.SIGHTS1.get("red_dot_rifle"), StatisticModel.SIGHTS1.texture, StatisticModel.RED_DOT_CROSSHAIR,
                "min_z_dis_rifle", "red_dot_rifle_body", "crosshair_rifle", 0.031f);
        this.setLow(StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("sights1").getChild("red_dot_rifle"),
                StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture);
    }
}
