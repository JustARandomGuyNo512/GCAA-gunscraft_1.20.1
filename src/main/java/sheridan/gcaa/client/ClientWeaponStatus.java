package sheridan.gcaa.client;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.gun.IGun;

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
    public final AtomicBoolean ads;
    public final AtomicReference<ItemStack> weapon;
    public float equipProgress;
    public int fireCount = 0;
    public int chargeTick = 0;
    public float adsProgress = 0;
    private float lastAdsProgress = 0;
    public float adsSpeed = 0;
    public long lastShoot = 0;
    public int equipDelay = 0;
    public float spread = 0;
    public Vector3f adsPos;
    public Vector3f adsRot;
    public float lastRecoilDirection = 1;

    public ClientWeaponStatus(boolean mainHand) {
        this.mainHand = mainHand;
        buttonDown = new AtomicBoolean(false);
        holdingGun = new AtomicBoolean(false);
        ads = new AtomicBoolean(false);
        fireDelay = new AtomicInteger(0);
        weapon = new AtomicReference<>(ItemStack.EMPTY);
        attachmentsStatus = new ClientAttachmentsStatus();
    }

    public void handleAds(ItemStack stack, IGun gun, Player player) {
        if (ads.get()) {
            if (gun != null && !player.swinging) {
                if (gun.shouldHandleAds(stack)) {
                    lastAdsProgress = adsProgress;
                    adsProgress = Math.min(adsSpeed + adsProgress, 1);
                } else {
                    exitAds();
                }
            } else {
                ads.set(false);
            }
        } else {
            if (adsProgress != 0 || lastAdsProgress != 0) {
                exitAds();
            }
        }
    }

    private void exitAds() {
        float speed = adsSpeed == 0 ? 0.005f : adsSpeed;
        speed = Math.min(speed * 1.5f, 0.25f);
        lastAdsProgress = adsProgress;
        adsProgress = Math.max(adsProgress - speed * 1.5f, 0);
    }

    public void updatePlayerSpread(ItemStack stack, IGun gun, Player player) {
        if (gun != null) {
            float[] spread = gun.getSpread(stack);
            float minSpread = spread[0];
            float maxSpread = spread[1];
            if (player.isCrouching()) {
                minSpread *= 0.75f;
                maxSpread *= 0.75f;
            }
            if (player.xxa != 0 || player.yya != 0 || player.zza != 0) {
                float spreadFactor = player.isSprinting() ? gun.getSprintingSpreadFactor(stack) : gun.getWalkingSpreadFactor(stack);
                minSpread *= spreadFactor;
                maxSpread *= spreadFactor;
            }
            float fallFactor = player.fallDistance < 10 ? player.fallDistance * 0.15f : 1.5f;
            minSpread += fallFactor;
            maxSpread += fallFactor;
            this.spread = Mth.clamp(this.spread - gun.getSpreadRecover(stack), minSpread, maxSpread);
        } else {
            if (this.spread != 0) {
                this.spread = Math.max(this.spread - 0.1f, 0);
            }
        }
    }

    public float getLerpAdsProgress(float particleTick) {
        return Mth.lerp(particleTick, lastAdsProgress, adsProgress);
    }
}
