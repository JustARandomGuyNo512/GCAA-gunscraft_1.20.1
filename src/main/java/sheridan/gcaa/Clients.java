package sheridan.gcaa;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.ClientWeaponLooper;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.model.BulletShellModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.akStuff.AKImprovedDustCoverModel;
import sheridan.gcaa.client.model.attachments.akStuff.AKRailBracketModel;
import sheridan.gcaa.client.model.attachments.akStuff.AKTacticalDustCoverModel;
import sheridan.gcaa.client.model.attachments.arStuff.ARGasBlockModel;
import sheridan.gcaa.client.model.attachments.arStuff.ARStockTubeModel;
import sheridan.gcaa.client.model.attachments.functional.GP_25Model;
import sheridan.gcaa.client.model.attachments.grip.VerticalGripModel;
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
import sheridan.gcaa.client.model.guns.*;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.fx.bulletShell.BulletShellDisplayData;
import sheridan.gcaa.client.render.fx.muzzleFlash.CommonMuzzleFlashes;
import sheridan.gcaa.client.render.fx.muzzleFlash.MuzzleFlashDisplayData;
import sheridan.gcaa.client.screens.AttachmentsScreen;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;
import sheridan.gcaa.lib.ArsenalLib;
import sheridan.gcaa.sounds.ModSounds;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static sheridan.gcaa.client.render.DisplayData.DataType.POS;
import static sheridan.gcaa.client.render.DisplayData.DataType.ROT;
import static sheridan.gcaa.client.render.DisplayData.DataType.SCALE;

public class Clients {
    @OnlyIn(Dist.CLIENT)
    public static ClientWeaponStatus mainHandStatus = new ClientWeaponStatus(true);
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
    public static long lastShootMain() {
        return mainHandStatus.lastShoot;
    }
    @OnlyIn(Dist.CLIENT)
    public static volatile long lastClientTick;
    @OnlyIn(Dist.CLIENT)
    public static boolean handleWeaponBobbing = true;
    @OnlyIn(Dist.CLIENT)
    public static boolean debugKeyDown = false;
    @OnlyIn(Dist.CLIENT)
    public static int getEquipDelay() {
        return mainHandStatus.equipDelay;
    }
    @OnlyIn(Dist.CLIENT)
    public static void equipDelayCoolDown() {
        mainHandStatus.equipDelay = Math.max(0, mainHandStatus.equipDelay-1);
    }
    @OnlyIn(Dist.CLIENT)
    public static void setEquipDelay(int delay) {
        mainHandStatus.equipDelay = delay;
    }
    @OnlyIn(Dist.CLIENT)
    public static boolean isInAds() {
        return mainHandStatus.ads;
    }
    @OnlyIn(Dist.CLIENT)
    public static float getAdsProgress() {
        return mainHandStatus.adsProgress;
    }
    @OnlyIn(Dist.CLIENT)
    public static boolean shouldHideFPRender = false;
    @OnlyIn(Dist.CLIENT)
    public static boolean displayGunInfoDetails = false;
    @OnlyIn(Dist.CLIENT)
    public static String getEffectiveSightUUID() {
        return mainHandStatus.attachmentsStatus.getEffectiveSightUUID();
    }
    @OnlyIn(Dist.CLIENT)
    public static float weaponAdsZMinDistance = Float.NaN;
    @OnlyIn(Dist.CLIENT)
    public static float fovModify = Float.NaN;
    @OnlyIn(Dist.CLIENT)
    public static float gunModelFovModify = Float.NaN;


    @OnlyIn(Dist.CLIENT)
    public static void onSetUp(final FMLClientSetupEvent event) {
        clientWeaponLooperTimer.scheduleAtFixedRate(new ClientWeaponLooper(), 0, 5L);

        //gun models register
        ArsenalLib.registerGunModel(ModItems.G19.get(), new G19Model(), new DisplayData()
                .setFirstPersonMain(-1.15f,2.85f,-7.15f, POS).set(DisplayData.FIRST_PERSON_MAIN, 0.25f, SCALE)
                .setThirdPersonRight(0f, 0f, 0.5f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 1.2f, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-2, 0f, 0f, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-2.1f, 0f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.5f, SCALE)
                .setAds(0,1.45f,-5.625f, POS)
                .setAttachmentScreen(1.6f, -0.75f, -9.9f, 0f, 90f, 0, 0.205f, 0.205f, 0.205f)
                .setInertialRecoilData(new InertialRecoilData(0.3f,0.05f,0.35f,0.08f,1.8f,0.18f,0.5f, 0.5f, new Vector3f(0.5f, 0.5f, 0.5f)))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 3.65f, -21.2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(1.5f))
                .setBulletShellDisplayData(new BulletShellDisplayData(1.1f, 4.6f, -6.5f, new Vector3f(3.8f, 2f, 0.5f), BulletShellModel.PISTOL))
        );

