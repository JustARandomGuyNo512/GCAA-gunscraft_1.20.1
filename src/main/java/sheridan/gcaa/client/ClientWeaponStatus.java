package sheridan.gcaa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.client.events.RenderEvents;
import sheridan.gcaa.items.ammunition.IAmmunitionMod;
import sheridan.gcaa.items.attachments.IArmReplace;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.attachments.Scope;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;
import sheridan.gcaa.items.gun.Sniper;
import sheridan.gcaa.items.gun.fireModes.Charge;

import java.util.ArrayList;
import java.util.List;
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
    public String identity;
    public boolean ads;
    public float equipProgress;
    public int fireCount = 0;
    public int chargeTick = 0;
    private int lastChargeTick = 0;
    private int chargeLength = 0;
    public float adsProgress = 0;
    private float lastAdsProgress = 0;
    public float adsSpeed = 0;
    public long lastShoot = 0;
    public int equipDelay = 0;
    public float spread = 0;
    public float minSpread = 0;
    public float maxSpread = 0;
    public float lastRecoilDirection = 1;
    public List<IAmmunitionMod> ammunitionMods;
    public boolean shootCreateMuzzleSmoke = true;
    @Nullable
    public IGunFireMode fireMode;
    private String ammunitionModsUUID = "none";
    private static boolean switchSightTooltipShowed = false;

    public ClientWeaponStatus(boolean mainHand) {
        this.mainHand = mainHand;
        buttonDown = new AtomicBoolean(false);
        holdingGun = new AtomicBoolean(false);
        ads = false;
        fireDelay = new AtomicInteger(0);
        weapon = new AtomicReference<>(ItemStack.EMPTY);
        attachmentsStatus = new ClientAttachmentsStatus(this);
        identity = "";
        ammunitionMods = new ArrayList<>();
    }

    public void handleAds(ItemStack stack, IGun gun, Player player) {
        if (ads) {
            if (gun != null && !player.swinging) {
                if (gun.shouldHandleAds(stack)) {
                    lastAdsProgress = adsProgress;
                    adsProgress = Math.min(adsSpeed + adsProgress, 1);
                    float sprinting = 1 - SprintingHandler.INSTANCE.getSprintingProgress();
                    float r1 = 1.001f - adsProgress;
                    float r2 = 1.001f - sprinting;
                    adsProgress = (adsProgress * r1 + sprinting * r2) / (r1 + r2);
                    if (adsProgress == 1) {
                        if (!switchSightTooltipShowed && attachmentsStatus.sights.size() > 1) {
                            String info = Component.translatable("tooltip.screen_info.switch_sight").getString();
                            info = info.replace("$key", KeyBinds.SWITCH_EFFECTIVE_SIGHT.getTranslatedKeyMessage().getString());
                            Minecraft.getInstance().gui.setOverlayMessage(Component.literal(info), false);
                            switchSightTooltipShowed = true;
                        }
                        if (getEffectiveSight() instanceof Scope scope && lastAdsProgress != 1) {
                            RenderEvents.renderScopeMagnificationTip(scope, getScopeMagnificationRate(), 0xffffff);
                        }
                    }
                } else {
                    exitAds();
                }
            } else {
                ads = false;
            }
        } else {
            if (adsProgress != 0 || lastAdsProgress != 0) {
                exitAds();
            }
        }
    }

    private void exitAds() {
        float speed = adsSpeed == 0 ? 0.005f : adsSpeed;
        speed = Math.min(speed * 1.3f, 0.25f);
        lastAdsProgress = adsProgress;
        adsProgress = Math.max(adsProgress - speed, 0);
    }

    public float getLerpedChargeTick(float particleTick) {
        return chargeLength == 0 ? 0 :
                Mth.lerp(particleTick, lastChargeTick, chargeTick) / chargeLength * (chargeLength / (chargeLength - 1f));
    }

    public void clearCharge() {
        chargeTick = 0;
        lastChargeTick = 0;
    }

    public void updateAmmunitionMods(ItemStack stack, IGun gun) {
        if (gun == null) {
            ammunitionModsUUID = "none";
            ammunitionMods.clear();
            resetAmmunitionModsOptions();
            return;
        }
        CompoundTag ammunitionData = gun.getGun().getAmmunitionData(stack);
        if (ammunitionData.contains("using")) {
            CompoundTag using = ammunitionData.getCompound("using");
            if (using.contains("mods")) {
                String modsUUID = using.getCompound("mods").getString("modsUUID");
                if (!ammunitionModsUUID.equals(modsUUID)) {
                    ammunitionModsUUID = modsUUID;
                    CompoundTag mods = using.getCompound("mods");
                    ammunitionMods = gun.getGunProperties().caliber.ammunition.getMods(mods);
                    resetAmmunitionModsOptions();
                    handleAmmunitionModHooks();
                }
                return;
            }
        }
        ammunitionModsUUID = "none";
        ammunitionMods.clear();
        resetAmmunitionModsOptions();
    }

    private void resetAmmunitionModsOptions() {
        shootCreateMuzzleSmoke = true;
    }

    private void handleAmmunitionModHooks() {
        for (IAmmunitionMod mod : ammunitionMods) {
            mod.onEquippedClient();
        }
    }

    public void updateChargeTick(ItemStack stack, IGun gun) {
        if (gun != null && gun.getFireMode(stack) instanceof Charge charge) {
            lastChargeTick = chargeTick;
            chargeLength = charge.getChargeLength();
            chargeTick = (buttonDown.get() && gun.getAmmoLeft(stack) > 0) ?
                    Math.min(chargeLength, chargeTick + 1) :
                    Math.max(0, chargeTick - 2);
        } else {
            chargeTick = 0;
            lastChargeTick = 0;
            chargeLength = 0;
        }
    }

    public void updatePlayerSpread(ItemStack stack, IGun gun, Player player) {
        if (gun != null) {
            float[] spread = gun.getSpread(stack);
            minSpread = spread[0];
            maxSpread = spread[1];
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

    public float[] getSightAimPos(float particleTick) {
        return attachmentsStatus.getLerpedSightAimPos(particleTick);
    }

    public float getLerpAdsProgress(float particleTick) {
        return Mth.lerp(particleTick, lastAdsProgress, adsProgress);
    }

    public float getLerpAdsProgress(double particleTick) {
        return (float) Mth.lerp(particleTick, lastAdsProgress, adsProgress);
    }

    public IAttachment getEffectiveSight() {
        return  attachmentsStatus == null ? null : attachmentsStatus.getEffectiveSight();
    }

    public float getScopeMagnificationRate() {
        return attachmentsStatus == null ? 0 : attachmentsStatus.getScopeMagnificationRate();
    }

    public boolean setScopeMagnificationRate(float val) {
        return attachmentsStatus != null && attachmentsStatus.setScopeMagnificationRate(val);
    }

    public AttachmentSlot getLeftArmReplace() {
        return  attachmentsStatus == null ? null : attachmentsStatus.leftArmReplace;
    }

    public IArmReplace getLeftArmReplaceAttachment() {
        AttachmentSlot slot = getLeftArmReplace();
        if (slot != null && AttachmentsRegister.get(slot.getAttachmentId()) instanceof IArmReplace attachment) {
            return attachment;
        }
        return null;
    }

    public IArmReplace getRightArmReplaceAttachment() {
        AttachmentSlot slot = getRightArmReplace();
        if (slot != null && AttachmentsRegister.get(slot.getAttachmentId()) instanceof IArmReplace attachment) {
            return attachment;
        }
        return null;
    }

    public AttachmentSlot getRightArmReplace() {
        return  attachmentsStatus == null ? null : attachmentsStatus.rightArmReplace;
    }

    public boolean isHoldingGun() {
        return holdingGun.get();
    }

}
