package sheridan.gcaa.client.model.attachments;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.lib.ArsenalLib;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class StatisticModel {
    public static StatisticModel LASER_SIGHTS = new StatisticModel(
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/laser_sights.png"),
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/laser_sights.geo.json"));

    public static StatisticModel AK_STUFF1 = new StatisticModel(
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/ak_stuff/handguard1_rail_set1.png"),
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/ak_stuff/handguard1_rail_set1.geo.json"));

    public static StatisticModel AR_STUFF1 = new StatisticModel(
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/ar_stuff/gas_block_stock_tube.png"),
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/ar_stuff/gas_block_stock_tube.geo.json"));

    public static StatisticModel MAG_COLLECTION1 = new StatisticModel(
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/mags/mags1.png"),
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/mags/mag_collection1.geo.json"));

    public static StatisticModel MUZZLE_COLLECTION1 = new StatisticModel(
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/muzzles/muzzles1.png"),
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/muzzles/muzzle_collection1.geo.json"));

    public static StatisticModel MUZZLE_COLLECTION2 = new StatisticModel(
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/muzzles/muzzles2.png"),
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/muzzles/muzzle_collection2.geo.json"));

    public static StatisticModel SIGHTS1 = new StatisticModel(
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/sights1.png"),
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/sights1.geo.json"));

    public static StatisticModel RAIL_PANELS = new StatisticModel(
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/rail_panels.png"),
            new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/rail_panels.geo.json"));

    public static final ResourceLocation HOLOGRAPHIC_CROSSHAIR = new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/holographic.png");
    public static final ResourceLocation RED_DOT_CROSSHAIR = new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/red_dot.png");


    private ModelPart root;
    public final ResourceLocation texture;
    public final ResourceLocation modelPath;

    public StatisticModel(ResourceLocation texture, ResourceLocation modelPath) {
        this.texture = texture;
        this.modelPath = modelPath;
    }

    protected void init() {
        root = ArsenalLib.loadBedRockGunModel(modelPath).bakeRoot().getChild("root");
        for (Map.Entry<String, ModelPart> entry : root.getChildren().entrySet()) {
            entry.getValue().meshing();
        }
    }

    public ModelPart get(String name) {
        if (root == null) {
            init();
        }
        return root == null ? ModelPart.EMPTY : root.getChild(name);
    }
}
