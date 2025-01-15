package sheridan.gcaa.items.ammunition.ammunitions;

import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.ammunitionMods.AmmunitionMods;

import java.util.Set;

public class Ammo545x39mm extends Ammunition {
    public Ammo545x39mm() {
        super(330, 10, Set.of(
                AmmunitionMods.AP,
                AmmunitionMods.EXPLOSIVE,
                AmmunitionMods.INCENDIARY,
                AmmunitionMods.HEAL,
                AmmunitionMods.EFFICIENT_PROPELLANT
        ));
    }
}
