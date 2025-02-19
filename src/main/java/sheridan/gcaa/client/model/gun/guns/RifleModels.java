package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.AKModel;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.gun.DMRModel;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class RifleModels {
    public static AKModel AK12_MODEL = new AKModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.png"));

    public static AKModel AKM_MODEL = new AKModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm.png"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm_low.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/akm/akm_low.png"));

    public static CommonRifleModel M4A1_MODEL = new CommonRifleModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.png"));

    public static DMRModel HK_G28_MODEL = new DMRModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.png")) {
        @Override
        protected VertexConsumer getDefaultVertex(GunRenderContext context) {
            return context.solidMipMap(texture);
        }
    };
}
