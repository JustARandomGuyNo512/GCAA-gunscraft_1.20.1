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
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.ClientWeaponLooper;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.events.RenderEvents;
import sheridan.gcaa.client.model.BulletShellModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.akStuff.AKImprovedDustCoverModel;
import sheridan.gcaa.client.model.attachments.akStuff.AKRailBracketModel;
import sheridan.gcaa.client.model.attachments.akStuff.AKTacticalDustCoverModel;
import sheridan.gcaa.client.model.attachments.arStuff.ARGasBlockModel;
import sheridan.gcaa.client.model.attachments.arStuff.ARStockTubeModel;
import sheridan.gcaa.client.model.attachments.functional.GP_25Model;
import sheridan.gcaa.client.model.attachments.grip.*;
import sheridan.gcaa.client.model.attachments.handguard.AKImprovedHandguardModel;
import sheridan.gcaa.client.model.attachments.handguard.ARRailedHandguardModel;
import sheridan.gcaa.client.model.attachments.mags.AKExtendMagModel;
import sheridan.gcaa.client.model.attachments.mags.ARExtendMagModel;
import sheridan.gcaa.client.model.attachments.mags.GlockExtendMagModel;
import sheridan.gcaa.client.model.attachments.mags.ShotgunExtendBayModel;
import sheridan.gcaa.client.model.attachments.muzzle.*;
import sheridan.gcaa.client.model.attachments.scope.AcogModel;
import sheridan.gcaa.client.model.attachments.scope.ScopeX10Model;
import sheridan.gcaa.client.model.attachments.sight.HolographicModel;
import sheridan.gcaa.client.model.attachments.sight.MicroRedDotModel;
import sheridan.gcaa.client.model.attachments.sight.RedDotModel;
import sheridan.gcaa.client.model.attachments.stocks.CTRStockModel;
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
    public static void onSetUp(final FMLClientSetupEvent event) {
        CLIENT_WEAPON_LOOPER_TIMER.scheduleAtFixedRate(new ClientWeaponLooper(), 0, 5L);

        //gun models register
        ArsenalLib.registerGunModel(ModItems.G19.get(), new G19Model(), new DisplayData()
                .setFirstPersonMain(-0.65f,3.115f,-7.5f, POS).set(DisplayData.FIRST_PERSON_MAIN, 0.25f, SCALE)
                .setThirdPersonRight(0f, 0f, 0.5f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 1.2f, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-2, 0f, 0f, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-2.1f, 0f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.5f, SCALE)
                .setAds(0,1.37f,-6.5f, POS)
                .setAttachmentScreen(1.6f, -0.75f, -9.9f, 0f, 90f, 0, 0.205f, 0.205f, 0.205f)
                .setInertialRecoilData(
                        new InertialRecoilData(0.3f,0.05f,0.3f,
                                0.08f,1.6f,0.15f,
                                0.4f, 0.5f, 0.5f,
                                0.5f, 0.5f, 0f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 3.65f, -21.2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(1.5f))
                .setBulletShellDisplayData(new BulletShellDisplayData(1.1f, 4.6f, -6.5f, new Vector3f(3.8f, 2f, 0.5f), BulletShellModel.PISTOL))
                .usePistolDefaultSprintingTrans()
        );

        ArsenalLib.registerGunModel(ModItems.PYTHON_357.get(), new Python357Model(), new DisplayData()
                .setFirstPersonMain(-1.25f,2.125f,-6.725f, POS).set(DisplayData.FIRST_PERSON_MAIN, 0.25f, SCALE)
                .setThirdPersonRight(0f, -0.4f, 0.1f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, -0.2f, 1.2f, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-1.9f, -0.8f, 0f, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-2.9f, -0.8f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.5f, SCALE)
                .setAds(0,1.35f,-5.625f, POS)
                .setAttachmentScreen(1f, -0.85f, -9.9f, 0f, 90f, 0, 0.205f, 0.205f, 0.205f)
                .setInertialRecoilData(
                        new InertialRecoilData(0.3f,0.05f,0.8f,
                                0.075f,3f,0.1f,
                                0.6f, 0.6f,0.4f,
                                0.4f, 0.4f, 0f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 2.55f, -26.475f).setScale(1.5f))
                .usePistolDefaultSprintingTrans()
        );

        ArsenalLib.registerGunModel(ModItems.AKM.get(), new AkmModel(), new DisplayData()
                .setFirstPersonMain(-12f,16.7f,-32.6f, POS).set(DisplayData.FIRST_PERSON_MAIN, 1f, SCALE)
                .setThirdPersonRight(0.0f,-0.2f,1.3f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,12.4f,-22f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0.03f, 0.01f, 0.6f,
                                0.05f, 0.55f,  0.04f,
                                0.45f, 0.55f, 0.3f,
                                0.6f, 0.2f, 0f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4.9f, -99f).setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AK_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2.8f, 7.6f, -24.5f, new Vector3f(3.2f, 1.5f, 0.5f), BulletShellModel.RIFLE).setRandomRate(0.35f))
                .setSprintingTrans(14, 9.5f, 3, 23, -52, 44)
        );

        ArsenalLib.registerGunModel(ModItems.M4A1.get(), new NewM4a1Model(), new DisplayData()
                .setFirstPersonMain(-11.3f,15.2f,-32.3f, POS)
                .setThirdPersonRight(0.0f,-0.7f,0.7f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,14.65f,-20f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0.03f, 0.01f, 0.3f,
                                0.04f, 0.45f, 0.045f,
                                0.375f, 0.32f, 0.5f,
                                0.5f, 0.2f, 0f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 3.725f, -88f).setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3.3f).setLength(25))
                .setBulletShellDisplayData(new BulletShellDisplayData(2.2f, 4f, -12f, new Vector3f(3.5f, 1.6f, -0.5f), BulletShellModel.RIFLE).setRandomRate(0.4f))
                .setSprintingTrans(10.5f, 8.5f, 2.5f, 28, -45, 36)
        );

        ArsenalLib.registerGunModel(ModItems.AWP.get(), new NewAwpModel(), new DisplayData()
                .setFirstPersonMain(-9.07f,13f,-33f, POS)
                .setThirdPersonRight(0.0f,-0.7f,-1.4f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,12.2f,-15f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0f, 0f, 0.1f,
                                0.05f, 0.12f,  0.05f,
                                0.2f, 0.2f, 0.6f,
                                0.5f, 0.4f, 0f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.AK_COMPENSATOR, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4.9f, -127.8f).setScale(4f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2f, 5f, -10f, new Vector3f(1.5f, 0.2f, 0.3f), BulletShellModel.RIFLE).setScale(1.3f))
                .setSprintingTrans(9, 20.5f, 2, 16, -51, 27)
        );

        ArsenalLib.registerGunModel(ModItems.M870.get(), new NewM870Model(), new DisplayData()
                .setFirstPersonMain(-9f,14.5f,-29.5f, POS)
                .setThirdPersonRight(0.0f,-0.2f,-0.4f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,8.3f,-19f, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0.00f, 0.0f, 1.2f,
                                0.06f, 1.2f,  0.06f,
                                0.5f, 0.2f, 0.6f,
                                0.5f, 0.3f, 0f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4.575f, -110.1f).setScale(3f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2.3f, 4.5f, -15f, new Vector3f(2f, 0.2f, 0.25f), BulletShellModel.SHOTGUN))
                .setSprintingTrans(11, 10.5f, 2, 28, -45, 36)
        );

        ArsenalLib.registerGunModel(ModItems.M249.get(), new NewM249Model(), new DisplayData()
                .setFirstPersonMain(-8.9f,18.6f,-35.2f, POS)
                .setThirdPersonRight(0.0f,-0.5f,-0.8f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, -1.4f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,13.65f,-28f, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0.01f, 0.005f, 0.4f,
                                0.06f, 0.5f,  0.06f,
                                0.3f, 0.35f,0.5f,
                                0.6f, 0.2f, 0f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 5.7f, -113.1f).setScale(2.1f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3.3f).setLength(25))
                .setBulletShellDisplayData(new BulletShellDisplayData(3.2f, 0.4f, -18.6f, new Vector3f(1.35f, 0.2f, 0.1f), BulletShellModel.RIFLE).setScale(1.1f))
                .setSprintingTrans(10.5f, 17, -4.5f, 13, -52, 30)
        );

        ArsenalLib.registerGunModel(ModItems.VECTOR_45.get(), new NewVector45Model(), new DisplayData()
                .setFirstPersonMain(-10.7f,18.8f,-32.8f, POS)
                .setThirdPersonRight(0.0f,0.9f,0.5f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, -1.4f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,15.35f,-19f, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0f, 0f, 0.35f,
                                0.035f, 0.3f,  0.03f,
                                0.45f, 0.3f,0.55f,
                                0.65f, 0.15f, 0f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 2.8f, -57.6f).setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(2.3f).setLength(25))
                .setBulletShellDisplayData(new BulletShellDisplayData(2f, 7f, -23f, new Vector3f(3f, 1.7f, -0.6f), BulletShellModel.PISTOL).setScale(1.2f))
                .setSprintingTrans(12, 9.5f, -1, 19, -45, 36)
        );

        ArsenalLib.registerGunModel(ModItems.XM1014.get(), new NewXm1014Model(), new DisplayData()
                .setFirstPersonMain(-10.4f,12.3f,-27.7f, POS)
                .setThirdPersonRight(0.0f,-0.4f,-0.3f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,8.4f,-17, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0.00f, 0.0f, 0.45f,
                                0.05f, 0.21f,  0.01f,
                                0.3f, 0.3f,0.2f,
                                0.7f, 0.15f, 0f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4f, -101.5f).setScale(3f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .setBulletShellDisplayData(new BulletShellDisplayData(3f, 4f, -17.5f, new Vector3f(2.8f, 0.8f, -0.25f), BulletShellModel.SHOTGUN))
                .setSprintingTrans(11.5f, 10.5f, 2, 28, -45, 36)
        );

        ArsenalLib.registerGunModel(ModItems.MK47.get(), new NewMk47Model(), new DisplayData()
                .setFirstPersonMain(-11f,15.2f,-32.8f, POS)
                .setThirdPersonRight(0.0f,-0.7f,0.6f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,12.325f,-23.5f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(
                        new InertialRecoilData(0.03f, 0.01f, 0.42f,
                                0.055f, 0.72f,  0.06f,
                                0.5f, 0.45f,0.4f,
                                0.6f, 0.2f, 0f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 3.2f, -87.2f).setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AK_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2.3f, 4f, -11.7f, new Vector3f(3.5f, 1.2f, 0.8f), BulletShellModel.RIFLE).setRandomRate(0.35f))
                .setSprintingTrans(11, 12.5f, -2, 18, -49, 41)
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
        ArsenalLib.registerAttachmentModel(ModItems.AK_TACTICAL_DUST_COVER.get(), new AKTacticalDustCoverModel());
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
    }

    public static void handleClientSound(float originalVol, float volModify, float pitch, float x, float y, float z, String soundName) {
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundName));
        if (soundEvent != null) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                float dis = (float) ((player.position().x - x) * (player.position().x - x) +
                        (player.position().y - y) * (player.position().y - y) +
                        (player.position().z - z) * (player.position().z - z));
                float range = soundEvent.getRange(originalVol);
                float prevVol = calculateVolume(dis, range * range);
                if (prevVol != 0) {
                    ModSounds.sound(prevVol * volModify, pitch, player, soundEvent);
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

    public static float calculateVolume(float disSq, float rangeSq) {
        return disSq >= rangeSq ? 0 : (1 - disSq / rangeSq);
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
            if (player.getMainHandItem().getItem() instanceof IGun gun) {
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
        return MAIN_HAND_STATUS.equipProgress == 0 && !player.isSwimming() && !ReloadingHandler.isReloading();
    }

    @OnlyIn(Dist.CLIENT)
    public static int handleClientShoot(ItemStack stack, IGun gun, Player player) {
        try {
            LOCK.lock();
            IGunFireMode fireMode = gun.getFireMode(stack);
            if (fireMode != null && fireMode.canFire(player, stack, gun)) {
                fireMode.clientShoot(player, stack, gun);
                return MAIN_HAND_STATUS.fireDelay.get();
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
