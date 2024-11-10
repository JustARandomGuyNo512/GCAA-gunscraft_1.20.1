package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunition.AmmunitionMod;

public class HollowPoint extends AmmunitionMod {
    public HollowPoint() {
        super(new ResourceLocation(GCAA.MODID, "hollow_point"), 5, ICONS_0, new Vector4i(32, 0, 128, 128));
    }

}
