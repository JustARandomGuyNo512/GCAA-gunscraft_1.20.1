package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.gun.IGunModel;

@OnlyIn(Dist.CLIENT)
public class CommonSMGModels {
    public static final IGunModel MP5_MODEL = new CommonRifleModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/mp5/mp5.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/mp5/mp5.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/mp5/mp5.png"));

    public static final IGunModel ANNIHILATOR_MODEL = new CommonRifleModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/annihilator/annihilator.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/annihilator/annihilator.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/annihilator/annihilator.png"));
}
