package sheridan.gcaa.items.ammunitions;

import sheridan.gcaa.items.gun.calibers.Caliber;

import java.util.List;

public interface IAmmunition {
    List<Caliber> getCalibersProvided();
    int getBulletLeftFor(Caliber caliber);
    int getMaxPoints();
    int getPointsLeft();
    int getEffectCapacity();
}
