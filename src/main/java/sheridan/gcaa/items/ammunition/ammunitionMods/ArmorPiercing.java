package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.AmmunitionMod;
import sheridan.gcaa.items.ammunition.IAmmunition;

import java.awt.*;

public class ArmorPiercing extends AmmunitionMod {
    public ArmorPiercing() {
        super(new ResourceLocation(GCAA.MODID, "armor_piercing"), 3, ICONS_0, new Vector4i(0, 0, 128, 128)
        ,"gcaa.ammunition_mod.armor_piercing", new Color(0x53257b).getRGB());
    }

    @Override
    public void onModifyAmmunition(IAmmunition ammunition, CompoundTag dataRateTag) {
        dataRateTag.putFloat(Ammunition.BASE_DAMAGE_RATE, dataRateTag.getFloat(Ammunition.BASE_DAMAGE_RATE) - 0.2f);
        dataRateTag.putFloat(Ammunition.MIN_DAMAGE_RATE, dataRateTag.getFloat(Ammunition.MIN_DAMAGE_RATE) - 0.2f);
        dataRateTag.putFloat(Ammunition.PENETRATION_RATE, dataRateTag.getFloat(Ammunition.PENETRATION_RATE) + 1f);
        dataRateTag.putFloat(Ammunition.EFFECTIVE_RANGE_RATE, dataRateTag.getFloat(Ammunition.EFFECTIVE_RANGE_RATE) + 0.2f);
        dataRateTag.putFloat(Ammunition.SPEED_RATE, dataRateTag.getFloat(Ammunition.SPEED_RATE) + 0.1f);
    }
}
