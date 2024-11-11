package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunition.AmmunitionMod;
import sheridan.gcaa.utils.FontUtils;

import java.awt.*;

public class Incendiary extends AmmunitionMod {
    private final float fireDamageRate = 0.1f;

    public Incendiary() {
        super(new ResourceLocation(GCAA.MODID, "incendiary"), 2, ICONS_0, new Vector4i(48, 0, 128, 128),
                "gcaa.ammunition_mod.incendiary", new Color(0xee2816).getRGB());
    }

    @Override
    public Component getSpecialDescription() {
        String str = Component.translatable("gcaa.ammunition_mod.incendiary_special").getString().replace("$rate", FontUtils.toPercentageStr(fireDamageRate));
        return Component.empty().append(Component.literal(str));
    }

    public float getFireDamageRate() {
        return fireDamageRate;
    }
}