        ArsenalLib.registerGunModel(ModItems.PYTHON_357.get(), new Python357Model(), new DisplayData()
                .setFirstPersonMain(-1.25f,2.125f,-6.625f, POS).set(DisplayData.FIRST_PERSON_MAIN, 0.25f, SCALE)
                .setThirdPersonRight(0f, -0.4f, 0.1f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, -0.2f, 1.2f, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-1.9f, -0.8f, 0f, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-2.9f, -0.8f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.5f, SCALE)
                .setAds(0,1.35f,-5.625f, POS)
                .setAttachmentScreen(1f, -0.85f, -9.9f, 0f, 90f, 0, 0.205f, 0.205f, 0.205f)
                .setInertialRecoilData(new InertialRecoilData(0.3f,0.05f,0.8f,0.075f,3f,0.1f,0.6f, 0.6f, new Vector3f(0.4f, 0.4f, 0.4f)))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 2.55f, -26.475f).setScale(1.5f))
        );

        ArsenalLib.registerGunModel(ModItems.AKM.get(), new AkmModel(), new DisplayData()
                .setFirstPersonMain(-5.5f,16.5f,-20.2f, POS)
                .setThirdPersonRight(0.0f,-0.2f,1.3f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,12.4f,-16.5f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(new InertialRecoilData(0.03f, 0.01f, 0.48f, 0.05f, 0.7f,  0.05f, 0.5f, 0.5f, new Vector3f(0.5f, 0.25f, 0.4f)))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4.9f, -99f).setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AK_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2.8f, 7.6f, -24.5f, new Vector3f(3.2f, 1.5f, 0.5f), BulletShellModel.RIFLE).setRandomRate(0.35f))
        );

        ArsenalLib.registerGunModel(ModItems.M4A1.get(), new M4a1Model(), new DisplayData()
                .setFirstPersonMain(-7.3f,15.8f,-27.5f, POS).set(DisplayData.FIRST_PERSON_MAIN, 1f, SCALE)
                .setThirdPersonRight(0.0f,-0.7f,0.7f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,14.65f,-20f, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(new InertialRecoilData(0.03f, 0.01f, 0.3f, 0.04f, 0.45f, 0.045f, 0.375f, 0.32f, new Vector3f(0.5f, 0.25f, 0.35f)))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 3.725f, -88f).setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3.3f).setLength(25))
                .setBulletShellDisplayData(new BulletShellDisplayData(2.2f, 4f, -12f, new Vector3f(3.5f, 1.6f, -0.5f), BulletShellModel.RIFLE).setRandomRate(0.4f))
        );

        ArsenalLib.registerGunModel(ModItems.AWP.get(), new AwpModel(), new DisplayData()
                .setFirstPersonMain(-6.675f,12.205196f,-28.533f, POS)
                .setThirdPersonRight(0.0f,-0.7f,-1.4f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,12.2f,-15, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(new InertialRecoilData(0f, 0f, 0.1f, 0.05f, 0.1f,  0.05f, 0.2f, 0.2f, new Vector3f(0.6f, 0.1f, 0.5f)))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.AK_COMPENSATOR, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4.9f, -127.8f).setScale(4f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2f, 5f, -10f, new Vector3f(1.5f, 0.2f, 0.3f), BulletShellModel.RIFLE).setScale(1.3f))
        );

        ArsenalLib.registerGunModel(ModItems.M870.get(), new M870Model(), new DisplayData()
                .setFirstPersonMain(-6.2f,12.85f,-24.4f, POS)
                .setThirdPersonRight(0.0f,-0.2f,-0.4f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,8.3f,-16, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(new InertialRecoilData(0.00f, 0.0f, 1.2f, 0.06f, 1.2f,  0.06f, 0.5f, 0.2f, new Vector3f(0.6f, 0.2f, 0.5f)))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 4.575f, -110.1f).setScale(3f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .setBulletShellDisplayData(new BulletShellDisplayData(2.3f, 4.5f, -15f, new Vector3f(2f, 0.2f, 0.25f), BulletShellModel.SHOTGUN))
        );

        ArsenalLib.registerGunModel(ModItems.M249.get(), new M249Model(), new DisplayData()
                .setFirstPersonMain(-7.7f,18.8f,-33f, POS)
                .setThirdPersonRight(0.0f,-0.5f,-0.8f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, -1.4f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,13.65f,-27.5f, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(new InertialRecoilData(0.01f, 0.005f, 0.42f, 0.068f, 0.6f,  0.05f, 0.35f, 0.35f, new Vector3f(0.6f, 0.45f, 0.3f)))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 5.7f, -113.1f).setScale(2.1f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(3.3f).setLength(25))
                .setBulletShellDisplayData(new BulletShellDisplayData(3.2f, 0.4f, -18.6f, new Vector3f(1.35f, 0.2f, 0.1f), BulletShellModel.RIFLE).setScale(1.1f))
        );

        ArsenalLib.registerGunModel(ModItems.VECTOR_45.get(), new Vector45Model(), new DisplayData()
                .setFirstPersonMain(-7f,19.2f,-30.5f, POS)
                .setThirdPersonRight(0.0f,0.9f,0.5f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, -1.4f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,15.35f,-19.1f, POS)
                .setAttachmentScreen(3.5f,-0.6f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(new InertialRecoilData(0f, 0f, 0.35f, 0.035f, 0.3f,  0.03f, 0.45f, 0.3f, new Vector3f(0.55f, 0.4f, 0.25f)))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setDefaultTranslate(0f, 2.8f, -57.6f).setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AR_COMPENSATOR, new MuzzleFlashDisplayData().setScale(2.3f).setLength(25))
                .setBulletShellDisplayData(new BulletShellDisplayData(2f, 7f, -23f, new Vector3f(3f, 1.7f, -0.6f), BulletShellModel.PISTOL).setScale(1.2f))
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

    public static float calculateVolume(float disSq, float rangeSq) {
        return disSq >= rangeSq ? 0 : (1 - disSq / rangeSq);
    }

    public static void updateClientPlayerStatus(int id, long lastShoot, long lastChamber, boolean reloading) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel != null) {
            Entity entity = clientLevel.getEntity(id);
            if (entity instanceof Player player) {
                player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((cap) -> {
                    cap.setLastShoot(lastShoot);
                    cap.setReloading(reloading);
                    cap.setLastChamberAction(lastChamber);
                    cap.dataChanged = false;
                });
            }
        }
    }

    public static void updateAttachmentScreenGuiContext(ListTag attachmentsTag) {
        if (Minecraft.getInstance().screen instanceof AttachmentsScreen attachmentsScreen) {
            Player player = Minecraft.getInstance().player;
            if (player.getMainHandItem().getItem() instanceof IGun gun) {
                attachmentsScreen.updateGuiContext(attachmentsTag, gun);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean allowFireBtnDown(ItemStack stack, IGun gun, Player player) {
        boolean allow = mainHandStatus.equipProgress == 0 && !player.isSwimming();
        if (allow && ReloadingHandler.INSTANCE.reloading()) {
            allow = gun.allowShootWhileReloading();
        }
        return allow;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean allowAdsStart(ItemStack stack, IGun gun, Player player) {
        return mainHandStatus.equipProgress == 0 && !player.isSwimming() && !ReloadingHandler.isReloading();
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

    @OnlyIn(Dist.CLIENT)
    public static float getSpread(IGun gun, Player player, ItemStack stack) {
        float spread = mainHandStatus.spread;
        int fireDelay = mainHandStatus.fireDelay.get();
        int fireFactor = fireDelay * 10 * (gun.isPistol() ? 4 : 8);
        int fireCount = mainHandStatus.fireCount;
        if (fireCount == 0 && !gun.isFreeBlot() && (System.currentTimeMillis() - mainHandStatus.lastShoot) > fireFactor) {
            spread *= 0.25f;
        }
        if (isInAds() && mainHandStatus.adsProgress >= 0.75f) {
            spread *= 0.135f;
            if (gun.isSniper()) {
                spread *= 0.25f;
            }
        }
        if (player.isCrouching()) {
            spread *= 0.9f;
        }
        return spread;
    }

    public static void testPlayParticle(BlockPos pos, Vec3 vec3, int directionIndex, int force) {
        if (force <= 0 || force >= 100) {
            return;
        }
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            Level level = player.level();
            BlockState state = level.getBlockState(pos);
            Direction direction = getDirection(directionIndex);
            Vec3 particleVelocity = getParticleVelocity(direction);
            for (int i = 0; i < force; i++) {
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state.getBlock().defaultBlockState()), vec3.x + (Math.random() * 0.2 - 0.1), vec3.y + (Math.random() * 0.2 - 0.1), vec3.z + (Math.random() * 0.2 - 0.1), particleVelocity.x, particleVelocity.y, particleVelocity.z);
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
            case 0 -> Direction.UP;
            case 1 -> Direction.DOWN;
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.NORTH;
            case 4 -> Direction.WEST;
            case 5 -> Direction.EAST;
            default -> null;
        };
    }

    private static Vec3 getParticleVelocity(Direction direction) {
        double seed1 = Math.random() * 0.5 - 0.25;
        double seed2 = Math.random() * 0.5 - 0.25;
        switch (direction) {
            case UP -> {
                return new Vec3(seed1, 1, seed2);
            }
            case DOWN -> {
                return new Vec3(seed1, -1, seed2);
            }
            case SOUTH -> {
                return new Vec3(seed1, seed2, 1);
            }
            case NORTH -> {
                return new Vec3(seed1, seed2, -1);
            }
            case WEST -> {
                return new Vec3(-1, seed1, seed2);
            }
            case EAST -> {
                return new Vec3(1, seed1, seed2);
            }
        }
        return Vec3.ZERO;
    }
}
