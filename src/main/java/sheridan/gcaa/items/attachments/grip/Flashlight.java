package sheridan.gcaa.items.attachments.grip;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.client.KeyBinds;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.gun.IGun;

import java.util.List;

public class Flashlight extends Attachment {
    public static final int OFF = 0;
    public static final int SPREAD = 1;
    public static final int SEARCHLIGHT = 2;
    private final float luminance;

    public Flashlight(float weight, float luminance) {
        super(weight);
        this.luminance = luminance;
    }

    @Override
    public void onAttach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        super.onAttach(player, stack, gun, data);
        CompoundTag nbt = gun.getGun().checkAndGet(stack);
        if (!nbt.contains("flashlight_mode")) {
            nbt.putInt("flashlight_mode", OFF);
        }
        if (!data.contains("flashlights")) {
            data.putInt("flashlights", 1);
        } else {
            data.putInt("flashlights", data.getInt("flashlights") + 1);
        }
    }

    public static int getFlashlightNum(ItemStack stack, IGun gun) {
        CompoundTag tag = gun.getPropertiesTag(stack);
        return tag.contains("flashlights") ? Math.max(0, tag.getInt("flashlights")) : 0;
    }

    public static boolean getFlashlightTurnOn(ItemStack stack, IGun gun) {
        if (getFlashlightNum(stack, gun) == 0) {
            return false;
        }
        CompoundTag tag = gun.getGun().checkAndGet(stack);
        return tag.contains("flashlight_mode") && tag.getInt("flashlight_mode") != OFF;
    }

    public float getLuminance() {
        return luminance;
    }

    public static void switchFlashlightMode(ItemStack stack, IGun gun) {
        if (getFlashlightNum(stack, gun) == 0) {
            return;
        }
        CompoundTag tag = gun.getGun().checkAndGet(stack);
        if (tag.contains("flashlight_mode")) {
            tag.putInt("flashlight_mode", (tag.getInt("flashlight_mode") + 1) % 3);
        }
    }

    public static int getFlashlightMode(ItemStack stack, IGun gun) {
        CompoundTag tag = gun.getGun().checkAndGet(stack);
        if (tag.contains("flashlight_mode")) {
            return tag.getInt("flashlight_mode");
        }
        return OFF;
    }

    @Override
    public void onDetach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        super.onDetach(player, stack, gun, data);
        CompoundTag nbt = gun.getGun().checkAndGet(stack);
        if (nbt.contains("flashlight_mode") && getFlashlightNum(stack, gun) <= 1) {
            nbt.putInt("flashlight_mode", OFF);
        }
        if (data.contains("flashlights")) {
            int num = data.getInt("flashlights");
            if (num - 1 > 0) {
                data.putInt("flashlights", num - 1);
            } else {
                data.remove("flashlights");
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        String str = Component.translatable("tooltip.gcaa.turn_flashlight").getString().replace("$key", KeyBinds.TURN_FLASHLIGHT.getTranslatedKeyMessage().getString());
        pTooltipComponents.add(Component.literal(str));
    }
}
