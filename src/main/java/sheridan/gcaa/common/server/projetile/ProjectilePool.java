package sheridan.gcaa.common.server.projetile;

import net.minecraft.world.entity.LivingEntity;

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

    public Projectile getOrCreate(LivingEntity shooter, float speed, float damage, float spread, float effectiveRange) {
        Projectile projectile = pool.pollFirst();
        if (projectile == null) {
            Projectile newProjectile = new Projectile();
            newProjectile.shoot(shooter, speed, damage, spread, effectiveRange);
            return newProjectile;
        }
        projectile.shoot(shooter, speed, damage, spread, effectiveRange);
        return projectile;
    }

    public void returnProjectile(Projectile projectile) {
        projectile.reset();
        pool.offerFirst(projectile);
    }
}
