package sheridan.gcaa.items.attachments.grip;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.FontUtils;

import java.util.List;

public class LaserSight extends Attachment {
    private final float walkingSpreadFactorLowerRate;
    private final float sprintingSpreadFactorLowerRate;
    public LaserSight(float weight, float walkingSpreadFactorLowerRate, float sprintingSpreadFactorLowerRate)  {
        super(weight);
        this.walkingSpreadFactorLowerRate = walkingSpreadFactorLowerRate;
        this.sprintingSpreadFactorLowerRate = sprintingSpreadFactorLowerRate;
    }

    @Override
    public void onAttach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.WALKING_SPREAD_FACTOR, data, (prevRate) -> prevRate - walkingSpreadFactorLowerRate);
        properties.setPropertyRateIfHas(GunProperties.WALKING_SPREAD_FACTOR, data, (prevRate) -> prevRate - sprintingSpreadFactorLowerRate);
        super.onAttach(player, stack, gun, data);
    }

    @Override
    public void onDetach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.WALKING_SPREAD_FACTOR, data, (prevRate) -> prevRate + walkingSpreadFactorLowerRate);
        properties.setPropertyRateIfHas(GunProperties.WALKING_SPREAD_FACTOR, data, (prevRate) -> prevRate - sprintingSpreadFactorLowerRate);
        super.onDetach(player, stack, gun, data);
    }

    @Override
    public List<Component> getEffectsInGunModifyScreen() {
        List<Component> effectsInGunModifyScreen = super.getEffectsInGunModifyScreen();
        if (sprintingSpreadFactorLowerRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("sprinting_spread_factor", - sprintingSpreadFactorLowerRate, sprintingSpreadFactorLowerRate > 0));
        if (walkingSpreadFactorLowerRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("walking_spread_factor", - walkingSpreadFactorLowerRate, walkingSpreadFactorLowerRate > 0));
        return effectsInGunModifyScreen;
    }
}
