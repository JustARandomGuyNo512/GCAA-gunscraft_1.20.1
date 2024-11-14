package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector2i;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.common.damageTypes.DamageTypes;
import sheridan.gcaa.common.damageTypes.ProjectileDamage;
import sheridan.gcaa.common.server.projetile.Projectile;
import sheridan.gcaa.common.server.projetile.ProjectileHandler;
import sheridan.gcaa.items.ammunition.AmmunitionMod;
import sheridan.gcaa.items.gun.IGun;
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

    @Override
    public void onHitEntity(Projectile projectile, Entity entity, boolean isHeadSHot, IGun gun, ProjectileHandler.AmmunitionDataCache cache) {
        float baseDamage = projectile.damage / cache.baseDamageRate();
        //TODO: 点燃目标 entity， 给予额外 10% 的燃烧伤害
    }

    public float getFireDamageRate() {
        return fireDamageRate;
    }

}
