package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunition.AmmunitionMod;

public class Explosive extends AmmunitionMod {
    public Explosive() {
        super(new ResourceLocation(GCAA.MODID, "explosive"), 1, ICONS_0, new Vector4i(16, 0, 128, 128));
    }

}
