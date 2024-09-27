package sheridan.gcaa.entities.projectiles;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public interface IProjectile {
    Predicate<Entity> GENERIC_TARGETS = (input) -> input instanceof LivingEntity && !input.isSpectator() && input.isAlive();

    //void shoot(LivingEntity shooter, float speed, float spread, float baseDamage, float miniDamage, float effectiveRang, Vec3 angle);
}
