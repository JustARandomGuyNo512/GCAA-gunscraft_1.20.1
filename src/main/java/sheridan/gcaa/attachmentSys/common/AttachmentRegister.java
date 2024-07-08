package sheridan.gcaa.attachmentSys.common;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.attachmentSys.client.AttachmentSlot;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.guns.IGun;

import java.util.Map;

public class AttachmentRegister {
    private static final Map<String, IAttachment> attachments = new Object2ObjectArrayMap<>();
    private static final Map<IAttachment, ResourceLocation> registryKeys = new Object2ObjectArrayMap<>();
    @OnlyIn(Dist.CLIENT)
    private static final Map<IGun, AttachmentSlot> attachmentSlots = new Object2ObjectArrayMap<>();

    public static void register(Map.Entry<ResourceKey<Item>, Item> entry) {
        if (entry.getValue() instanceof IAttachment attachment) {
            attachments.put(entry.getKey().location().toString(), attachment);
            registryKeys.put(attachment, entry.getKey().location());
        }
    }

    public static IAttachment get(String s) {
        return attachments.get(s);
    }

    public static ResourceLocation getKey(IAttachment attachment) {
        return registryKeys.get(attachment);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerAttachmentSlot(IGun gun, AttachmentSlot slot) {
        attachmentSlots.put(gun, slot);
    }

    @OnlyIn(Dist.CLIENT)
    public static AttachmentSlot getAttachmentSlot(IGun gun) {
        return attachmentSlots.getOrDefault(gun, AttachmentSlot.EMPTY);
    }
}