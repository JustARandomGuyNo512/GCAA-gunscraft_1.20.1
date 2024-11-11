package sheridan.gcaa.items.attachments;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.Clients;
import sheridan.gcaa.utils.FontUtils;

import java.util.List;

public abstract class Scope extends Sight {
    public final float maxMagnification;
    public final float minMagnification;
    public final float adsSpeedRate;

    public Scope(float maxMagnification, float minMagnification, float adsSpeedRate, float weight)    {
        super(weight);
        this.maxMagnification = maxMagnification;
        this.minMagnification = minMagnification;
        this.adsSpeedRate = adsSpeedRate;
    }

    private double tempMouseSensitivity = -1;

    public void handleMouseSensitivity() {
        float aimingProgress = Clients.MAIN_HAND_STATUS.getLerpAdsProgress(Minecraft.getInstance().getPartialTick());
        if (Clients.isInAds()) {
            if (tempMouseSensitivity == -1) {
                tempMouseSensitivity = Minecraft.getInstance().options.sensitivity().get();
            } else {
                float rate = Clients.MAIN_HAND_STATUS.getScopeMagnificationRate();
                float prevMagnification = Mth.lerp(rate, maxMagnification, minMagnification);
                double prevMouseSensitivity = Mth.lerp(aimingProgress, tempMouseSensitivity,
                        tempMouseSensitivity * (1 / (prevMagnification)));
                Minecraft.getInstance().options.sensitivity().set(prevMouseSensitivity);
            }
        } else {
            if (tempMouseSensitivity != -1) {
                Minecraft.getInstance().options.sensitivity().set(tempMouseSensitivity);
            }
            tempMouseSensitivity = -1;
        }
    }

    public void onLoseEffective() {
        if (tempMouseSensitivity != -1) {
            Minecraft.getInstance().options.sensitivity().set(tempMouseSensitivity);
        }
        tempMouseSensitivity = -1;
    }

    protected static float getNormalField() {
        GameRenderer renderer = Minecraft.getInstance().gameRenderer;
        return (float) (renderer.isPanoramicMode() ? Math.tan(Math.toRadians(45)) : Math.tan(Math.toRadians(35)));
    }

    public static float getFov(float rate) {
        return (float) (Math.atan(getNormalField() / rate) * 180 / Math.PI * 2);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(FontUtils.helperTip(Component.translatable("tooltip.gcaa.modify_magnification")));
    }
}
