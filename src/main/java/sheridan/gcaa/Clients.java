package sheridan.gcaa;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.joml.Vector3f;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.ClientWeaponLooper;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.model.guns.AkmModel;
import sheridan.gcaa.client.model.guns.G19Model;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.fx.muzzleFlash.CommonMuzzleFlashes;
import sheridan.gcaa.client.render.fx.muzzleFlash.MuzzleFlashDisplayData;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.guns.IGun;
import sheridan.gcaa.items.guns.IGunFireMode;
import sheridan.gcaa.lib.ArsenalLib;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static sheridan.gcaa.client.render.DisplayData.DataType.POS;
import static sheridan.gcaa.client.render.DisplayData.DataType.ROT;
import static sheridan.gcaa.client.render.DisplayData.DataType.SCALE;

public class Clients {
    @OnlyIn(Dist.CLIENT)
    public static ClientWeaponStatus mainHandStatus = new ClientWeaponStatus(true);
    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public static ClientWeaponStatus offHandStatus = new ClientWeaponStatus(false);
    @OnlyIn(Dist.CLIENT)
    public static boolean mainButtonDown() {
        return mainHandStatus.buttonDown.get();
    }
    @OnlyIn(Dist.CLIENT)
    public static boolean holdingGun() {
        return mainHandStatus.holdingGun.get();
    }
    @OnlyIn(Dist.CLIENT)
    public static ReentrantLock lock = new ReentrantLock();
    @OnlyIn(Dist.CLIENT)
    public static boolean clientRegistriesHandled = false;
    @OnlyIn(Dist.CLIENT)
    public static Timer clientWeaponLooperTimer = new Timer();
    @OnlyIn(Dist.CLIENT)
    public static AtomicBoolean cancelLooperWork = new AtomicBoolean(false);
    @OnlyIn(Dist.CLIENT)
    public static AtomicBoolean cancelLooperWorkWithCoolDown = new AtomicBoolean(false);
    @OnlyIn(Dist.CLIENT)
    public static int clientPlayerId = 0;

    @OnlyIn(Dist.CLIENT)
    public static void onSetUp(final FMLClientSetupEvent event) {
        clientWeaponLooperTimer.scheduleAtFixedRate(new ClientWeaponLooper(), 0, 5L);
        ArsenalLib.registerGunModel(ModItems.G19.get(), new G19Model(), new DisplayData()
                .setFirstPersonMain(-4.7f,12f,-30f, POS).set(DisplayData.FIRST_PERSON_MAIN, 1f, SCALE)
                .setThirdPersonRight(0f, 0f, 0.5f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 0, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(0f, 0f, 0, POS).setFrame(0f, 0f, 0, ROT).set(DisplayData.FIXED, 0.3f, SCALE));

        ArsenalLib.registerGunModel(ModItems.AKM.get(), new AkmModel(), new DisplayData()
                .setFirstPersonMain(-7f,21.8f,-22f, POS).set(DisplayData.FIRST_PERSON_MAIN, 1.2f, SCALE)
                .setThirdPersonRight(0.0f,-0.2f,1.3f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 0, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(0f, 0f, 0, POS).setFrame(0f, 0f, 0, ROT).set(DisplayData.FIXED, 0.3f, SCALE)
                .setInertialRecoilData(new InertialRecoilData(0.075f, 0.06f, 0.625f, 0.08f, 0.9f,  0.08f, 0.4f, 0.28f, new Vector3f(0.55f, 0.7f, 0.6f)))
                .addMuzzleFlash("normal", CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setTranslate(0f, 4.9f, -98.6f).setScale(1.8f))
        );

    }

    public static void updateClientPlayerStatus(int id, long lastShootLeft, long lastChamber, boolean reloading) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel != null) {
            Entity entity = clientLevel.getEntity(id);
            if (entity instanceof Player player) {
                player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((cap) -> {
                    cap.setLastShoot(lastShootLeft);
                    cap.setReloading(reloading);
                    cap.setLastChamberAction(lastChamber);
                    cap.dataChanged = false;
                });
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static int handleClientShoot(ItemStack stack, IGun gun, Player player) {
        try {
            lock.lock();
            IGunFireMode fireMode = gun.getFireMode(stack);
            if (fireMode != null && fireMode.canFire(player, stack, gun)) {
                fireMode.clientShoot(player, stack, gun);
                return mainHandStatus.fireDelay.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return 0;
    }
}
