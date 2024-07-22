package sheridan.gcaa.entities.projectiles;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface IProjectile {
    void shoot(LivingEntity shooter, float speed, float spread, float baseDamage, float miniDamage, float effectiveRang, Vec3 angle);
    Projectile get();
}
