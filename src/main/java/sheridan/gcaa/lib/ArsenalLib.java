package sheridan.gcaa.lib;


import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.Clients;
import sheridan.gcaa.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.model.assets.AnimationLoader;
import sheridan.gcaa.client.model.assets.ModelLoader;
import sheridan.gcaa.client.model.modelPart.LayerDefinition;

import java.util.Map;

public class ArsenalLib {

    /**
     * This method relies on Minecraft ResourceManager.
     * @see ModelLoader#loadModelLayer(ResourceLocation)
     * */
    public static LayerDefinition loadBedRockGunModel(ResourceLocation modelLocation) {
        return ModelLoader.loadModelLayer(modelLocation);
    }

    /**
     * This method relies on Minecraft ResourceManager.
     * @see AnimationLoader#loadAnimationCollection(ResourceLocation)
     * */
    public static Map<String, AnimationDefinition> loadBedRockAnimation(ResourceLocation modelLocation) {
        return AnimationLoader.loadAnimationCollection(modelLocation);
    }

    /**
     * Gets the client weapon status in main hand.
     * */
    public ClientWeaponStatus getClientWeaponStatus() {
        return Clients.mainHandStatus;
    }

}
