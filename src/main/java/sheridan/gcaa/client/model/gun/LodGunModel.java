package sheridan.gcaa.client.model.gun;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.client.config.ClientConfig;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;

import java.util.HashMap;
import java.util.Map;


@OnlyIn(Dist.CLIENT)
public abstract class LodGunModel extends GunModel{
    public static final String LOW_QUALITY_KEY = "lowQuality";
    private boolean lowQualityLoaded = false;
    protected ModelPart lowQualityRoot;
    protected ModelPart lowQualityGun;
    protected Map<ModelPart, ModelPart> gunLayerMapping;

    public LodGunModel(ResourceLocation modelPath, @Nullable ResourceLocation lowQualityModelPath, ResourceLocation animationPath) {
        super(modelPath, animationPath);
        if (lowQualityModelPath != null) {
            try {
                lowQualityRoot = ArsenalLib.loadBedRockGunModel(lowQualityModelPath).bakeRoot().getChild("root");
                lowQualityGun = lowQualityRoot.getChild("gun");
                gunLayerMapping = new HashMap<>();
                lowQualityLoaded = true;
                postInitLowQuality(lowQualityGun, lowQualityRoot);
            } catch (Exception e) {
                System.out.println("exception when loading low model:");
                e.printStackTrace();
            }
        }
    }

    protected void resolveLowQualityMapping(ModelPart originLayer, ModelPart lowQualityLayer) {
        Map<String, ModelPart> children = originLayer.getChildren();
        if (children != null && !children.isEmpty()) {
            for (Map.Entry<String, ModelPart> entry : children.entrySet()) {
                if (entry.getKey().startsWith("_SUB_R_")) {
                    continue;
                }
                ModelPart key = entry.getValue();
                String childName = entry.getKey();
                if (lowQualityLayer.hasChild(childName)) {
                    ModelPart value = lowQualityLayer.getChild(childName);
                    gunLayerMapping.put(key, value);
                    resolveLowQualityMapping(key, value);
                } else {
                    throw new RuntimeException("Unable to mapping low quality model part " + childName);
                }
            }
        }
    }

    protected abstract void postInitLowQuality(ModelPart lowQualityGun, ModelPart lowQualityRoot);

    protected boolean handleShouldRenderLowQuality(GunRenderContext context) {
        if (context.isFirstPerson || !lowQualityLoaded || context.inAttachmentScreen) {
            return false;
        }
        ItemDisplayContext transformType = context.transformType;
        switch (transformType) {
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                return ClientConfig.renderLowQualityModelInTPView.get();
            }
            case GROUND -> {return ClientConfig.renderLowQualityModelInGroundView.get();}
            case GUI -> {return ClientConfig.renderLowQualityModelInGuiView.get();}
            default -> {return ClientConfig.renderLowQualityModelInOtherView.get();}
        }
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        if (handleShouldRenderLowQuality(context)) {
            context.saveInLocal(LOW_QUALITY_KEY, null);
            renderGunLow(context);
        } else {
            renderGunNormal(context);
        }
    }

    protected abstract void renderGunNormal(GunRenderContext context);
    protected abstract void renderGunLow(GunRenderContext context);

    public ModelPart getFromMapping(GunRenderContext context, ModelPart modelPart) {
        return getShouldRenderLowQuality(context) ? gunLayerMapping.get(modelPart) : modelPart;
    }

    public ModelPart[] getFromMapping(GunRenderContext context, ModelPart... modelParts) {
        if (getShouldRenderLowQuality(context)) {
            for (int i = 0; i < modelParts.length; i++) {
                modelParts[i] = gunLayerMapping.get(modelParts[i]);
            }
        }
        return modelParts;
    }

    public ModelPart getFromMapping(boolean useLowQuality, ModelPart modelPart) {
        return useLowQuality ? gunLayerMapping.get(modelPart) : modelPart;
    }

    protected boolean getShouldRenderLowQuality(GunRenderContext context) {
        return context.localRenderStorage != null && context.localRenderStorage.containsKey(LOW_QUALITY_KEY);
    }


    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {

    }

    @Override
    protected void renderPostEffect(GunRenderContext context) {

    }

    @Override
    protected void animationGlobal(GunRenderContext context) {

    }

    @Override
    protected void afterRender(GunRenderContext context) {

    }

}
