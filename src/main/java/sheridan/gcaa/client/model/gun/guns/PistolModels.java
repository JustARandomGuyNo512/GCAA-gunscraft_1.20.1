package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.GlockModel;
import sheridan.gcaa.client.model.gun.RevolverWithLoaderModel;

@OnlyIn(Dist.CLIENT)
public class PistolModels {

    public static final GlockModel G19_MODEL = new GlockModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.png"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19_low.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19_low.png"),
            5, 0.4f, 0.28f, 4.5f);

    public static final RevolverWithLoaderModel PYTHON_357_MODEL = new RevolverWithLoaderModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/python_357/python_357.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/python_357/python_357.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/python_357/python_357.png"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/python_357/python_357_low.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/python_357/python_357_low.png"),
            6, 45);

    public static final GlockModel FN57_MODEL = new GlockModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/fn57/fn57.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/fn57/fn57.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/fn57/fn57.png"),
            7, 0.5f, 0, 0f);

}
