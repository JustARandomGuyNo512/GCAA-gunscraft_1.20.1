package sheridan.gcaa.common.server.projetile;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import sheridan.gcaa.items.gun.IGun;

import java.util.ArrayDeque;
import java.util.Deque;

public class ProjectilePool {
    private final Deque<Projectile> pool;

    public ProjectilePool(int initialSize) {
        pool = new ArrayDeque<>(initialSize);
        for (int i = 0; i < initialSize; i++) {
            pool.offerFirst(new Projectile());
        }
    }

    public Projectile getOrCreate(LivingEntity shooter, Vec3 angle, float speed, float damage, float spread, float effectiveRange, IGun gun) {
        Projectile projectile = pool.pollFirst();
        if (projectile == null) {
            Projectile newProjectile = new Projectile();
            newProjectile.shoot(shooter, angle, speed, damage, spread, effectiveRange, gun);
            return newProjectile;
        }
        projectile.shoot(shooter, angle, speed, damage, spread, effectiveRange, gun);
        return projectile;
    }

    public Projectile getOrCreate(LivingEntity shooter, float speed, float damage, float spread, float effectiveRange, IGun gun) {
        return getOrCreate(shooter, shooter.getLookAngle(), speed, damage, spread, effectiveRange, gun);
    }

    public void returnProjectile(Projectile projectile) {
        projectile.reset();
        pool.offerFirst(projectile);
    }
}
