package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.SniperModel;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class SniperModels {
    public static SniperModel AWP_MODEL = new SniperModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/awp/awp.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/awp/awp.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/awp/awp.png"));

    public static SniperModel FN_BALLISTA_MODEL = new SniperModel(
            new ResourceLocation(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.geo.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.animation.json"),
            new ResourceLocation(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.png")) {
        @Override
        protected VertexConsumer getDefaultVertex(GunRenderContext context) {
            return context.solidMipMap(texture);
        }
    };
}
