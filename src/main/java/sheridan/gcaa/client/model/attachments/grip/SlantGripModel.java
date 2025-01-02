package sheridan.gcaa.client.model.attachments.grip;

import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.lib.ArsenalLib;

public class SlantGripModel extends CommonGripModel{
    @Override
    protected void init() {
        root = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/slant_grip.geo.json")).bakeRoot().getChild("root");
        body = root.getChild("body").meshing();
        left_arm = root.getChild("left_arm");
        left_arm_rifle = root.getChild("left_arm_new");
        low = root.getChild("low").meshing();
        texture = new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/slant_grip.png");
        texture_low = texture;
    }
}
