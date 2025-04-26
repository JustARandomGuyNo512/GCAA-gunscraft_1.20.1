package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.Sniper;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.HandAction;
import sheridan.gcaa.items.gun.propertyExtensions.HandActionExtension;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class FnBallista extends Sniper {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_338_LAPUA_MAGNUM, 40, 20, 15f, 18.5f, 1.6f)
                    .setAmmunition(ModItems.AMMO_338_LAPUA_MAGNUM.get());

    public FnBallista() {
        super(new GunProperties(2.8f, 0.2f, 4.4f, 3f, 0.1f,
                        6f, GunProperties.toRPM(40), getTicks(2.45f), getTicks(3.2f),
                        5, 6.8f, 1.5f, 0.1f, 0.1f, 25,
                        List.of(HandAction.HAND_ACTION), ModSounds.FN_BALLISTA_FIRE, ModSounds.FN_BALLISTA_FIRE_SUPPRESSED, caliber),
                new HandActionExtension("bolt_action","bolt_action",
                        getTicks(0.4f), getTicks(1.4f), getTicks(0.6f), false));
    }
}
