package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Sniper;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.fireModes.HandAction;
import sheridan.gcaa.items.gun.propertyExtensions.HandActionExtension;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class Awp extends Sniper {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_762X51MM, 22, 12, 10f, 15f, 1.2f)
                    .setAmmunition(ModItems.AMMO_7_62X51MM.get());

    public Awp() {
        super(new GunProperties(3f, 0.25f, 3.8f, 2f, 0.1f,
                        5f, GunProperties.toRPM(45), getTicks(2.25f), getTicks(3.35f),
                        10, 4.8f, 1f, 0.1f, 0.1f, 22,
                        List.of(HandAction.HAND_ACTION), ModSounds.AWP_FIRE, ModSounds.AWP_FIRE_SUPPRESSED, caliber),
                new HandActionExtension("bolt_action_ads","bolt_action",
                        getTicks(0.5f), getTicks(1.25f), 8, true));
    }

}
