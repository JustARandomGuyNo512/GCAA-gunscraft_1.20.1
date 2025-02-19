package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.AutoShotGunModel;
import sheridan.gcaa.client.model.gun.PumpShotGunModel;

@OnlyIn(Dist.CLIENT)
public class ShotGunModels {
    public static final PumpShotGunModel M870_MODEL = new PumpShotGunModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m870/m870.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m870/m870.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m870/m870.png"));

    public static final AutoShotGunModel XM1014_MODEL = new AutoShotGunModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.png"));
}
