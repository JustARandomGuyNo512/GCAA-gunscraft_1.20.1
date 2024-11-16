package sheridan.gcaa.common.server.projetile;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import sheridan.gcaa.items.ammunition.IAmmunitionMod;
import sheridan.gcaa.items.gun.IGun;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class ProjectilePool {
    private final Deque<Projectile> pool;

    public ProjectilePool(int initialSize) {
        pool = new ArrayDeque<>(initialSize);
        for (int i = 0; i < initialSize; i++) {
            pool.offerFirst(new Projectile());
        }
    }

    public Projectile getOrCreate(LivingEntity shooter, Vec3 angle, float penetration, float speed, float damage, float minDamage, float spread, float effectiveRange, IGun gun, String modsUUID) {
        Projectile projectile = pool.pollFirst();
        if (projectile == null) {
            Projectile newProjectile = new Projectile();
            newProjectile.shoot(shooter, angle, penetration, speed, damage, minDamage, spread, effectiveRange, gun, modsUUID);
            return newProjectile;
        }
        projectile.shoot(shooter, angle, penetration, speed, damage, minDamage, spread, effectiveRange, gun, modsUUID);
        return projectile;
    }

    public Projectile getOrCreate(LivingEntity shooter, float penetration, float speed, float damage, float minDamage, float spread, float effectiveRange, IGun gun, String modsUUID) {
        return getOrCreate(shooter, shooter.getLookAngle(), penetration, speed, damage, minDamage, spread, effectiveRange, gun, modsUUID);
    }

    public void returnProjectile(Projectile projectile) {
        projectile.reset();
        pool.offerFirst(projectile);
    }

    public int size() {
        return pool.size();
    }
}
