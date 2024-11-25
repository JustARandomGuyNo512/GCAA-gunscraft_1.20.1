package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.AmmunitionMod;
import sheridan.gcaa.items.ammunition.IAmmunition;

import java.awt.*;

public class HollowPoint extends AmmunitionMod {
    public HollowPoint() {
        super(new ResourceLocation(GCAA.MODID, "hollow_point"), 10, ICONS_0, new Vector4i(32, 0, 128, 128),
                "gcaa.ammunition_mod.hollow_point", new Color(0x53bd92).getRGB(), 40);
    }

    @Override
    public int getCostFor(IAmmunition ammunition) {
        return ammunition.getMaxModCapacity();
    }

    @Override
    public void onModifyAmmunition(IAmmunition ammunition, CompoundTag dataRateTag) {
        dataRateTag.putFloat(Ammunition.BASE_DAMAGE_RATE, dataRateTag.getFloat(Ammunition.BASE_DAMAGE_RATE) + 2.5f);
        dataRateTag.putFloat(Ammunition.MIN_DAMAGE_RATE, dataRateTag.getFloat(Ammunition.MIN_DAMAGE_RATE) - 0.5f);
        dataRateTag.putFloat(Ammunition.EFFECTIVE_RANGE_RATE, dataRateTag.getFloat(Ammunition.EFFECTIVE_RANGE_RATE) - 0.5f);
        dataRateTag.putFloat(Ammunition.PENETRATION_RATE, dataRateTag.getFloat(Ammunition.PENETRATION_RATE) - 0.8f);
    }
}
