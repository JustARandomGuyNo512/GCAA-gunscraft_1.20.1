package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunition.AmmunitionMod;

public class ArmorPiercing extends AmmunitionMod {
    public ArmorPiercing() {
        super(new ResourceLocation(GCAA.MODID, "armor_piercing"), 1, ICONS_0, new Vector4i(0, 0, 128, 128));
    }

}
