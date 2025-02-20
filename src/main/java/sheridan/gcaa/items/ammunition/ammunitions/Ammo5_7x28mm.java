package sheridan.gcaa.items.ammunition.ammunitions;

import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.ammunitionMods.AmmunitionMods;

import java.util.Set;

public class Ammo5_7x28mm extends Ammunition {
    public Ammo5_7x28mm() {
        super(400, 8, Set.of(
                AmmunitionMods.AP,
                AmmunitionMods.EXPLOSIVE,
                AmmunitionMods.INCENDIARY,
                AmmunitionMods.HEAL,
                AmmunitionMods.SOFT_POINT,
                AmmunitionMods.EFFICIENT_PROPELLANT
        ));
    }
}
