package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunition.AmmunitionMod;

public class Explosive extends AmmunitionMod {
    public Explosive() {
        super(new ResourceLocation(GCAA.MODID, "explosive"), 1);
    }
}
