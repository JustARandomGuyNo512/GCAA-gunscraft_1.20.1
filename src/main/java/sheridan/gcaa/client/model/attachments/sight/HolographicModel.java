package sheridan.gcaa.client.model.attachments.sight;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.CommonSightModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;

import static sheridan.gcaa.client.model.attachments.SightViewRenderer.DEFAULT_RED_DOT_CROSSHAIR_SCALE;

@OnlyIn(Dist.CLIENT)
public class HolographicModel extends CommonSightModel {
    public HolographicModel() {
        super(StatisticModel.SIGHTS1.get("holographic"),
                StatisticModel.SIGHTS1.texture,
                StatisticModel.HOLOGRAPHIC_CROSSHAIR,
                "min_z_dis_holo", "holographic_body", "crosshair_holo",
                DEFAULT_RED_DOT_CROSSHAIR_SCALE);
        this.setLow(StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("sights1").getChild("holographic"),
                StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture);
    }
}
