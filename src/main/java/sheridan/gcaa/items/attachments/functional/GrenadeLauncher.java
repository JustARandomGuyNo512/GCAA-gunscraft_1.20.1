package sheridan.gcaa.items.attachments.functional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.GrenadeLauncherReloadTask;
import sheridan.gcaa.client.KeyBinds;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.client.animation.recoilAnimation.RecoilCameraHandler;
import sheridan.gcaa.client.model.attachments.functional.GP_25Model;
import sheridan.gcaa.entities.ModEntities;
import sheridan.gcaa.entities.projectiles.Grenade;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.attachments.IArmReplace;
import sheridan.gcaa.items.attachments.IInteractive;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.FireGrenadeLauncherPacket;
import sheridan.gcaa.sounds.ModSounds;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.Objects;

public class GrenadeLauncher extends Attachment implements IArmReplace, IInteractive {
    public static final String KEY_AMMO = "grenade_ammo";
    @OnlyIn(Dist.CLIENT)
    private Object recoilData;

    public static boolean hasGrenade(ItemStack stack, IGun gun) {
        CompoundTag tag = gun.getGun().checkAndGet(stack);
        return tag.contains(KEY_AMMO) && tag.getBoolean(KEY_AMMO);
    }

    public static void setHasGrenade(ItemStack stack, IGun gun, boolean hasGrenade) {
        CompoundTag tag = gun.getGun().checkAndGet(stack);
        tag.putBoolean(KEY_AMMO, hasGrenade);
    }

    public static void setLastFire(ItemStack stack, IGun gun, long lastFire) {
        setLastFire(gun.getPropertiesTag(stack), lastFire);
    }

    public static void setLastFire(CompoundTag data, long lastFire) {
        data.putLong("last_fire_grenade", lastFire);
    }

    public static long getLastFire(ItemStack stack, IGun gun) {
        CompoundTag tag = gun.getPropertiesTag(stack);
        return tag.contains("last_fire_grenade") ? tag.getLong("last_fire_grenade") : 0;
    }

    public static void reload(ItemStack stack, IGun gun, Player player) {
        setHasGrenade(stack, gun, true);
    }

    public static void shoot(ItemStack stack, IGun gun, Player player, long lastFire) {
        if (hasGrenade(stack, gun)) {
            setLastFire(stack, gun, lastFire);
            setHasGrenade(stack, gun, false);
            if (!player.level().isClientSide) {
                Grenade grenade = new Grenade(ModEntities.GRENADE.get(), player.level());
                grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F, 3f);
                player.level().addFreshEntity(grenade);
            }
        }
    }

    @Override
    public boolean replaceArmRender(boolean mainHand) {
        return !mainHand;
    }

    @Override
    public int orderForArmRender(boolean mainHand) {
        return mainHand ? 0 : 2;
    }

    @Override
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {
        if (!hasGrenade(stack, gun)) {
            setHasGrenade(stack, gun, false);
        }
        setLastFire(data, 0L);
//        GunProperties properties = gun.getGunProperties();
//        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate + 0.05f);
//        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate + 0.12f);
    }

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {
        if (hasGrenade(stack, gun)) {
            //TODO: 出了弹药系统记得把榴弹还给玩家
        }
        CompoundTag tag = gun.getGun().checkAndGet(stack);
        tag.remove(KEY_AMMO);
//        GunProperties properties = gun.getGunProperties();
//        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate - 0.05f);
//        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate - 0.12f);
    }

    @Override
    public float getPitchRecoilControlIncRate() {
        return 0.075f;
    }

    @Override
    public float getYawRecoilControlIncRate() {
        return 0.045f;
    }

    @OnlyIn(Dist.CLIENT)
    protected void handleClientShoot(ItemStack stack, IGun gun, Player player) {
        if (recoilData == null) {
            recoilData = new InertialRecoilData(
                    0, 0, 0.9f, 0.08f, 1f,  0.06f, 0.3f, 0);
        }
        AnimationHandler.INSTANCE.pushRecoil(
                (InertialRecoilData) recoilData, RenderAndMathUtils.randomIndex(), RenderAndMathUtils.randomIndex(), 1, 1);
        RecoilCameraHandler.INSTANCE.onShoot(
                4f, (float) ((Math.random() - 0.5) * 2.5f),
                gun.getRecoilPitchControl(stack) * 0.2f, gun.getRecoilYawControl(stack) * 0.2f);
        ModSounds.sound(1, 1f, player, ModSounds.GP_25_FIRE.get());
        long now = System.currentTimeMillis();
        setLastFire(stack, gun, now);
        PacketHandler.simpleChannel.sendToServer(new FireGrenadeLauncherPacket(now));
        setHasGrenade(stack, gun, false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onKeyPress(int key, int action, ItemStack stack, IGun gun, Player player) {
        if (KeyBinds.USE_GRENADE_LAUNCHER.isDown() && action == 1) {
            if (hasGrenade(stack, gun)) {
                if (!ReloadingHandler.INSTANCE.reloading()) {
                    handleClientShoot(stack, gun, player);
                }
            } else {
                if (!ReloadingHandler.INSTANCE.reloading()) {
                    if (Objects.equals(gun.getFireMode(stack).getName(), Auto.AUTO.getName())
                            && Clients.mainHandStatus.buttonDown.get()
                            && gun.getAmmoLeft(stack) > 0) {
                        return;
                    }
                    ReloadingHandler.INSTANCE.setTask(new GrenadeLauncherReloadTask(
                            RenderAndMathUtils.secondsToTicks(2.1f),
                            GP_25Model.INSTANCE.getGunReload(),
                            GP_25Model.INSTANCE.getAttachmentReload(),
                            gun, stack));
                    PlayerStatusProvider.setReloading(player, true);
                    Clients.mainHandStatus.buttonDown.set(false);
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onMouseButton(int btn, int action, ItemStack stack, IGun gun, Player player) {}
}
