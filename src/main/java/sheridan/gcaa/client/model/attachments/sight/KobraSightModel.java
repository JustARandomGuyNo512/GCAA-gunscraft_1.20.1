package sheridan.gcaa.client.model.attachments.sight;

import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.CommonSightModel;

public class KobraSightModel extends CommonSightModel {
    public KobraSightModel() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/kobra_sight.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/kobra_sight.png"),
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/kobra_sight.png"),
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/kobra.png"),
                "low", "min_z_dis", "body", "crosshair", 0.12f);
    }
}
