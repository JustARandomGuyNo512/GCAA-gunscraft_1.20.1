package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector4i;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.AmmunitionMod;
import sheridan.gcaa.items.ammunition.IAmmunition;

import java.awt.*;

public class EfficientPropellant extends AmmunitionMod {
    public EfficientPropellant() {
        super(new ResourceLocation(GCAA.MODID, "efficient_propellant"), 3, ICONS_0, new Vector4i(80, 0, 128, 128)
                ,"gcaa.ammunition_mod.efficient_propellant", new Color(0x4a52c5).getRGB(), 100);
    }

    @Override
    public void onModifyAmmunition(IAmmunition ammunition, CompoundTag dataRateTag) {
        dataRateTag.putFloat(Ammunition.BASE_DAMAGE_RATE, dataRateTag.getFloat(Ammunition.BASE_DAMAGE_RATE) + 0.025f);
        dataRateTag.putFloat(Ammunition.MIN_DAMAGE_RATE, dataRateTag.getFloat(Ammunition.MIN_DAMAGE_RATE) + 0.025f);
        dataRateTag.putFloat(Ammunition.PENETRATION_RATE, dataRateTag.getFloat(Ammunition.PENETRATION_RATE) + 0.025f);
        dataRateTag.putFloat(Ammunition.EFFECTIVE_RANGE_RATE, dataRateTag.getFloat(Ammunition.EFFECTIVE_RANGE_RATE) + 0.025f);
        dataRateTag.putFloat(Ammunition.SPEED_RATE, dataRateTag.getFloat(Ammunition.SPEED_RATE) + 0.025f);
    }

    @Override
    public Component getSpecialDescription() {
        return Component.translatable("gcaa.ammunition_mod.efficient_propellant_special");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onEquippedClient() {
        Clients.MAIN_HAND_STATUS.shootCreateMuzzleSmoke = false;
    }
}
