package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.HandActionGunProperties;
import sheridan.gcaa.items.gun.ProjectileData;
import sheridan.gcaa.items.gun.calibers.Caliber7_62x51mm;
import sheridan.gcaa.items.gun.fireModes.HandAction;
import sheridan.gcaa.sounds.ModSounds;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.List;

public class Awp extends HandActionGun {
    public Awp() {
        super(new HandActionGunProperties(2.5f, 0.25f, 3f, 1.8f, 0.1f, 5f, GunProperties.toRPM(45),
                RenderAndMathUtils.secondsToTicks(2.25f), RenderAndMathUtils.secondsToTicks(3.35f), 10,
                3.5f, 1f, 0.1f, 0.1f, 22, List.of(new HandAction()),
                ModSounds.AWP_FIRE, ModSounds.AWP_FIRE_SUPPRESSED,
                new Caliber7_62x51mm(new ProjectileData(22, 16, 10f, 10f))));
    }
}
