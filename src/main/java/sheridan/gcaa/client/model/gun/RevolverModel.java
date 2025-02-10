package sheridan.gcaa.client.model.gun;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class RevolverModel extends NewGunModel{
    protected int magSize;
    private ModelPart reloading_arm;
    private ModelPart drum;
    private ModelPart[] unFiredBullets;
    private ModelPart[] firedBullets;

    public RevolverModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture,
                         @Nullable ResourceLocation lowQualityModelPath, @Nullable ResourceLocation lowQualityTexture, int magSize) {
        super(modelPath, animationPath, texture, lowQualityModelPath, lowQualityTexture);
        this.magSize = magSize;
    }

    public RevolverModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture, int magSize) {
        super(modelPath, animationPath, texture);
        this.magSize = magSize;
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {

    }

    @Override
    protected void animationGlobal(GunRenderContext context) {

    }

    @Override
    protected void renderGunModelLowQuality(GunRenderContext context) {

    }
}
