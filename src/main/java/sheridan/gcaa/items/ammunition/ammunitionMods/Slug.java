package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.AmmunitionMod;
import sheridan.gcaa.items.ammunition.IAmmunition;

import java.awt.*;

public class Slug extends AmmunitionMod {
    public Slug() {
        super(new ResourceLocation(GCAA.MODID, "slug"), 8, ICONS_0, new Vector4i(112, 0, 128, 128),
                "gcaa.ammunition_mod.slug", new Color(0xbaa6Ad).getRGB(), 150);
    }

    @Override
    public int getCostFor(IAmmunition ammunition) {
        return ammunition.getMaxModCapacity();
    }

    @Override
    public net.minecraft.network.chat.Component getSpecialDescription() {
        return Component.translatable("gcaa.ammunition_mod.slug_special");
    }

    @Override
    public void onModifyAmmunition(IAmmunition ammunition, CompoundTag dataRateTag) {
        dataRateTag.putFloat(Ammunition.BASE_DAMAGE_RATE, dataRateTag.getFloat(Ammunition.BASE_DAMAGE_RATE) + 6f);
        dataRateTag.putFloat(Ammunition.MIN_DAMAGE_RATE, dataRateTag.getFloat(Ammunition.MIN_DAMAGE_RATE) + 6f);
        dataRateTag.putFloat(Ammunition.PENETRATION_RATE, dataRateTag.getFloat(Ammunition.PENETRATION_RATE) + 1f);
        dataRateTag.putFloat(Ammunition.EFFECTIVE_RANGE_RATE, dataRateTag.getFloat(Ammunition.EFFECTIVE_RANGE_RATE) + 2f);
        dataRateTag.putFloat(Ammunition.SPEED_RATE, dataRateTag.getFloat(Ammunition.SPEED_RATE) + 0.3f);
    }
}
