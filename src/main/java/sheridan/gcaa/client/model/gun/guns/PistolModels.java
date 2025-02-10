package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.GlockModel;

@OnlyIn(Dist.CLIENT)
public class PistolModels {

    public static final GlockModel G19_MODEL = new GlockModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.png"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19_low.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19_low.png"),
            5, 0.4f, 0.28f, 4.5f);

}
