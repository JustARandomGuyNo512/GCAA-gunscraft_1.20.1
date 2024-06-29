package sheridan.gcaa.items.guns;

import sheridan.gcaa.entities.projectiles.effects.IBulletEffectProcessor;
import sheridan.gcaa.items.ammunitions.IAmmunition;

import java.util.List;

public interface ICaliber {
    String getName();
    void fireBullet(IAmmunition ammunition, List<IBulletEffectProcessor> effectProcessors, IGun gun);
    int getCost();
}
