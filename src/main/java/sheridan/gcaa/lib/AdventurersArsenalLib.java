package sheridan.gcaa.lib;


import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.assets.AnimationLoader;
import sheridan.gcaa.client.model.assets.ModelLoader;
import sheridan.gcaa.client.model.modelPart.LayerDefinition;

import java.util.Map;

public class AdventurersArsenalLib {

    /**
     * This method relies on Minecraft ResourceManager.
     * @see ModelLoader#loadModelAsset(ResourceLocation)
     * */
    public static LayerDefinition loadBedRockGunModel(ResourceLocation modelLocation) {
        return ModelLoader.loadModelAsset(modelLocation);
    }

    /**
     * This method relies on Minecraft ResourceManager.
     * @see AnimationLoader#loadAnimationCollection(ResourceLocation)
     * */
    public static Map<String, AnimationDefinition> loadBedRockAnimation(ResourceLocation modelLocation) {
        return AnimationLoader.loadAnimationCollection(modelLocation);
    }

}
