package sheridan.gcaa;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;
import sheridan.gcaa.addon.AddonHandler;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.ClientWeaponLooper;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.animation.recoilAnimation.RecoilData;
import sheridan.gcaa.client.events.RenderEvents;
import sheridan.gcaa.client.model.BulletShellModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.attachments.akStuff.AKImprovedDustCoverModel;
import sheridan.gcaa.client.model.attachments.akStuff.AKRailBracketModel;
import sheridan.gcaa.client.model.attachments.arStuff.ARGasBlockModel;
import sheridan.gcaa.client.model.attachments.arStuff.ARStockTubeModel;
import sheridan.gcaa.client.model.attachments.functional.GP_25Model;
import sheridan.gcaa.client.model.attachments.functional.M203Model;
import sheridan.gcaa.client.model.attachments.grip.*;
import sheridan.gcaa.client.model.attachments.handguard.AKImprovedHandguardModel;
import sheridan.gcaa.client.model.attachments.handguard.ARLightHandguardModel;
import sheridan.gcaa.client.model.attachments.handguard.ARLightHandguardShortModel;
import sheridan.gcaa.client.model.attachments.handguard.ARRailedHandguardModel;
import sheridan.gcaa.client.model.attachments.mag.*;
import sheridan.gcaa.client.model.attachments.muzzle.*;
import sheridan.gcaa.client.model.attachments.other.GlockMountModel;
import sheridan.gcaa.client.model.attachments.other.RailClampModel;
import sheridan.gcaa.client.model.attachments.scope.AcogModel;
import sheridan.gcaa.client.model.attachments.scope.ElcanModel;
import sheridan.gcaa.client.model.attachments.scope.ScopeX10Model;
import sheridan.gcaa.client.model.attachments.sight.*;
import sheridan.gcaa.client.model.attachments.stock.AKTacticalStockModel;
import sheridan.gcaa.client.model.attachments.stock.CTRStockModel;
import sheridan.gcaa.client.model.attachments.stock.UBRStockModel;
import sheridan.gcaa.client.model.gun.guns.*;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.fx.bulletShell.BulletShellDisplayData;
import sheridan.gcaa.client.render.fx.muzzleFlash.CommonMuzzleFlashes;
import sheridan.gcaa.client.render.fx.muzzleFlash.MuzzleFlashDisplayData;
import sheridan.gcaa.client.screens.AmmunitionModifyScreen;
import sheridan.gcaa.client.screens.GunModifyScreen;
import sheridan.gcaa.client.screens.TransactionTerminalScreen;
import sheridan.gcaa.client.screens.VendingMachineScreen;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.ammunition.AmmunitionModRegister;
import sheridan.gcaa.items.ammunition.IAmmunitionMod;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;
import sheridan.gcaa.items.gun.Sniper;
import sheridan.gcaa.lib.ArsenalLib;
import sheridan.gcaa.sounds.ModSounds;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static sheridan.gcaa.client.render.DisplayData.DataType.POS;
import static sheridan.gcaa.client.render.DisplayData.DataType.ROT;
import static sheridan.gcaa.client.render.DisplayData.DataType.SCALE;

public class Clients {
    @OnlyIn(Dist.CLIENT)
    public static final ClientWeaponStatus MAIN_HAND_STATUS = new ClientWeaponStatus(true);
    @OnlyIn(Dist.CLIENT)
    public static boolean mainButtonDown() {
        return MAIN_HAND_STATUS.buttonDown.get();
    }
    @OnlyIn(Dist.CLIENT)
    public static boolean holdingGun() {
        return MAIN_HAND_STATUS.holdingGun.get();
    }
    @OnlyIn(Dist.CLIENT)
    public static final ReentrantLock LOCK = new ReentrantLock();
    @OnlyIn(Dist.CLIENT)
    public static boolean clientRegistriesHandled = false;
    @OnlyIn(Dist.CLIENT)
    public static final Timer CLIENT_WEAPON_LOOPER_TIMER = new Timer();
    @OnlyIn(Dist.CLIENT)
    public static AtomicBoolean cancelLooperWork = new AtomicBoolean(false);
    @OnlyIn(Dist.CLIENT)
    public static AtomicBoolean cancelLooperWorkWithCoolDown = new AtomicBoolean(false);
    @OnlyIn(Dist.CLIENT)
    public static int clientPlayerId = 0;
    @OnlyIn(Dist.CLIENT)
    public static long lastShootMain() {
        return MAIN_HAND_STATUS.lastShoot;
    }
    @OnlyIn(Dist.CLIENT)
    public static volatile long lastClientTick;
    @OnlyIn(Dist.CLIENT)
    public static boolean handleWeaponBobbing = true;
    @OnlyIn(Dist.CLIENT)
    public static boolean debugKeyDown = false;
    @OnlyIn(Dist.CLIENT)
    public static int getEquipDelay() {
        return MAIN_HAND_STATUS.equipDelay;
    }
    @OnlyIn(Dist.CLIENT)
    public static void equipDelayCoolDown() {
        MAIN_HAND_STATUS.equipDelay = Math.max(0, MAIN_HAND_STATUS.equipDelay - 1);
    }
    @OnlyIn(Dist.CLIENT)
    public static void setEquipDelay(int delay) {
        MAIN_HAND_STATUS.equipDelay = delay;
    }
    @OnlyIn(Dist.CLIENT)
    public static boolean isInAds() {
        return MAIN_HAND_STATUS.ads;
    }
    @OnlyIn(Dist.CLIENT)
    public static float getAdsProgress() {
        return MAIN_HAND_STATUS.adsProgress;
    }
    @OnlyIn(Dist.CLIENT)
    public static boolean shouldHideFPRender = false;
    @OnlyIn(Dist.CLIENT)
    public static boolean displayGunInfoDetails = false;
    @OnlyIn(Dist.CLIENT)
    public static String getEffectiveSightUUID() {
        return MAIN_HAND_STATUS.attachmentsStatus.getEffectiveSightUUID();
    }
    @OnlyIn(Dist.CLIENT)
    public static float weaponAdsZMinDistance = Float.NaN;
    @OnlyIn(Dist.CLIENT)
    public static float fovModify = Float.NaN;
    @OnlyIn(Dist.CLIENT)
    public static float gunModelFovModify = Float.NaN;
    @OnlyIn(Dist.CLIENT)
    public static boolean isInSprintingTransAdjust = false;
    @OnlyIn(Dist.CLIENT)
    public static RenderLevelStageEvent.Stage currentStage;
    @OnlyIn(Dist.CLIENT)
    public static long localTimeOffset = 0;
    @OnlyIn(Dist.CLIENT)
    public static boolean DO_SEND_FIRE_PACKET = true;


