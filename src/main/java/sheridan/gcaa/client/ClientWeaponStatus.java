package sheridan.gcaa.client;

import java.util.concurrent.atomic.AtomicBoolean;

public class ClientWeaponStatus {
    public final boolean mainHand;
    public AtomicBoolean buttonDown;
    public AtomicBoolean holdingGun;
    public float equipProgress;
    public int fireCount = 0;
    public boolean ads = false;
    public float adsProgress = 0;
    public long lastFire = 0;
    public long lastReload = 0;

    public ClientWeaponStatus(boolean mainHand) {
        this.mainHand = mainHand;
        buttonDown = new AtomicBoolean(false);
        holdingGun = new AtomicBoolean(false);
    }
}
