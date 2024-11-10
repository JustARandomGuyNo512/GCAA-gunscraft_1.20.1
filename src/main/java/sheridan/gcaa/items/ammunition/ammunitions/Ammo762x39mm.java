package sheridan.gcaa.items.ammunition.ammunitions;

import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.ammunitionMods.AmmunitionMods;

import java.util.Set;

public class Ammo762x39mm extends Ammunition {
    public Ammo762x39mm() {
        super(220, 12, Set.of(
                AmmunitionMods.AP,
                AmmunitionMods.EXPLOSIVE,
                AmmunitionMods.INCENDIARY
        ));
    }
}