    @OnlyIn(Dist.CLIENT)
    public static void onSetUp(final FMLClientSetupEvent event) {
        CLIENT_WEAPON_LOOPER_TIMER.scheduleAtFixedRate(new ClientWeaponLooper(), 0, 5L);

        //gun models register
        ArsenalLib.registerGunModel(ModItems.G19.get(), PistolModels.G19_MODEL, new DisplayData()
                .setFirstPersonMain(-0.65f,3.1f,-7.3f, POS).set(DisplayData.FIRST_PERSON_MAIN, 0.245f, SCALE)
                .setThirdPersonRight(0f, 0f, 0.5f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 1.2f, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-2, 0f, 0f, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,1.30f,-6.5f, POS)
                .setAttachmentScreen(1.6f, -0.75f, -9.9f, 0f, 90f, 0, 0.205f, 0.205f, 0.205f)
                .setInertialRecoilData(
                        new InertialRecoilData(0,0,0.3f,
                                0.1f,0.5f,0.08f,
                                1f, 0.5f,
                                0.5f, 0.5f, 0.5f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 3.65f, -21.2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(1.5f))
                .setBulletShellDisplayData(new BulletShellDisplayData(1.1f, 4.6f, -6.5f, new Vector3f(3.8f, 2f, 0.5f), BulletShellModel.PISTOL))
                .usePistolDefaultSprintingTrans()
        );

        ArsenalLib.registerGunModel(ModItems.PYTHON_357.get(), PistolModels.PYTHON_357_MODEL, new DisplayData()
                .setFirstPersonMain(-1.25f,2.125f,-6.725f, POS).set(DisplayData.FIRST_PERSON_MAIN, 0.25f, SCALE)
                .setThirdPersonRight(0f, -0.4f, 0.1f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, -0.2f, 1.2f, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-1.9f, -0.8f, 0f, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,1.35f,-5.625f, POS)
                .setAttachmentScreen(1f, -0.85f, -9.9f, 0f, 90f, 0, 0.205f, 0.205f, 0.205f)
                .setInertialRecoilData(
                        new InertialRecoilData(0,0,0.8f,
                                0.075f,0.7f,0.08f,
                                1f, 0.6f,
                                0.4f, 0.4f, 0.4f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 2.55f, -26.475f).setScale(1.5f))
                .usePistolDefaultSprintingTrans()
        );

        ArsenalLib.registerGunModel(ModItems.AKM.get(), RifleModels.AKM_MODEL, new DisplayData()
                .setFirstPersonMain(-13.5f,16.25f,-29.05f, POS).set(DisplayData.FIRST_PERSON_MAIN, 1f, SCALE)
                .setThirdPersonRight(0.0f,-0.2f,1.3f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,12.1f,-17f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0f, 0f, 0.56f,
                                0.0535f, 0.52f,  0.055f,
                                0.4f, 0.6f,
                                0.3f, 0.7f, 0.35f)
                                .shake(0.025f, 0.5f, 0.1f,
                                        1.75f, 0.11f, 0.5f, 0.6f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 3.3f, -96f).setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AK_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2f, 5.7f, -19f, new Vector3f(3.2f, 1.5f, 0.5f), BulletShellModel.RIFLE).setRandomRate(0.35f))
                .setSprintingTrans(14, 9.5f, 3, 23, -52, 44)
        );

