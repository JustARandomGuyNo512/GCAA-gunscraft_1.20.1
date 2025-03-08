package sheridan.gcaa.items.ammunition.ammunitions;

import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.ammunitionMods.AmmunitionMods;

import java.util.Set;

public class Ammo6_8x51mm extends Ammunition {
    public Ammo6_8x51mm() {
        super(220, 13, Set.of(
                AmmunitionMods.AP,
                AmmunitionMods.EXPLOSIVE,
                AmmunitionMods.INCENDIARY,
                AmmunitionMods.HEAL,
                AmmunitionMods.SOFT_POINT,
                AmmunitionMods.EFFICIENT_PROPELLANT
        ));
    }
}

