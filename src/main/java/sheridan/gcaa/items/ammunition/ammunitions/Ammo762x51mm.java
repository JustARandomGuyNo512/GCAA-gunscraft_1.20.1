package sheridan.gcaa.items.ammunition.ammunitions;

import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.ammunitionMods.AmmunitionMods;

import java.util.Set;

public class Ammo762x51mm extends Ammunition {
    public Ammo762x51mm() {
        super(200, 15, Set.of(
                AmmunitionMods.AP,
                AmmunitionMods.EXPLOSIVE,
                AmmunitionMods.INCENDIARY,
                AmmunitionMods.HEAL,
                AmmunitionMods.SOFT_POINT,
                AmmunitionMods.EFFICIENT_PROPELLANT
        ));
    }
}
