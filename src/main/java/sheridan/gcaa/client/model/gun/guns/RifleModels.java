package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.AKModel;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.gun.IGunModel;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RifleModels {
    public static IGunModel AK12_MODEL = new AKModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.png"));

    public static IGunModel AKM_MODEL = new AKModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.png"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm_low.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm_low.png"));

    public static IGunModel M4A1_MODEL = new CommonRifleModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.png"));

    public static IGunModel AUG_A3_MODEL = new CommonRifleModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/aug_a3/aug_a3.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/aug_a3/aug_a3.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/aug_a3/aug_a3.png"));

    public static IGunModel HK_G28_MODEL = new CommonRifleModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.png"))
            .setOptions("renderType", "solidMipMap");

    public static IGunModel MCX_SPEAR_MODEL = new CommonRifleModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/mcx_spear/mcx_spear.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/mcx_spear/mcx_spear.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/mcx_spear/mcx_spear.png"))
            .setOptions("renderType", "solidMipMap");
}
