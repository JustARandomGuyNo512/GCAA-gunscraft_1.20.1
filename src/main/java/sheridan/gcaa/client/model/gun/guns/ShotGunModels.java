package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.AKModel;
import sheridan.gcaa.client.model.gun.AutoShotGunModel;
import sheridan.gcaa.client.model.gun.IGunModel;
import sheridan.gcaa.client.model.gun.PumpShotGunModel;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class ShotGunModels {
    public static final IGunModel M870_MODEL = new PumpShotGunModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m870/m870.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m870/m870.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m870/m870.png"));

    public static final IGunModel XM1014_MODEL = new AutoShotGunModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.png"));

    public static final IGunModel SAIGA_12K_MODEL = new Saiga12kModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/saiga_12k/saiga_12k.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/saiga_12k/saiga_12k.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/saiga_12k/saiga_12k.png")) {
    }.setOptions("renderType", "solidMipMap");
}
