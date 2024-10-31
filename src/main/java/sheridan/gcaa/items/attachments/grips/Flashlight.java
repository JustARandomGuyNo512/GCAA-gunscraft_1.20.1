package sheridan.gcaa.items.attachments.grips;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
    private final float luminance;

    public Flashlight(float weight, float luminance) {
        super(weight);
        this.luminance = luminance;
    }

    @Override
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {
        super.onAttach(stack, gun, data);
        CompoundTag nbt = gun.getGun().checkAndGet(stack);
        if (!nbt.contains("flashlight_on")) {
            nbt.putBoolean("flashlight_on", false);
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
        return tag.contains("flashlight_on") && tag.getBoolean("flashlight_on");
    }

    public float getLuminance() {
        return luminance;
    }

    public static void setFlashlightTurnOn(ItemStack stack, IGun gun, boolean on) {
        CompoundTag tag = gun.getGun().checkAndGet(stack);
        tag.putBoolean("flashlight_on", on);
    }

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {
        super.onDetach(stack, gun, data);
        if (data.contains("flashlights")) {
            int num = data.getInt("flashlights");
            if (num - 1 > 0) {
                data.putInt("flashlights", num - 1);
            } else {
                data.remove("flashlights");
            }
        }
        CompoundTag nbt = gun.getGun().checkAndGet(stack);
        if (nbt.contains("flashlight_on") && getFlashlightNum(stack, gun) == 1) {
            nbt.putBoolean("flashlight_on", false);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        String str = Component.translatable("tooltip.gcaa.turn_flashlight").getString().replace("$key", KeyBinds.TURN_FLASHLIGHT.getTranslatedKeyMessage().getString());
        pTooltipComponents.add(Component.literal(str));
    }
}
