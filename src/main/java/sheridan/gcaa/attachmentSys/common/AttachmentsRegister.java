package sheridan.gcaa.attachmentSys.common;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;

import java.util.Map;

public class AttachmentsRegister {
    private static final Map<String, IAttachment> ATTACHMENTS = new Object2ObjectArrayMap<>();
    private static final Map<IAttachment, ResourceLocation> REGISTRY_KEYS = new Object2ObjectArrayMap<>();
    private static final Map<IGun, AttachmentSlot> ATTACHMENT_SLOTS = new Object2ObjectArrayMap<>();
    private static final Map<IAttachment, IAttachmentModel> ATTACHMENT_MODELS = new Object2ObjectArrayMap<>();

    public static void register(Map.Entry<ResourceKey<Item>, Item> entry) {
        if (entry.getValue() instanceof IAttachment attachment) {
            ATTACHMENTS.put(entry.getKey().location().toString(), attachment);
            REGISTRY_KEYS.put(attachment, entry.getKey().location());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerModel(IAttachment attachment, IAttachmentModel model) {
        if (model != null) {
            ATTACHMENT_MODELS.put(attachment, model);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static IAttachmentModel getModel(IAttachment attachment) {
        return ATTACHMENT_MODELS.get(attachment);
    }

    public static IAttachment get(String s) {
        return ATTACHMENTS.get(s);
    }

    public static IAttachmentModel getModel(String s) {
        return getModel(get(s));
    }

    public static ResourceLocation getKey(IAttachment attachment) {
        return REGISTRY_KEYS.get(attachment);
    }

    public static String getStrKey(IAttachment attachment) {
        ResourceLocation resourceLocation = getKey(attachment);
        return resourceLocation == null ? null : resourceLocation.toString();
    }

    public static void registerAttachmentSlot(IGun gun, AttachmentSlot slot) {
        ATTACHMENT_SLOTS.put(gun, slot);
    }

    public static AttachmentSlot getAttachmentSlot(IGun gun) {
        return ATTACHMENT_SLOTS.getOrDefault(gun, AttachmentSlot.EMPTY);
    }
}
