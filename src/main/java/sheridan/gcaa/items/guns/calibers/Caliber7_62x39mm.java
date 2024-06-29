package sheridan.gcaa.items.guns.calibers;

import sheridan.gcaa.entities.projectiles.effects.IBulletEffectProcessor;
import sheridan.gcaa.items.ammunitions.IAmmunition;
import sheridan.gcaa.items.guns.ICaliber;
import sheridan.gcaa.items.guns.IGun;

import java.util.List;

public class Caliber7_62x39mm implements ICaliber {
    public static final Caliber7_62x39mm INSTANCE = new Caliber7_62x39mm();

    @Override
    public String getName() {
        return "7.62x39mm";
    }

    @Override
    public void fireBullet(IAmmunition ammunition, List<IBulletEffectProcessor> effectProcessors, IGun gun) {

    }

    @Override
    public int getCost() {
        return 7;
    }
}
