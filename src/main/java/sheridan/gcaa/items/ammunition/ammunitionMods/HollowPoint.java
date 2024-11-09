package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunition.AmmunitionMod;

public class HollowPoint extends AmmunitionMod {
    public HollowPoint() {
        super(new ResourceLocation(GCAA.MODID, "hollow_point"), 5);
    }
}
