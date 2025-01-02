package sheridan.gcaa.client.model.attachments.grip;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class VerticalGripModel extends CommonGripModel {
    @Override
    protected void init() {
        root = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/vertical_grip.geo.json")).bakeRoot().getChild("root");
        body = root.getChild("body").meshing();
        left_arm = root.getChild("left_arm");
        left_arm_rifle = root.getChild("left_arm_new");
        low = StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("grip").meshing();
        texture = new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/vertical_grip.png");
        texture_low = StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture;
    }
}