        ArsenalLib.registerGunModel(ModItems.M4A1.get(), RifleModels.M4A1_MODEL, new DisplayData()
                .setFirstPersonMain(-12.5f,15.1f,-31.1f, POS)
                .setThirdPersonRight(0.0f,-0.7f,0.7f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,14.65f,-20f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0f, 0f, 0.45f,
                                0.06f, 0.4f, 0.06f,
                                0.3f, 0.7f, 0.35f, 0.7f,
                                0.2f, 0.7f, 0.25f)
                                .shake(0.022f, 0.4f, 0.11f,
                                        1.9f, 0.11f, 0.5f, 0.6f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 3.725f, -88f).setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3.3f).setLength(25))
                .setBulletShellDisplayData(new BulletShellDisplayData(2.2f, 4f, -12f, new Vector3f(3.5f, 1.6f, -0.5f), BulletShellModel.RIFLE).setRandomRate(0.4f))
                .setSprintingTrans(10.5f, 8.5f, 2.5f, 28, -45, 36)
        );

        ArsenalLib.registerGunModel(ModItems.AWP.get(), SniperModels.AWP_MODEL, new DisplayData()
                .setFirstPersonMain(-10.9f,12.7f,-31.3f, POS)
                .setThirdPersonRight(0.0f,-0.7f,-1.4f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,12.2f,-22f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.AK_COMPENSATOR, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4.9f, -127.8f).setScale(4f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2f, 5f, -10f, new Vector3f(1.5f, 0.2f, 0.3f), BulletShellModel.RIFLE).setScale(1.3f))
                .setSprintingTrans(9, 20.5f, 2, 16, -51, 27)
        );

        ArsenalLib.registerGunModel(ModItems.M870.get(), ShotGunModels.M870_MODEL, new DisplayData()
                .setFirstPersonMain(-10.5f,14.5f,-22.9f, POS)
                .setThirdPersonRight(0.0f,-0.2f,-0.4f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,10.1155f,-16f, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(-0.06f, 0.03f, 0.5f,
                                0.05f, 0.3f,  0.04f,
                                0.7f, 0.8f,
                                0.6f, 0.5f, 0.3f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4.575f, -110.1f).setScale(3f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2.3f, 4.5f, -15f, new Vector3f(2f, 0.2f, 0.25f), BulletShellModel.SHOTGUN))
                .setSprintingTrans(11, 10.5f, 2, 28, -45, 36)
        );

        ArsenalLib.registerGunModel(ModItems.M249.get(), CommonMGModels.M249_Model, new DisplayData()
                .setFirstPersonMain(-9.2f,17.6f,-24.8f, POS)
                .setThirdPersonRight(0.0f,-0.5f,-0.8f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, -1.4f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,13.4f,-20.5f, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(-0.05f, 0.1f, 0.4f,
                                0.06f, 0.2f,  0.075f,
                                0.35f, 0.7f, 0.35f, 0.7f,
                                0.1f, 0.75f, 0.25f)
                                .shake(0.015f, 0.2f, 0.12f,
                                        1.7f, 0.11f, 0.5f, 0.6f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 5.7f, -119f).setScale(2.1f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3.3f).setLength(25))
                .setBulletShellDisplayData(new BulletShellDisplayData(3.2f, 0.4f, -24.5f, new Vector3f(1.35f, 0.2f, 0.1f), BulletShellModel.RIFLE).setScale(1.1f))
                .setSprintingTrans(10.5f, 17, -4.5f, 13, -52, 30)
        );

        ArsenalLib.registerGunModel(ModItems.VECTOR_45.get(), new Vector45Model(), new DisplayData()
                .setFirstPersonMain(-11.3f,19.5f,-30.2f, POS)
                .setThirdPersonRight(0.0f,0.9f,0.5f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, -1.4f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,15.35f,-20f, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0f, 0f, 0.4f,
                                0.055f, 0.3f,  0.065f,
                                0.4f, 0.75f, 0.3f, 0.6f,
                                0.55f, 0.6f, 0.15f)
                                .shake(0.025f, 0.5f, 0.12f,
                                        1.9f, 0.11f, 0.5f, 0.4f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 2.8f, -57.6f).setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(2.3f).setLength(25))
                .setBulletShellDisplayData(new BulletShellDisplayData(2f, 7f, -23f, new Vector3f(3f, 1.7f, -0.6f), BulletShellModel.PISTOL).setScale(1.2f))
                .setSprintingTrans(12, 9.5f, -1, 19, -45, 36)
        );

        ArsenalLib.registerGunModel(ModItems.XM1014.get(), ShotGunModels.XM1014_MODEL, new DisplayData()
                .setFirstPersonMain(-11.4f,11.9f,-26.2f, POS)
                .setThirdPersonRight(0.0f,-0.4f,-0.3f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,8.4f,-17, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0, 0, 0.4f,
                                0.06f, 0.5f,  0.07f,
                                0.35f, 1f,
                                0.2f, 0.6f, 0.2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4f, -101.5f).setScale(3f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .setBulletShellDisplayData(new BulletShellDisplayData(3f, 4f, -17.5f, new Vector3f(2.8f, 0.8f, -0.25f), BulletShellModel.SHOTGUN))
                .setSprintingTrans(11.5f, 10.5f, 2, 28, -45, 36)
        );

        ArsenalLib.registerGunModel(ModItems.MK47.get(), new Mk47Model(), new DisplayData()
                .setFirstPersonMain(-12.5f,14.7f,-31.1f, POS)
                .setThirdPersonRight(0.0f,-0.7f,0.6f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,12.325f,-23.5f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0, 0, 0.45f,
                                0.05f, 0.53f,  0.06f,
                                0.5f, 0.45f,
                                0.4f, 0.6f, 0.25f)
                                .shake(0.0255f, 0.5f, 0.12f,
                                        1.8f, 0.1f, 0.6f, 0.6f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 3.2f, -87.2f).setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AK_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2.3f, 4f, -11.7f, new Vector3f(3.5f, 1.2f, 0.8f), BulletShellModel.RIFLE).setRandomRate(0.35f))
                .setSprintingTrans(11, 12.5f, -2, 18, -49, 41)
        );

        ArsenalLib.registerGunModel(ModItems.HK_G28.get(), RifleModels.HK_G28_MODEL, new DisplayData()
                .setFirstPersonMain(-12.65f,14.7f,-29.1f, POS)
                .setThirdPersonRight(0.0f,-0.7f,0.6f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,13.3f,-23f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0, 0, 0.5f,
                                0.04f, 0.3f,  0.045f,
                                0.5f, 0.25f,
                                0.4f, 0.6f, 0.3f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 3.6824f, -115.9f).setScale(3.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(3.2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(4.5f))
                .setBulletShellDisplayData(new BulletShellDisplayData(1.6f, 4f, -17f, new Vector3f(3.5f, 1.2f, 0.8f), BulletShellModel.RIFLE).setRandomRate(0.35f).setScale(1.2f))
                .setSprintingTrans(14, 13f, 2, 15, -49, 44)
        );

        ArsenalLib.registerGunModel(ModItems.AK12.get(), RifleModels.AK12_MODEL, new DisplayData()
                .setFirstPersonMain(-14.9f,14.9f,-35.5f, POS)
                .setThirdPersonRight(0.0f,-0.7f,0.7f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,11.2526f,-23.4f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0, 0, 0.48f,
                                0.055f, 0.5f, 0.06f,
                                0.3f, 0.7f, 0.3f, 0.5f,
                                0.5f, 0.6f, 0.25f)
                                .shake(0.021f, 0.55f, 0.11f,
                                        1.75f, 0.11f, 0.5f, 0.6f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.AK_COMPENSATOR, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 1.4561f, -103f).setScale(2.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2.1f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2.2f, 4.5f, -18f, new Vector3f(3.5f, 1.6f, -0.5f), BulletShellModel.RIFLE).setRandomRate(0.4f))
                .setSprintingTrans(16.5f, 10.5f, 4.5f, 27, -51, 45)
        );

        ArsenalLib.registerGunModel(ModItems.BERETTA_686.get(), new Beretta686Model(), new DisplayData()
                .setFirstPersonMain(-11f,13.1f,-33.3f, POS)
                .setThirdPersonRight(0.0f,-0.2f,-0.4f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,7.6296f,-32f, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0, 0.0f, 1f,
                                0.05f, 0.3f,  0.03f,
                                1.5f, 0.2f,
                                0.2f, 0.2f, 0.1f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4.0296f, -137.6134f).setScale(3f))
                .setSprintingTrans(16, 11.5f, 7, 28, -46, 47)
        );

        ArsenalLib.registerGunModel(ModItems.ANNIHILATOR.get(), CommonSMGModels.ANNIHILATOR_MODEL, new DisplayData()
                .setFirstPersonMain(-12.7f,14.5f,-53.5f, POS)
                .setThirdPersonRight(0.0f,-0.2f,-0.4f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,6.6127f,-44.5f, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 2.8f, -61.5f).setScale(2.25f))
                .setSprintingTrans(28, 15.5f, 11, 25, -52, 39)
                .setBulletShellDisplayData(new BulletShellDisplayData(2f, 4.5127f, -8.9f, new Vector3f(3.5f, 1.6f, -0.5f), BulletShellModel.PISTOL).setScale(1.2f).setRandomRate(0.4f))
        );

        ArsenalLib.registerGunModel(ModItems.MP5.get(), CommonSMGModels.MP5_MODEL, new DisplayData()
                .setFirstPersonMain(-11.7f,18.7f,-38f, POS)
                .setThirdPersonRight(0.0f,-0.8f,0.4f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, -1.4f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,14.6f,-24.2f, POS)
                .setAttachmentScreen(1f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0, 0, 0.45f,
                                0.06f, 0.45f,  0.0675f,
                                0.4f, 0.7f, 0.42f                                                                                                                                                       , 0.7f,
                                0.5f, 0.7f, 0.15f)
                                .shake(0.023f, 0.6f, 0.15f,
                                        1.7f, 0.1f, 0.5f, 0.5f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 5.4f, -57.8f).setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(2.3f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2f, 8f, -10f, new Vector3f(3f, 1.7f, -0.6f), BulletShellModel.PISTOL).setScale(1f))
                .setSprintingTrans(18.1f, 11.5f, 0, 20, -53, 45)
        );

        ArsenalLib.registerGunModel(ModItems.FN_BALLISTA.get(), SniperModels.FN_BALLISTA_MODEL, new DisplayData()
                .setFirstPersonMain(-11.8f,13.7f,-32.4f, POS)
                .setThirdPersonRight(0.0f,-0.7f,-1.4f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,12.95f,-22f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.AK_COMPENSATOR, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4f, -139.4f).setScale(6f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(5f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2f, 4.7f, -9.4f, new Vector3f(1.5f, 0.2f, 0.3f), BulletShellModel.RIFLE).setScale(1.5f))
                .setSprintingTrans(9, 20.5f, 2, 16, -51, 27)
        );

        ArsenalLib.registerGunModel(ModItems.M60E4.get(), CommonMGModels.M60E4, new DisplayData()
                .setFirstPersonMain(-12f,15.5f,-27.5f, POS).set(DisplayData.FIRST_PERSON_MAIN, 1f, SCALE)
                .setThirdPersonRight(0.0f,-0.1f,1.2f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, -1.4f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,14.7f,-21f, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0, 0, 0.52f,
                                0.048f, 0.5f,  0.04f,
                                0.4f,  0.3f,
                                0.1f, 0.7f, 0.3f)
                                .shake(0.026f, 0.4f, 0.12f,
                                        1.6f, 0.1f, 0.6f, 0.5f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 2.4f, -106.6f).setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(3.5f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2f, 2.4f, -11.6f, new Vector3f(1.35f, 0.2f, 0.1f), BulletShellModel.RIFLE).setScale(1.25f))
                .setSprintingTrans(8.5f,15.5f,0, -11.5f, -60.5f, 6.5f)
        );

        ArsenalLib.registerGunModel(ModItems.FN57.get(), PistolModels.FN57_MODEL, new DisplayData()
                .setFirstPersonMain(-1.65f,2.9f,-6.8f, POS).set(DisplayData.FIRST_PERSON_MAIN, 0.245f, SCALE)
                .setThirdPersonRight(0f, 0f, 0.5f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 1.2f, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-2, 0f, 0f, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,1.586375f,-6f, POS)
                .setAttachmentScreen(1.6f, -0.75f, -9.9f, 0f, 90f, 0, 0.205f, 0.205f, 0.205f)
                .setInertialRecoilData(
                        new InertialRecoilData(0,0,0.4f,
                                0.15f,0.6f,0.1f,
                                1f, 0.5f,
                                0.5f, 0.5f, 0.5f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4.4f, -21.525f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(1.5f))
                .setBulletShellDisplayData(new BulletShellDisplayData(1.4f, 5.1f, -8.025f, new Vector3f(3.8f, 2f, 0.5f), BulletShellModel.RIFLE).setScale(0.5f))
                .usePistolDefaultSprintingTrans()
        );

        ArsenalLib.registerGunModel(ModItems.MCX_SPEAR.get(), RifleModels.MCX_SPEAR_MODEL, new DisplayData()
                .setFirstPersonMain(-12.7f,16.2f,-34.2f, POS)
                .setThirdPersonRight(0.0f,-1.2f,0.7f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,14.55f,-28.5f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0f, 0f, 0.6f,
                                0.055f, 0.525f, 0.058f,
                                0.48f, 0.6f, 0.48f, 0.6f,
                                0.2f, 0.7f, 0.2f)
                                .shake(0.023f, 0.5f, 0.12f,
                                        1.75f, 0.1f, 0.5f, 0.35f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 1.6f, -85f).setScale(2.2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 1.6f, -113.4f).setScale(2.4f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3.6f))
                .setBulletShellDisplayData(new BulletShellDisplayData(4f, 2.6f, -11.5f, new Vector3f(3.5f, 1.3f, -0.6f), BulletShellModel.RIFLE).setRandomRate(0.4f).setScale(1.2f))
                .setSprintingTrans(15f, 12f, 5f, 22.5f, -49, 38.5f)
        );

        ArsenalLib.registerGunModel(ModItems.AUG_A3.get(), RifleModels.AUG_A3_MODEL, new DisplayData()
                .setFirstPersonMain(-10.5f,16.4f,-21.5f, POS)
                .setThirdPersonRight(0.0f,0f,2.4f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, -0.6f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,14.6f,-14.2f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0f, 0f, 0.485f,
                                0.06f, 0.42f, 0.06f,
                                0.36f,  0.37f,
                                0.2f, 0.7f, 0.45f)
                                .shake(0.0245f, 0.5f, 0.12f,
                                        1.8f, 0.1f, 0.5f, 0.5f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 5.6f, -72.6f).setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2.4f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3.6f))
                .setBulletShellDisplayData(new BulletShellDisplayData(3f, 6.6f, 15f, new Vector3f(3f, 1.6f, -1.5f), BulletShellModel.RIFLE).setRandomRate(0.4f))
                .setSprintingTrans(9.9f,7.5f,4.0f, 22.5f,-49f,38.5f)
        );

        ArsenalLib.registerGunModel(ModItems.SAIGA_12K.get(), ShotGunModels.SAIGA_12K_MODEL, new DisplayData()
                .setFirstPersonMain(-12f,15.2f,-30.5f, POS).set(DisplayData.FIRST_PERSON_MAIN, 1f, SCALE)
                .setThirdPersonRight(0.0f,-0.2f,1.3f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,10.375f,-22f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0f, 0f, 0.8f,
                                0.053f, 0.7f,  0.054f,
                                0.75f, 0.6f,
                                0.3f, 0.7f, 0.35f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 3f, -94.9f).setScale(2.5f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2.5f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AK_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3.5f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2f, 6f, -15.9f, new Vector3f(2.6f, 1f, 0.5f), BulletShellModel.SHOTGUN).setRandomRate(0.35f))
                .setSprintingTrans(14, 9.5f, 3, 23, -52, 44)
        );

        ArsenalLib.registerGunModel(ModItems.SKS.get(), RifleModels.SKS_MODEL, new DisplayData()
                .setFirstPersonMain(-13f,15.3f,-25.5f, POS).set(DisplayData.FIRST_PERSON_MAIN, 1f, SCALE)
                .setThirdPersonRight(0.0f,-0.2f,1.3f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, -0.9f, 4, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-6.6f, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setAds(0,10.7f,-15.5f, POS)
                .setAttachmentScreen(6f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0f, 0f, 0.85f,
                                0.065f, 0.6f,  0.06f,
                                0.5f, 0.5f,
                                0.3f, 0.7f, 0.35f)
                                .shake(0.03f, 0.55f, 0.15f,
                                        1.85f, 0.12f, 0.5f, 0.5f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4f, -123.9f).setScale(2.5f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2.5f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AK_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3.5f))
                .setBulletShellDisplayData(new BulletShellDisplayData(1f, 7f, -26.5f, new Vector3f(2.5f, 2f, -0.6f), BulletShellModel.RIFLE).setRandomRate(0.35f))
                .setSprintingTrans(14, 9.5f, 3, 23, -52, 44)
        );

        //attachment models register
        ArsenalLib.registerAttachmentModel(ModItems.PISTOL_SUPPRESSOR.get(), new PistolSuppressorModel());
        ArsenalLib.registerAttachmentModel(ModItems.AK_SUPPRESSOR.get(), new AKSuppressorModel());
        ArsenalLib.registerAttachmentModel(ModItems.AR_SUPPRESSOR.get(), new ARSuppressorModel());
        ArsenalLib.registerAttachmentModel(ModItems.SNIPER_SUPPRESSOR.get(), new SniperSuppressorModel());
        ArsenalLib.registerAttachmentModel(ModItems.SHOTGUN_SUPPRESSOR.get(), new ShotGunSuppressorModel());
        ArsenalLib.registerAttachmentModel(ModItems.OSPREY_SUPPRESSOR.get(), new OspreySuppressorModel());
        ArsenalLib.registerAttachmentModel(ModItems.AK_COMPENSATOR.get(), new AKCompensatorModel());
        ArsenalLib.registerAttachmentModel(ModItems.AK_IMPROVED_HANDGUARD.get(), new AKImprovedHandguardModel());
        ArsenalLib.registerAttachmentModel(ModItems.AK_RAIL_BRACKET.get(), new AKRailBracketModel());
        ArsenalLib.registerAttachmentModel(ModItems.AK_IMPROVED_DUST_COVER.get(), new AKImprovedDustCoverModel());
        ArsenalLib.registerAttachmentModel(ModItems.MICRO_RED_DOT.get(), new MicroRedDotModel());
        ArsenalLib.registerAttachmentModel(ModItems.RED_DOT.get(), new RedDotModel());
        ArsenalLib.registerAttachmentModel(ModItems.HOLOGRAPHIC.get(), new HolographicModel());
        ArsenalLib.registerAttachmentModel(ModItems.SCOPE_X10.get(), new ScopeX10Model());
        ArsenalLib.registerAttachmentModel(ModItems.VERTICAL_GRIP.get(), new VerticalGripModel());
        ArsenalLib.registerAttachmentModel(ModItems.GP_25.get(), new GP_25Model());
        ArsenalLib.registerAttachmentModel(ModItems.ACOG.get(), new AcogModel());
        ArsenalLib.registerAttachmentModel(ModItems.AR_STOCK_TUBE.get(), new ARStockTubeModel());
        ArsenalLib.registerAttachmentModel(ModItems.AR_GAS_BLOCK.get(), new ARGasBlockModel());
        ArsenalLib.registerAttachmentModel(ModItems.AR_RAILED_HANDGUARD.get(), new ARRailedHandguardModel());
        ArsenalLib.registerAttachmentModel(ModItems.AR_EXTEND_MAG.get(), new ARExtendMagModel());
        ArsenalLib.registerAttachmentModel(ModItems.AK_EXTEND_MAG.get(), new AKExtendMagModel());
        ArsenalLib.registerAttachmentModel(ModItems.GLOCK_EXTEND_MAG.get(), new GlockExtendMagModel());
        ArsenalLib.registerAttachmentModel(ModItems.SHOTGUN_EXTEND_BAY.get(), new ShotgunExtendBayModel());
        ArsenalLib.registerAttachmentModel(ModItems.SNIPER_EXTEND_MAG.get(), IAttachmentModel.EMPTY);
        ArsenalLib.registerAttachmentModel(ModItems.CTR_STOCK.get(), new CTRStockModel());
        ArsenalLib.registerAttachmentModel(ModItems.VECTOR_45_EXTEND_MAG.get(), IAttachmentModel.EMPTY);
        ArsenalLib.registerAttachmentModel(ModItems.AR_COMPENSATOR.get(), new ARCompensatorModel());
        ArsenalLib.registerAttachmentModel(ModItems.SMG_COMPENSATOR.get(), new SMGCompensatorModel());
        ArsenalLib.registerAttachmentModel(ModItems.MICRO_LASER_SIGHT.get(), new MicroLaserSightModel());
        ArsenalLib.registerAttachmentModel(ModItems.LASER_SIGHT.get(), new LaserSightModel());
        ArsenalLib.registerAttachmentModel(ModItems.HORIZONTAL_LASER_SIGHT.get(), new HorizontalLaserModel());
        ArsenalLib.registerAttachmentModel(ModItems.RAIL_PANEL.get(), new RailPanelModel());
        ArsenalLib.registerAttachmentModel(ModItems.RAL_PANEL_SHORT.get(), new RailPanelShortModel());
        ArsenalLib.registerAttachmentModel(ModItems.M249_RAILED_HANDGUARD.get(), IAttachmentModel.EMPTY);
        ArsenalLib.registerAttachmentModel(ModItems.MICRO_FLASHLIGHT.get(), new MicroFlashlightModel());
        ArsenalLib.registerAttachmentModel(ModItems.FLASHLIGHT.get(), new FlashlightModel());
        ArsenalLib.registerAttachmentModel(ModItems.OKP_7_A.get(), new Okp7AModel());
        ArsenalLib.registerAttachmentModel(ModItems.OKP_7_B.get(), new Okp7BModel());
        ArsenalLib.registerAttachmentModel(ModItems.EXP_MAG5_45X39.get(), new ExpMag5_45x39Model());
        ArsenalLib.registerAttachmentModel(ModItems.EXP_MAG7_62X51.get(), new ExpMag7_62x51Model());
        ArsenalLib.registerAttachmentModel(ModItems.SLANT_GRIP.get(), new SlantGripModel());
        ArsenalLib.registerAttachmentModel(ModItems.DMR_COMPENSATOR.get(), new DMRCompensatorModel());
        ArsenalLib.registerAttachmentModel(ModItems.MP5_RAIL_HANDGUARD.get(), IAttachmentModel.EMPTY);
        ArsenalLib.registerAttachmentModel(ModItems.RAIL_CLAMP.get(), new RailClampModel());
        ArsenalLib.registerAttachmentModel(ModItems.AR_LIGHT_HANDGUARD_SHORT.get(), new ARLightHandguardShortModel());
        ArsenalLib.registerAttachmentModel(ModItems.AR_LIGHT_HANDGUARD.get(), new ARLightHandguardModel());
        ArsenalLib.registerAttachmentModel(ModItems.EXP_MAG_45_STRAIGHT.get(),
                new StatisticMagModel(StatisticModel.MAG_COLLECTION3, "_45_straight", "_45_straight_bullet"));
        ArsenalLib.registerAttachmentModel(ModItems.EXP_MAG9X19.get(),
                new StatisticMagModel(StatisticModel.MAG_COLLECTION3, "9mm_arc", "9mm_arc_bullet"));
        ArsenalLib.registerAttachmentModel(ModItems.DRUM_9X19_ARC.get(),
                new StatisticMagModel(StatisticModel.MAG_COLLECTION3, "9mm_arc_drum", "9mm_arc_drum_bullet"));
        ArsenalLib.registerAttachmentModel(ModItems.DRUM_AK.get(),
                new StatisticMagModel(StatisticModel.MAG_COLLECTION3, "ak_drum", "ak_drum_bullet"));
        ArsenalLib.registerAttachmentModel(ModItems.MAG_SURE_FIRE_60.get(),
                new StatisticMagModel(StatisticModel.MAG_COLLECTION3, "SF_60", "SF_60_bullet"));
        ArsenalLib.registerAttachmentModel(ModItems.DRUM_45_STRAIGHT.get(),
                new StatisticMagModel(StatisticModel.MAG_COLLECTION3, "_45_straight_drum", "_45_straight_drum_bullet"));
        ArsenalLib.registerAttachmentModel(ModItems.DRUM_7_62X51.get(),
                new StatisticMagModel(StatisticModel.MAG_COLLECTION3, "dmr_308_drum", "dmr_308_drum_bullet"));
        ArsenalLib.registerAttachmentModel(ModItems.DRUM_5_45X39.get(),
                new StatisticMagModel(StatisticModel.MAG_COLLECTION3, "5_45x39_drum", "bullet"));
        ArsenalLib.registerAttachmentModel(ModItems.SMG_SUPPRESSOR.get(), new SMGSuppressorModel());
        ArsenalLib.registerAttachmentModel(ModItems.OSPREY_SMG_SUPPRESSOR.get(), new OspreySMGSuppressorModel());
        ArsenalLib.registerAttachmentModel(ModItems.AK12_SUPPRESSOR.get(), new AK12SuppressorModel());
        ArsenalLib.registerAttachmentModel(ModItems.UBR_STOCK.get(), new UBRStockModel());
        ArsenalLib.registerAttachmentModel(ModItems.AK_TACTICAL_STOCK.get(), new AKTacticalStockModel());
        ArsenalLib.registerAttachmentModel(ModItems.M203.get(), new M203Model());
        ArsenalLib.registerAttachmentModel(ModItems.ELCAN.get(), new ElcanModel());
        ArsenalLib.registerAttachmentModel(ModItems.GLOCK_MOUNT.get(), new GlockMountModel());
        ArsenalLib.registerAttachmentModel(ModItems.MCX_SPEAR_EXP_MAG.get(), IAttachmentModel.EMPTY);
        ArsenalLib.registerAttachmentModel(ModItems.SAIGA_12K_DRUM.get(), IAttachmentModel.EMPTY);
        ArsenalLib.registerAttachmentModel(ModItems.SAIGA_12K_EXP_MAG.get(), IAttachmentModel.EMPTY);
        ArsenalLib.registerAttachmentModel(ModItems.SAIGA_12K_TACTICAL_HANDGUARD.get(), IAttachmentModel.EMPTY);
        ArsenalLib.registerAttachmentModel(ModItems.KOBRA_SIGHT.get(), new KobraSightModel());

        //TEST!!!
