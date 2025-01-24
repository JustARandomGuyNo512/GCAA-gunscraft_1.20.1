package sheridan.gcaa.items.attachments.stock;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

public class Stock extends Attachment {
    private final float recoilPitchControlInc;
    private final float recoilYawControlInc;
    private final float adsSpeedInc;
    private final float spreadRecoverInc;
    private final float sprintingSpreadFactorDec;
    private final float walkingSpreadFactorDec;
    private final float agilityInc;

    public Stock(float weight, float recoilPitchControlInc, float recoilYawControlInc, float adsSpeedInc, float spreadRecoverInc, float sprintingSpreadFactorDec, float walkingSpreadFactorDec, float agilityInc) {
        super(weight);
        this.recoilPitchControlInc = recoilPitchControlInc;
        this.recoilYawControlInc = recoilYawControlInc;
        this.adsSpeedInc = adsSpeedInc;
        this.spreadRecoverInc = spreadRecoverInc;
        this.sprintingSpreadFactorDec = sprintingSpreadFactorDec;
        this.walkingSpreadFactorDec = walkingSpreadFactorDec;
        this.agilityInc = agilityInc;
    }

    @Override
    public void onAttach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate + recoilPitchControlInc);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate + recoilYawControlInc);
        properties.setPropertyRateIfHas(GunProperties.ADS_SPEED, data, (prevRate) -> prevRate + adsSpeedInc);
        properties.setPropertyRateIfHas(GunProperties.SPREAD_RECOVER, data, (prevRate) -> prevRate + spreadRecoverInc);
        properties.setPropertyRateIfHas(GunProperties.SPRINTING_SPREAD_FACTOR, data, (prevRate) -> prevRate - sprintingSpreadFactorDec);
        properties.setPropertyRateIfHas(GunProperties.WALKING_SPREAD_FACTOR, data, (prevRate) -> prevRate - walkingSpreadFactorDec);
        properties.setPropertyRateIfHas(GunProperties.AGILITY, data, (prevRate) -> prevRate + agilityInc);
        super.onAttach(player, stack, gun, data);
    }

    @Override
    public void onDetach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate - recoilPitchControlInc);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate - recoilYawControlInc);
        properties.setPropertyRateIfHas(GunProperties.ADS_SPEED, data, (prevRate) -> prevRate - adsSpeedInc);
        properties.setPropertyRateIfHas(GunProperties.SPREAD_RECOVER, data, (prevRate) -> prevRate - spreadRecoverInc);
        properties.setPropertyRateIfHas(GunProperties.SPRINTING_SPREAD_FACTOR, data, (prevRate) -> prevRate + sprintingSpreadFactorDec);
        properties.setPropertyRateIfHas(GunProperties.WALKING_SPREAD_FACTOR, data, (prevRate) -> prevRate + walkingSpreadFactorDec);
        properties.setPropertyRateIfHas(GunProperties.AGILITY, data, (prevRate) -> prevRate - agilityInc);
        super.onDetach(player, stack, gun, data);
    }


}
