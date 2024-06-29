package sheridan.gcaa.entities.projectiles;

import net.minecraft.world.entity.LivingEntity;

public interface IBullet {
    void shoot(LivingEntity shooter, float speed, float spread, float baseDamage, float miniDamage, float effectiveRange);
    Bullet get();
}
