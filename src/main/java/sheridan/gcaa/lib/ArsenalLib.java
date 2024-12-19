package sheridan.gcaa.lib;


import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.gun.IGunModel;
import sheridan.gcaa.client.animation.io.AnimationLoader;
import sheridan.gcaa.client.model.io.ModelLoader;
import sheridan.gcaa.client.model.modelPart.LayerDefinition;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;

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
     * This method relies on Minecraft ResourceManager.
     * @see AnimationLoader#loadAnimationCollection(ResourceLocation)
     * */
    public static Map<String, AnimationDefinition> loadBedRockAnimationWithSound(ResourceLocation modelLocation) {
        return AnimationLoader.loadAnimationCollection(modelLocation, true);
    }

    /**
     * Gets the client weapon status in main hand.
     * */
    public static ClientWeaponStatus getClientWeaponStatus() {
        return Clients.MAIN_HAND_STATUS;
    }

    /**
     * Register a gun model and display data, only in client side, you should better call this method in FMLClientSetupEvent stage
     * */
    @OnlyIn(Dist.CLIENT)
    public static void registerGunModel(IGun gun, IGunModel model, DisplayData displayData) {
        GunModelRegister.registerModel(gun, model);
        GunModelRegister.registerTransform(gun, displayData);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerAttachmentModel(@NotNull IAttachment attachment, @NotNull IAttachmentModel model) {
        AttachmentsRegister.registerModel(attachment, model);
    }

    /**
     * Register a gun attachment slot, only in client side, you should better call this method in FMLClientSetupEvent stage
     * */
    @OnlyIn(Dist.CLIENT)
    public static void registerGunAttachments(@NotNull IGun gun,@NotNull AttachmentSlot slot) {
        AttachmentsRegister.registerAttachmentSlot(gun, slot);
    }

    /**
     * Get the attachments' handler.
     * @see AttachmentsHandler
     * */
    public static AttachmentsHandler attachmentsHandler() {
        return AttachmentsHandler.INSTANCE;
    }
}
