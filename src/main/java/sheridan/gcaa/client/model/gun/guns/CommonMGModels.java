package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.IGunModel;
import sheridan.gcaa.client.model.gun.MGModel;

@OnlyIn(Dist.CLIENT)
public class CommonMGModels {
    public static final IGunModel M60E4 = new MGModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m60e4/m60e4.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m60e4/m60e4.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m60e4/m60e4.png"),
            9, 2400, false);

    public static final IGunModel M249_Model = new MGModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m249/m249.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m249/m249.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m249/m249.png"),
            11, 2400, true);
}
