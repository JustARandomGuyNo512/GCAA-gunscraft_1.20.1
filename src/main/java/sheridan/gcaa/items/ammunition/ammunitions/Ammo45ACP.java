package sheridan.gcaa.items.ammunition.ammunitions;

import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.IAmmunitionMod;
import sheridan.gcaa.items.ammunition.ammunitionMods.AmmunitionMods;

import java.util.Set;

public class Ammo45ACP extends Ammunition {
    public Ammo45ACP() {
        super(400, 12, Set.of(
                AmmunitionMods.AP,
                AmmunitionMods.HOLLOW_POINT,
                AmmunitionMods.EXPLOSIVE,
                AmmunitionMods.INCENDIARY,
                AmmunitionMods.HEAL
        ));
    }
}
