package sheridan.gcaa;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.ListTag;
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
import org.joml.Vector3f;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.ClientWeaponLooper;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.model.attachments.akStuff.AKImprovedDustCoverModel;
import sheridan.gcaa.client.model.attachments.akStuff.AKRailBracketModel;
import sheridan.gcaa.client.model.attachments.handguard.AKImprovedHandguardModel;
import sheridan.gcaa.client.model.attachments.muzzle.AKCompensatorModel;
import sheridan.gcaa.client.model.attachments.muzzle.AKSuppressorModel;
import sheridan.gcaa.client.model.attachments.muzzle.PistolSuppressorModel;
import sheridan.gcaa.client.model.guns.AkmModel;
import sheridan.gcaa.client.model.guns.G19Model;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.fx.muzzleFlash.CommonMuzzleFlashes;
import sheridan.gcaa.client.render.fx.muzzleFlash.MuzzleFlashDisplayData;
import sheridan.gcaa.client.screens.AttachmentsScreen;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;
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
    public static boolean shouldHideFPRender = false;
    @OnlyIn(Dist.CLIENT)
    public static boolean displayGunInfoDetails = false;


    @OnlyIn(Dist.CLIENT)
    public static void onSetUp(final FMLClientSetupEvent event) {
        clientWeaponLooperTimer.scheduleAtFixedRate(new ClientWeaponLooper(), 0, 5L);

        //gun models register
        ArsenalLib.registerGunModel(ModItems.G19.get(), new G19Model(), new DisplayData()
                .setFirstPersonMain(-4.6f,12f,-30.5f, POS)
                .setThirdPersonRight(0f, 0f, 0.5f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 1.2f, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-2, 0f, 0f, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-2.1f, 0f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.5f, SCALE)
                .setAds(0,5.6f,-22.5f, POS)
                .setAttachmentScreen(1.6f, -0.75f, -9.9f, 0f, 90f, 0, 0.205f, 0.205f, 0.205f)
                .setInertialRecoilData(new InertialRecoilData(0.2f,0.05f,0.4f,0.07f,2.8f,0.1f,0.5f, 0.5f, new Vector3f(0.5f, 0.5f, 0.5f)))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setTranslate(0f, 3.65f, -21.2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setTranslate(0f, 3.65f, -41.2f).setScale(1.5f))
        );

        ArsenalLib.registerGunModel(ModItems.AKM.get(), new AkmModel(), new DisplayData()
                .setFirstPersonMain(-5.5f,18.1f,-17.3f, POS)
                .setThirdPersonRight(0.0f,-0.2f,1.3f, POS).set(DisplayData.THIRD_PERSON_RIGHT, 0.15f, SCALE)
                .setGround(0f, 0f, 3, POS).set(DisplayData.GROUND, 0.15f, SCALE)
                .setFrame(-4, 0f, 0, POS).setFrame(0f, -90, 0, ROT).set(DisplayData.FRAME, 0.3f, SCALE)
                .setGUI(-3.2f, 0.9f, 0, POS).setGUI(-25f, -45f, -35f, ROT).set(DisplayData.GUI, 0.20f, SCALE)
                .setAds(0,14,-11, POS)
                .setAttachmentScreen(4f,-0.3f,-22.1f, 0f, 90f, 0, 0.225f, 0.225f, 0.225f)
                .setInertialRecoilData(new InertialRecoilData(0.075f, 0.06f, 0.6f, 0.08f, 0.9f,  0.07f, 0.5f, 0.3f, new Vector3f(0.55f, 0.45f, 0.5f)))
                .addMuzzleFlash(Gun.MUZZLE_STATE_NORMAL, CommonMuzzleFlashes.COMMON, new MuzzleFlashDisplayData().setTranslate(0f, 4.9f, -99f).setScale(1.8f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_SUPPRESSOR, CommonMuzzleFlashes.SUPPRESSOR_COMMON, new MuzzleFlashDisplayData().setTranslate(0f, 3.65f, -139f).setScale(2f))
                .addMuzzleFlash(Gun.MUZZLE_STATE_COMPENSATOR, CommonMuzzleFlashes.AK_COMPENSATOR, new MuzzleFlashDisplayData().setTranslate(0f, 4.8f, -106f).setScale(3f))
        );

        //attachment models register
        ArsenalLib.registerAttachmentModel(ModItems.PISTOL_SUPPRESSOR.get(), new PistolSuppressorModel());
        ArsenalLib.registerAttachmentModel(ModItems.AK_SUPPRESSOR.get(), new AKSuppressorModel());
        ArsenalLib.registerAttachmentModel(ModItems.AK_COMPENSATOR.get(), new AKCompensatorModel());
        ArsenalLib.registerAttachmentModel(ModItems.AK_IMPROVED_HANDGUARD.get(), new AKImprovedHandguardModel());
        ArsenalLib.registerAttachmentModel(ModItems.AK_RAIL_BRACKET.get(), new AKRailBracketModel());
        ArsenalLib.registerAttachmentModel(ModItems.AK_IMPROVED_DUST_COVER.get(), new AKImprovedDustCoverModel());
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
                spread *= 0.3f;
            }
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
