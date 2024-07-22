package sheridan.gcaa.entities.projectiles.effects;

import sheridan.gcaa.entities.projectiles.IProjectile;

public interface IBulletEffectProcessor {
    void process(IProjectile bullet);
    int getCost();
    String getName();
}
