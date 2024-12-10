package sheridan.gcaa.items.ammunition.ammunitions;

import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.ammunitionMods.AmmunitionMods;

import java.util.Set;

public class Ammo12Gauge extends Ammunition {
    public Ammo12Gauge() {
        super(100, 5, Set.of(
                AmmunitionMods.HEAL
        ));
    }
}
