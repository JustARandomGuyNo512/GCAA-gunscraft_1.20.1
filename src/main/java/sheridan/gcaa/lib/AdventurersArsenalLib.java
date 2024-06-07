package sheridan.gcaa.lib;


import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.client.model.assets.ModelLoader;
import sheridan.gcaa.client.model.modelPart.LayerDefinition;

public class AdventurersArsenalLib {

    /**
     * @see ModelLoader#loadModelAsset(ResourceLocation)
     * */
    public static LayerDefinition loadBedRockGunModel(ResourceLocation modelLocation) {
        return ModelLoader.loadModelAsset(modelLocation);
    }
}
