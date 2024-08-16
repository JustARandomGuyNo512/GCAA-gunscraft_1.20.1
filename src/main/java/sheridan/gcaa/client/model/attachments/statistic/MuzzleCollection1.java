package sheridan.gcaa.client.model.attachments.statistic;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.lib.ArsenalLib;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class MuzzleCollection1 {
    public static ModelPart root;
    public static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/muzzles/muzzles1.png");

    private static void init() {
        root = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/attachments/muzzles/muzzle_collection1.json")).bakeRoot().getChild("root");
        for (Map.Entry<String, ModelPart> entry : root.getChildren().entrySet()) {
            entry.getValue().meshing();
        }
    }

    public static ModelPart get(String name) {
        if (root == null) {
            init();
        }
        return root == null ? ModelPart.EMPTY : root.getChild(name);
    }
}
