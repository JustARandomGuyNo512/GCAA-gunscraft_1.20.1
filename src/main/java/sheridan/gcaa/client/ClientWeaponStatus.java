package sheridan.gcaa.client;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@OnlyIn(Dist.CLIENT)
public class ClientWeaponStatus {
    public final ClientAttachmentsStatus attachmentsStatus;
    public final boolean mainHand;
    public final AtomicBoolean buttonDown;
    public final AtomicBoolean holdingGun;
    public final AtomicInteger fireDelay;
    public final AtomicReference<ItemStack> weapon;
    public float equipProgress;
    public int fireCount = 0;
    public int chargeTick = 0;
    public boolean ads = false;
    public float adsProgress = 0;
    public long lastShoot = 0;
    public long lastReload = 0;

    public ClientWeaponStatus(boolean mainHand) {
        this.mainHand = mainHand;
        buttonDown = new AtomicBoolean(false);
        holdingGun = new AtomicBoolean(false);
        fireDelay = new AtomicInteger(0);
        weapon = new AtomicReference<>(ItemStack.EMPTY);
        attachmentsStatus = new ClientAttachmentsStatus();
    }

    public void onGunEquipped() {

    }

    public void onGunUnEquipped() {

    }
}
