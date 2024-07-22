package sheridan.gcaa.entities.projectiles.damageTypes;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import sheridan.gcaa.GCAA;

import java.util.Locale;

public class DamageTypes {
    public static final ResourceKey<DamageType> GENERIC_PROJECTILE = create("projectile");

    public static ResourceKey<DamageType> create(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(GCAA.MODID, name.toLowerCase(Locale.ROOT)));
    }

    public static DamageSource getDamageSource(Level level, ResourceKey<DamageType> type, Entity attacker, Entity indirectAttacker) {
        return new ProjectileDamage(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), attacker, indirectAttacker);
    }
}
