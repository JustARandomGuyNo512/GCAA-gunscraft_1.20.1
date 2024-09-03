package sheridan.gcaa.items.ammunitions;

import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.entities.projectiles.effects.IBulletEffectProcessor;
import sheridan.gcaa.items.gun.calibers.Caliber;

import java.util.List;

public interface IAmmunition {
    List<Caliber> getCalibersProvided();
    int getBulletLeftFor(Caliber caliber);
    int getMaxPoints();
    int getPointsLeft();
    List<IBulletEffectProcessor> getBulletEffectProcessors(ItemStack stack);
    int getEffectCapacity();
}
