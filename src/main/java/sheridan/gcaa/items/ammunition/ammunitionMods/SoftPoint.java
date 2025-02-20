package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.AmmunitionMod;
import sheridan.gcaa.items.ammunition.IAmmunition;

import java.awt.*;

public class SoftPoint extends AmmunitionMod {
    public SoftPoint() {
        super(new ResourceLocation(GCAA.MODID, "soft_point"), 8, ICONS_0, new Vector4i(96, 0, 128, 128),
                "gcaa.ammunition_mod.soft_point", new Color(0xE83EDA).getRGB(), 200);
    }

    @Override
    public int getCostFor(IAmmunition ammunition) {
        return ammunition.getMaxModCapacity() - 2;
    }

    @Override
    public void onModifyAmmunition(IAmmunition ammunition, CompoundTag dataRateTag) {
        dataRateTag.putFloat(Ammunition.BASE_DAMAGE_RATE, dataRateTag.getFloat(Ammunition.BASE_DAMAGE_RATE) + 1f);
        dataRateTag.putFloat(Ammunition.PENETRATION_RATE, dataRateTag.getFloat(Ammunition.PENETRATION_RATE) - 0.6f);
    }
}
