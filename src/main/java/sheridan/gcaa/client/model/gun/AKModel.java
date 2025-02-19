package sheridan.gcaa.client.model.gun;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.fireModes.Auto;

public class AKModel extends CommonRifleModel{
    private ModelPart safety;
    public AKModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture, @Nullable ResourceLocation lowQualityModelPath, @Nullable ResourceLocation lowQualityTexture) {
        super(modelPath, animationPath, texture, lowQualityModelPath, lowQualityTexture);
    }

    public AKModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture) {
        super(modelPath, animationPath, texture);
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        safety = main.getChild("safety");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        if (context.isFirstPerson) {
            safety.xRot = Clients.MAIN_HAND_STATUS.fireMode == Auto.AUTO ? 0.2181661564992911875f : 0.436332312998582375f;
        } else {
            safety.xRot = 0;
        }
        super.renderGunModel(context);
    }
}
