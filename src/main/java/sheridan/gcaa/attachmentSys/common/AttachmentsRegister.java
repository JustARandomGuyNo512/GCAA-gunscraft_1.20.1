package sheridan.gcaa.attachmentSys.common;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.AttachmentSlotProxy;
import sheridan.gcaa.attachmentSys.IAttachmentSlotProxyCreator;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;

import java.util.Map;

/**
 * @description 配件注册
 */
public class AttachmentsRegister {
    /**
     * 配件对象Map
     */
    private static final Map<String, IAttachment> ATTACHMENTS = new Object2ObjectArrayMap<>();
    private static final Map<IAttachment, ResourceLocation> REGISTRY_KEYS = new Object2ObjectArrayMap<>();
    private static final Map<IAttachment, IAttachmentModel> ATTACHMENT_MODELS = new Object2ObjectArrayMap<>();
    private static final Map<IGun, IAttachmentSlotProxyCreator> ATTACHMENT_SLOT_PROXIES = new Object2ObjectArrayMap<>();
    private static final Map<IGun, AttachmentSlot> ATTACHMENT_SLOTS = new Object2ObjectArrayMap<>();

    public static void register(Map.Entry<ResourceKey<Item>, Item> entry) {
        if (entry.getValue() instanceof IAttachment attachment) {
            ATTACHMENTS.put(entry.getKey().location().toString(), attachment);
            REGISTRY_KEYS.put(attachment, entry.getKey().location());
        }
    }
    /**
     * 注册模型到配件对象中
     * @param attachment 配件对象
     * @param model 要注册的模型
     */
    @OnlyIn(Dist.CLIENT)
    public static void registerModel(IAttachment attachment, IAttachmentModel model) {
        if (model != null) {
            ATTACHMENT_MODELS.put(attachment, model);
        }
    }

    /**
     * 通过配件对象获取模型
     * @param attachment 配件对象
     * @return 返回获取到的模型
     */
    @OnlyIn(Dist.CLIENT)
    public static IAttachmentModel getModel(IAttachment attachment) {
        return ATTACHMENT_MODELS.get(attachment);
    }

    /**
     * 获取配件对象
     * @param s 配件id
     * @return 配件对象
     */
    public static IAttachment get(String s) {
        return ATTACHMENTS.get(s);
    }

    /**
     * 通过id获取配件模型
     * @param s 配件id
     * @return 返回获取的配件模型
     */
    public static IAttachmentModel getModel(String s) {
        return getModel(get(s));
    }

    public static ResourceLocation getKey(IAttachment attachment) {
        return REGISTRY_KEYS.get(attachment);
    }
    /**
     * 获取配件对象的id
     * @param attachment 传入需要获取的配件
     * @return 返回对应id
     */
    public static String getStrKey(IAttachment attachment) {
        ResourceLocation resourceLocation = getKey(attachment);
        return resourceLocation == null ? null : resourceLocation.toString();
    }

    /**
     * 注册配件到枪械的配件节点上
     * @param gun 枪械
     * @param slot 节点
     */
    public static void registerAttachmentSlot(IGun gun, AttachmentSlot slot) {
        ATTACHMENT_SLOTS.put(gun, slot);
    }
    /**
     * 注册配件到枪械的配件节点上
     * @param gun 枪械
     * @param slot 节点
     * @param creator 配件树代理对象生成器
     */
    public static void registerAttachmentSlot(IGun gun, AttachmentSlot slot, IAttachmentSlotProxyCreator creator) {
        ATTACHMENT_SLOTS.put(gun, slot);
        ATTACHMENT_SLOT_PROXIES.put(gun, creator);
    }

    /**
     * 获取配件代理对象
     * @param gun 枪械
     * @param root 根节点
     * @return 返回代理对象-里有配件槽
     */
    public static AttachmentSlotProxy getProxiedAttachmentSlot(IGun gun, AttachmentSlot root) {
        IAttachmentSlotProxyCreator creator = ATTACHMENT_SLOT_PROXIES.get(gun);
        if (creator != null) {
            return creator.create(root);
        }
        return AttachmentSlotProxy.getEmptyProxy(root);
    }

    /**
     * 获取枪械的配件树
     * @param gun 枪械
     * @return 配件树
     */
    public static AttachmentSlot getAttachmentSlot(IGun gun) {
        return ATTACHMENT_SLOTS.getOrDefault(gun, AttachmentSlot.EMPTY);
    }
}
