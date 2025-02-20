package sheridan.gcaa.client.model.gun.namingScript.scripts;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.gun.namingScript.IScript;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public record PartBoundVisible(ModelPart part,
                               ModelPart lowQualityPart) implements IScript<PartBoundVisible> {

    @Override
    public boolean value(GunRenderContext context) {
        return part.visible;
    }

    @Override
    public boolean valueLowQuality(GunRenderContext context) {
        return lowQualityPart != null && lowQualityPart.visible;
    }

    @Override
    public PartBoundVisible parse(String script, GunModel gunModel) {
        if (!script.matches("^[a-zA-Z0-9_-]+$") || script.matches("SCOPES")) {
            return null;
        }
        Map<String, ModelPart> flatDir = gunModel.getFlatDir();
        ModelPart modelPart = flatDir.get(script);
        if (modelPart != null) {
            Map<ModelPart, ModelPart> mainToLowMapping = gunModel.getMainToLowMapping();
            ModelPart lowQualityPart = mainToLowMapping.get(modelPart);
            return new PartBoundVisible(modelPart, lowQualityPart);
        }
        return null;
    }

}
