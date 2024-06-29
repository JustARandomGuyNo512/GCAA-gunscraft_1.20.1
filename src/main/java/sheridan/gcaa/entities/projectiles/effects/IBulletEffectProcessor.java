package sheridan.gcaa.entities.projectiles.effects;

import sheridan.gcaa.entities.projectiles.IBullet;

public interface IBulletEffectProcessor {
    void process(IBullet bullet);
    int getCost();
    String getName();
}
