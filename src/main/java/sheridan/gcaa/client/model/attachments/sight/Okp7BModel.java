package sheridan.gcaa.client.model.attachments.sight;

import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.CommonSightModel;

public class Okp7BModel extends CommonSightModel {
    public Okp7BModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/okp7_b.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/okp7_b.png"),
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/okp7_b.png"),
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/okp7.png"),
                "low", "min_z_dis", "body", "crosshair_rifle", 0.2f);
    }
}
