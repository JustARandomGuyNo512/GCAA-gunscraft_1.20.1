package sheridan.gcaa.items.attachments;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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
import sheridan.gcaa.items.gun.IGun;

import java.util.List;
import java.util.Map;

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
        return prevSlot.isEmpty() && prevSlot.acceptsAttachment(AttachmentsRegister.getStrKey(this)) ? PASSED : REJECTED;
    }

    @Override
    public AttachResult canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        return PASSED;
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
}