//        List<Gun> allInstances = Gun.getAllInstances();
//        for (Gun gun : allInstances) {
//            RecoilData.register(gun, new RecoilData("1", "0.5", "0.25", "0.25", "0.1"));
//        }
        AddonHandler.INSTANCE.handleRegisterClient();
    }

    public static void handleClientSound(float originalVol, float volModify, float pitch, float x, float y, float z, String soundName) {
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundName));
        if (soundEvent != null) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                float dis = (float) Math.sqrt(Math.pow(player.position().x - x, 2) +
                        Math.pow(player.position().y - y, 2) +
                        Math.pow(player.position().z - z, 2));

                float range = soundEvent.getRange(originalVol);
                float prevVol = calculateSmoothVolume(dis, range);

                if (prevVol > 0) {
                    float[] playerDir = {(float) player.getLookAngle().x, (float) player.getLookAngle().y, (float) player.getLookAngle().z};
                    float[] soundDir = {(float) (x - player.position().x), (float) (y - player.position().y), (float) (z - player.position().z)};
                    float playerDirMagnitude = (float) Math.sqrt(playerDir[0] * playerDir[0] + playerDir[1] * playerDir[1] + playerDir[2] * playerDir[2]);
                    float soundDirMagnitude = (float) Math.sqrt(soundDir[0] * soundDir[0] + soundDir[1] * soundDir[1] + soundDir[2] * soundDir[2]);

                    float dotProduct = (playerDir[0] * soundDir[0] + playerDir[1] * soundDir[1] + playerDir[2] * soundDir[2]) /
                            (playerDirMagnitude * soundDirMagnitude);
                    float angleFactor = (float) (1.0 - Math.acos(dotProduct) / Math.PI) * 0.5f + 0.5f;
                    float adjustedVol = prevVol * volModify * angleFactor;
                    ModSounds.sound(adjustedVol, pitch, player, soundEvent);
                }
            }
        }
    }

    private static long lastHeadShotSound = 0;
    public static void handleClientShotFeedBack(boolean isHeadshot) {
        if (isHeadshot && System.currentTimeMillis() - lastHeadShotSound > 300) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                try {
                    SoundEvent soundEvent = ModSounds.HEADSHOT_SOUNDS[RenderAndMathUtils.getRandomIndex(ModSounds.HEADSHOT_SOUNDS.length)].get();
                    ModSounds.sound(1,1, player, soundEvent);
                    lastHeadShotSound = System.currentTimeMillis();
                } catch (Exception e) {e.printStackTrace();}
            }
        }
        RenderEvents.callHeadShotFeedBack(isHeadshot);
    }

    public static float calculateSmoothVolume(float distance, float range) {
        // 如果超出范围则音量为 0
        if (distance >= range) {
            return 0;
        }
        // 平滑衰减，采用距离的平方根作为衰减因子
        return (float) (1 - Math.pow(distance / range, 2));
    }

    public static void updateClientPlayerStatus(int id, long lastShoot, long lastChamber, long localTimeOffset,
                                                int latency, long balance, boolean reloading) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel != null) {
            Entity entity = clientLevel.getEntity(id);
            if (entity instanceof Player player) {
                player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((cap) -> {
                    cap.setLastShoot(lastShoot);
                    cap.setReloading(reloading);
                    cap.setLastChamberAction(lastChamber);
                    cap.setLocalTimeOffset(localTimeOffset);
                    cap.setLatency(latency);
                    cap.setBalance(balance);
                    cap.dataChanged = false;
                });
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static int getLocalLatency() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            return PlayerStatusProvider.getStatus(player).getLatency();
        }
        return 0;
    }

    public static void updateGunModifyScreenGuiContext(ListTag attachmentsTag) {
        if (Minecraft.getInstance().screen instanceof GunModifyScreen attachmentsScreen) {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem().getItem() instanceof IGun gun) {
                attachmentsScreen.updateGuiContext(attachmentsTag, gun);
            }
        }
    }

    public static void updateTransactionTerminalScreenData(List<Integer> playerIds) {
        if (Minecraft.getInstance().screen instanceof TransactionTerminalScreen terminalScreen) {
            terminalScreen.updateClientDataFromServer(playerIds);
        }
    }

    public static void updateTransferBalance(Long balance) {
        if (Minecraft.getInstance().screen instanceof TransactionTerminalScreen terminalScreen) {
            terminalScreen.updateBalance(balance);
        }
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer != null) {
            PlayerStatusProvider.getStatus(clientPlayer).setBalance(balance);
        }
    }

    public static void updateVendingMachineScreen(long balance) {
        if (Minecraft.getInstance().screen instanceof VendingMachineScreen vendingMachineScreen) {
            vendingMachineScreen.handleUpdate(balance);
        }
    }

    public static void updateAmmunitionModifyScreen(String modsUUID, int maxModCapability, CompoundTag modsTag, long balance) {
        if (Minecraft.getInstance().screen instanceof AmmunitionModifyScreen ammunitionModifyScreen) {
            ammunitionModifyScreen.updateClient(modsUUID, maxModCapability, modsTag, balance);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean allowFireBtnDown(ItemStack stack, IGun gun, Player player) {
        boolean allow = MAIN_HAND_STATUS.equipProgress == 0 && !player.isSwimming();
        if (allow && ReloadingHandler.INSTANCE.reloading()) {
            allow = gun.allowShootWhileReloading();
        }
        return allow;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean allowAdsStart(ItemStack stack, IGun gun, Player player) {
        return !player.isSwimming() && !ReloadingHandler.isReloading();
    }

    @OnlyIn(Dist.CLIENT)
    public static int handleClientShoot(ItemStack stack, IGun gun, Player player) {
        try {
            LOCK.lock();
            IGunFireMode fireMode = gun.getFireMode(stack);
            if (fireMode != null && fireMode.canFire(player, stack, gun)) {
                int delay = MAIN_HAND_STATUS.fireDelay.get();
                try {
                    fireMode.clientShoot(player, stack, gun);
                } catch (Exception ignored) {}
                return delay;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static float getSpread(IGun gun, Player player, ItemStack stack) {
        float spread = MAIN_HAND_STATUS.spread;
        int fireDelay = MAIN_HAND_STATUS.fireDelay.get();
        int fireFactor = fireDelay * 10 * (gun.isPistol() ? 4 : 8);
        int fireCount = MAIN_HAND_STATUS.fireCount;
        if (fireCount == 0 && !gun.isFreeBlot() && (System.currentTimeMillis() - MAIN_HAND_STATUS.lastShoot) > fireFactor) {
            spread *= 0.25f;
        }
        if (isInAds() && MAIN_HAND_STATUS.adsProgress >= 0.75f) {
            spread *= 1 -  Math.pow(MAIN_HAND_STATUS.adsProgress, 3) * 0.75f;
            if (gun.isSniper()) {
                spread *= 0.2f;
            }
        }
        if (MAIN_HAND_STATUS.adsProgress < 0.75f && gun instanceof Sniper) {
            spread = Mth.lerp(MAIN_HAND_STATUS.adsProgress / 0.75f, MAIN_HAND_STATUS.maxSpread, spread);
        }
        if (player.isCrouching()) {
            spread *= 0.9f;
        }
        return spread;
    }

    public static void onProjectileHitBlock(BlockPos pos, Vector3f hitVec, int directionIndex, int[] modsIndexList) {
        Direction direction = getDirection(directionIndex);
        Player player = Minecraft.getInstance().player;
        Vector3f normalVec = getNormalVec(direction);
        testPlayParticle(pos, hitVec, normalVec, player);
        for (int i : modsIndexList) {
            IAmmunitionMod mod = AmmunitionModRegister.getByIndex(i);
            if (mod != null) {
                mod.onHitBlockClient(pos, hitVec, direction, normalVec, player);
            }
        }
    }

    public static void testPlayParticle(BlockPos pos, Vector3f vec3, Vector3f normalVec, Player player) {
        if (player != null) {
            Level level = player.level();
            BlockState state = level.getBlockState(pos);
            for (int i = 0; i < 8; i++) {
                Vector3f dir = normalVec.add(
                        (float) (Math.random() * 0.5f - 0.25f),
                        (float) (Math.random() * 0.5f - 0.25f),
                        (float) (Math.random() * 0.5f - 0.25f));
                level.addParticle(new BlockParticleOption(
                        ParticleTypes.BLOCK, state.getBlock().defaultBlockState()),
                        vec3.x + (Math.random() * 0.2 - 0.1),
                        vec3.y + (Math.random() * 0.2 - 0.1),
                        vec3.z + (Math.random() * 0.2 - 0.1),
                        dir.x, dir.y, dir.z);
            }
            float pit = (float) (1 + Math.random() * 0.2 - 0.1);
            if (state.getSoundType() == SoundType.METAL) {
                level.playSound(player, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.9f, pit);
            } else {
                level.playSound(player, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.9f, pit);
            }
        }
    }

    public static Direction getDirection(int index) {
        return switch (index) {
            case 1 -> Direction.DOWN;
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.NORTH;
            case 4 -> Direction.WEST;
            case 5 -> Direction.EAST;
            default -> Direction.UP;
        };
    }

    private static Vector3f getNormalVec(Direction direction) {
        switch (direction) {
            case UP -> {
                return new Vector3f(0, 1, 0);
            }
            case DOWN -> {
                return new Vector3f(0, -1, 0);
            }
            case SOUTH -> {
                return new Vector3f(0, 0, 1);
            }
            case NORTH -> {
                return new Vector3f(0, 0, -1);
            }
            case WEST -> {
                return new Vector3f(-1, 0, 0);
            }
            case EAST -> {
                return new Vector3f(1, 0, 0);
            }
        }
        return new Vector3f(0,0,0);
    }
}
