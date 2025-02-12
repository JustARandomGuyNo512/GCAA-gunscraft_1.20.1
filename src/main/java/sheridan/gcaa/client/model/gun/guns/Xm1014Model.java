package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.AutoShotGunModel;

@OnlyIn(Dist.CLIENT)
public class Xm1014Model extends AutoShotGunModel {
    public Xm1014Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.png"));
    }
}
