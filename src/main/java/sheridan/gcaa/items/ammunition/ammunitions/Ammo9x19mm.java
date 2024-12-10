package sheridan.gcaa.items.ammunition.ammunitions;

import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.ammunitionMods.AmmunitionMods;

import java.util.Set;

public class Ammo9x19mm extends Ammunition {
    public Ammo9x19mm() {
        super(500, 10, Set.of(
                AmmunitionMods.AP,
                AmmunitionMods.HOLLOW_POINT,
                AmmunitionMods.EXPLOSIVE,
                AmmunitionMods.INCENDIARY,
                AmmunitionMods.HEAL
        ));
    }
}
