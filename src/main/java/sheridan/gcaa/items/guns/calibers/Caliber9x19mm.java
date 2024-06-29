package sheridan.gcaa.items.guns.calibers;

import sheridan.gcaa.entities.projectiles.effects.IBulletEffectProcessor;
import sheridan.gcaa.items.ammunitions.IAmmunition;
import sheridan.gcaa.items.guns.ICaliber;
import sheridan.gcaa.items.guns.IGun;

import java.util.List;

public class Caliber9x19mm implements ICaliber {

    public static final Caliber9x19mm INSTANCE = new Caliber9x19mm();

    @Override
    public String getName() {
        return "9x19mm";
    }

    @Override
    public void fireBullet(IAmmunition ammunition, List<IBulletEffectProcessor> effectProcessors, IGun gun) {

    }

    @Override
    public int getCost() {
        return 5;
    }
}
