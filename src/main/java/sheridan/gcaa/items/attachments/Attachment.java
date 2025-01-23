package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.items.AutoRegister;
import sheridan.gcaa.items.NoRepairNoEnchantmentItem;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public abstract class Attachment extends NoRepairNoEnchantmentItem implements IAttachment, AutoRegister {
    public static final String MUZZLE = "muzzle";
    public static final String MAG = "mag";
    public static final String HANDGUARD = "handguard";
    public static final String STOCK = "stock";
    public static final String GRIP = "grip";
    public static final String SCOPE = "scope";
    public float weight;
    public Attachment(float weight) {
        super(new Properties().stacksTo(1));
        this.weight = weight;
    }

    @Override
    public AttachResult canAttach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        ReplaceableGunPart replaceableGunPart = prevSlot.getReplaceableGunPart();
        if (replaceableGunPart != null) {
            AttachResult result = replaceableGunPart.canReplace(stack, gun, root, prevSlot);
            if (!result.isPassed()) {
                return result;
            }
        }
        AttachResult result = prevSlot.isEmpty() && prevSlot.acceptsAttachment(AttachmentsRegister.getStrKey(this)) ?
                IAttachment.passed() :
                IAttachment.rejected();
        Stack<AttachmentSlot> ancestors = prevSlot.getAncestors();
        for (AttachmentSlot ancestor : ancestors) {
            String attachmentId = ancestor.getAttachmentId();
            IAttachment attachment = AttachmentsRegister.get(attachmentId);
            if (attachment != null) {
                attachment.childTryAttach(stack, gun, this, prevSlot, ancestors, result);
            }
        }
        return result;
    }

    @Override
    public AttachResult canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        AttachResult result = IAttachment.passed();
        Stack<AttachmentSlot> ancestors = prevSlot.getAncestors();
        for (AttachmentSlot ancestor : ancestors) {
            String attachmentId = ancestor.getAttachmentId();
            IAttachment attachment = AttachmentsRegister.get(attachmentId);
            if (attachment != null) {
                attachment.childTryDetach(stack, gun, this, prevSlot, ancestors, result);
            }
        }
        return result;
    }

    @Override
    public void onAttach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.addWeight(data, weight);
    }

    @Override
    public void onDetach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.addWeight(data, -weight);
    }

    @Override
    public void clientRegister(Map.Entry<ResourceKey<Item>, Item> entry) {
        AttachmentsRegister.register(entry);
    }

    @Override
    public Attachment get() {
        return this;
    }

    @Override
    public void serverRegister(Map.Entry<ResourceKey<Item>, Item> entry) {
        AttachmentsRegister.register(entry);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("tooltip.gun_info.weight").append("" + weight));
    }

    @Override
    public List<Component> getEffectsInGunModifyScreen() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable(getDescriptionId()));
        if (this.weight > 0) {
            list.add(Component.translatable("tooltip.gun_info.weight").append(Component.literal("+ " + weight).withStyle(Style.EMPTY.withColor(Color.RED.getRGB()))));
        }
        return list;
    }
}
