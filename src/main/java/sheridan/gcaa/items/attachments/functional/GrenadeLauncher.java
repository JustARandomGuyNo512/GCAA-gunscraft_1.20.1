package sheridan.gcaa.items.attachments.functional;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.Clients;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.GrenadeLauncherReloadTask;
import sheridan.gcaa.client.KeyBinds;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.SprintingHandler;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.client.animation.recoilAnimation.RecoilCameraHandler;
import sheridan.gcaa.client.model.attachments.functional.GP_25Model;
import sheridan.gcaa.entities.ModEntities;
import sheridan.gcaa.entities.projectiles.Grenade;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.ammunition.AmmunitionHandler;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.attachments.*;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.FireGrenadeLauncherPacket;
import sheridan.gcaa.sounds.ModSounds;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.List;
import java.util.Objects;

public class GrenadeLauncher extends Attachment implements IArmReplace, IInteractive, ForwardSlotBlocker {
    public static final String KEY_AMMO = "grenade_ammo";
    private final InertialRecoilData recoilData;
    public final IAmmunition ammunition;
    private final float recoilPitch;
    private final float recoilYaw;
    private final RegistryObject<SoundEvent> fireSound;
    private final int reloadTicks;
    private final float velocity;
    private final float pInaccuracy;
    private final float explodeRadius;
    private final int safeTicks;

    public GrenadeLauncher(IAmmunition ammunition, InertialRecoilData recoilData, float recoilPitch, float recoilYaw,
                           RegistryObject<SoundEvent> fireSound, int reloadTicks, float velocity, float pInaccuracy,
                           float explodeRadius, int safeTicks, float weight)  {
        super(weight);
        this.ammunition = ammunition;
        this.recoilData = recoilData;
        this.recoilPitch = recoilPitch;
        this.recoilYaw = recoilYaw;
        this.fireSound = fireSound;
        this.reloadTicks = reloadTicks;
        this.velocity = velocity;
        this.pInaccuracy = pInaccuracy;
        this.explodeRadius = explodeRadius;
        this.safeTicks = safeTicks;
    }

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

    public static void reload(String attachmentId, ItemStack stack, IGun gun, Player player) {
        if (stack != player.getMainHandItem()) {
            return;
        }
        IAttachment attachment = AttachmentsRegister.get(attachmentId);
        if (attachment instanceof GrenadeLauncher grenadeLauncher) {
            IAmmunition ammunition = grenadeLauncher.ammunition;
            NonNullList<ItemStack> items = player.getInventory().items;
            for (int i = 0; i < items.size(); i ++) {
                ItemStack itemStack = items.get(i);
                if (itemStack.getItem() instanceof IAmmunition ammo && ammo == ammunition) {
                    int ammoLeft = ammo.getAmmoLeft(itemStack);
                    if (ammoLeft - 1 == 0) {
                        items.set(i, new ItemStack(Items.AIR));
                    } else {
                        ammo.setAmmoLeft(itemStack, ammoLeft - 1);
                    }
                    if (ammoLeft > 0) {
                        setHasGrenade(stack, gun, true);
                        return;
                    }
                }
            }
        }
    }

    public static void shoot(ItemStack stack, IGun gun, Player player, long lastFire, GrenadeLauncher launcher) {
        if (hasGrenade(stack, gun)) {
            setLastFire(stack, gun, lastFire);
            setHasGrenade(stack, gun, false);
            if (!player.level().isClientSide) {
                Grenade grenade = new Grenade(ModEntities.GRENADE.get(), player.level());
                grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, launcher.velocity, launcher.pInaccuracy, launcher.explodeRadius, launcher.safeTicks);
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
    public void onAttach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        if (!hasGrenade(stack, gun)) {
            setHasGrenade(stack, gun, false);
        }
        setLastFire(data, 0L);
        super.onAttach(player, stack, gun, data);
    }

    @Override
    public void onDetach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        if (hasGrenade(stack, gun)) {
            AmmunitionHandler.andAmmunition(player, ammunition, 1);
        }
        CompoundTag tag = gun.getGun().checkAndGet(stack);
        tag.remove(KEY_AMMO);
        super.onDetach(player, stack, gun, data);
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
    protected void handleClientShoot(ItemStack stack, IGun gun, Player player, GrenadeLauncher launcher) {
        AnimationHandler.INSTANCE.pushRecoil(recoilData, RenderAndMathUtils.randomIndex(), RenderAndMathUtils.randomIndex(), 1, 1);
        RecoilCameraHandler.INSTANCE.onShoot(
                recoilPitch, (float) ((Math.random() - 0.5) * recoilYaw),
                gun.getRecoilPitchControl(stack) * 0.2f, gun.getRecoilYawControl(stack) * 0.2f);
        ModSounds.sound(1, 1f, player, fireSound.get());
        long now = System.currentTimeMillis();
        setLastFire(stack, gun, now);
        PacketHandler.simpleChannel.sendToServer(new FireGrenadeLauncherPacket(now, Item.getId(launcher)));
        setHasGrenade(stack, gun, false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onKeyPress(int key, int action, ItemStack stack, IGun gun, Player player) {
        if (KeyBinds.USE_GRENADE_LAUNCHER.isDown() && action == 1) {
            if (SprintingHandler.INSTANCE.getSprintingProgress() != 0) {
                SprintingHandler.INSTANCE.exitSprinting(RenderAndMathUtils.secondsToTicks(1.25f));
                return;
            }
            if (hasGrenade(stack, gun)) {
                if (!ReloadingHandler.INSTANCE.reloading()) {
                    handleClientShoot(stack, gun, player, this);
                }
            } else {
                if (!ReloadingHandler.INSTANCE.reloading()) {
                    if (Objects.equals(gun.getFireMode(stack).getName(), Auto.AUTO.getName())
                            && Clients.MAIN_HAND_STATUS.buttonDown.get()
                            && gun.getAmmoLeft(stack) > 0) {
                        return;
                    }
                    if (!AmmunitionHandler.hasAmmunitionItem(ammunition, player)) {
                        String str = Component.translatable("tooltip.screen_info.no_ammo").getString();
                        String ammunitionName = Component.translatable(ammunition.get().getDescriptionId()).getString();
                        Minecraft.getInstance().gui.setOverlayMessage(Component.literal(str.replace("@ammo", ammunitionName)), false);
                        return;
                    }

                    ReloadingHandler.INSTANCE.setTask(new GrenadeLauncherReloadTask(
                            AttachmentsRegister.getStrKey(this),
                            reloadTicks,
                            gun, stack));
                    PlayerStatusProvider.setReloading(player, true);
                    Clients.MAIN_HAND_STATUS.buttonDown.set(false);
                }
            }
        }
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void onMouseButton(int btn, int action, ItemStack stack, IGun gun, Player player) {}

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        String ammo = Component.translatable("tooltip.gun_info.ammunition").getString();
        String name = Component.translatable(ammunition.get().getDescriptionId()).getString();
        String string = Component.translatable("tooltip.gcaa.use_grenade_launcher").getString();
        string = string.replace("$key", KeyBinds.USE_GRENADE_LAUNCHER.getTranslatedKeyMessage().getString());
        pTooltipComponents.add(Component.literal(ammo + name));
        pTooltipComponents.add(Component.literal(string));
    }
}
