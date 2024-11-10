package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunition.AmmunitionMod;

public class Incendiary extends AmmunitionMod {
    public Incendiary() {
        super(new ResourceLocation(GCAA.MODID, "incendiary"), 2, ICONS_0, new Vector4i(48, 0, 128, 128));
    }

}
