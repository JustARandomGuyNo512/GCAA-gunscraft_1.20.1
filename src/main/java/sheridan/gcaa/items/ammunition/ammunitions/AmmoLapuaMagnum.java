package sheridan.gcaa.items.ammunition.ammunitions;

import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.ammunitionMods.AmmunitionMods;

import java.util.Set;

public class AmmoLapuaMagnum extends Ammunition {
    public AmmoLapuaMagnum() {
        super(100, 18, Set.of(
                AmmunitionMods.AP,
                AmmunitionMods.EXPLOSIVE,
                AmmunitionMods.INCENDIARY,
                AmmunitionMods.HEAL,
                AmmunitionMods.EFFICIENT_PROPELLANT
        ));
    }
}
