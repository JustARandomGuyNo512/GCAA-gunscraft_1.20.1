package sheridan.gcaa.items.guns;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.client.animation.recoilAnimation.RecoilAnimationHandler;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.animation.recoilAnimation.RecoilCameraHandler;
import sheridan.gcaa.client.model.registry.GunModelRegistry;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.items.BaseItem;
import sheridan.gcaa.items.GunProperties;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.GunFirePacket;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;
import java.util.Map;

public class Gun extends BaseItem implements IGun {
    public static final String MUZZLE_STATE_NORMAL = "normal";
    public static final String MUZZLE_STATE_SUPPRESSED = "suppressed";
    public static final String MUZZLE_STATE_COMPENSATOR = "compensator";
    private final GunProperties gunProperties;

    public Gun(GunProperties gunProperties) {
        super(new Properties().stacksTo(1));
        this.gunProperties = gunProperties;
    }

    @Override
    public GunProperties getGunProperties() {
        return gunProperties;
    }

    @Override
    public Gun getGun() {
        return this;
    }

    @Override
    public int getAmmoLeft(ItemStack stack) {
        return 1;
        // return checkAndGet(stack).getInt("ammo_left");
    }

    protected float randomIndex() {
        return Math.random() <= 0.5 ? 1 : -1;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientShoot(ItemStack stack, Player player, IGunFireMode fireMode) {
        Clients.mainHandStatus.lastShoot = System.currentTimeMillis();
        PlayerStatusProvider.setLastShoot(player, System.currentTimeMillis());
        PacketHandler.simpleChannel.sendToServer(new GunFirePacket());
        DisplayData data = GunModelRegistry.getDisplayData(this);
        float directionY = randomIndex();
        if (data != null) {
            InertialRecoilData inertialRecoilData = data.getInertialRecoilData();
            if (inertialRecoilData != null) {
                float directionX = randomIndex();
                RecoilAnimationHandler.INSTANCE.onShoot(inertialRecoilData, directionX, directionY);
            }
        }
        RecoilCameraHandler.INSTANCE.onShoot(this, stack, directionY);
        handleClientFireSound(stack, player);
    }

    @OnlyIn(Dist.CLIENT)
    protected void handleClientFireSound(ItemStack stack, Player player) {
        String muzzleState = getMuzzleFlash(stack);
        if (MUZZLE_STATE_SUPPRESSED.equals(muzzleState)) {
            if (gunProperties.suppressedSound != null) {
                ModSounds.clientSound(1f, 1f + ((float) Math.random() * 0.1f), player, gunProperties.suppressedSound.get());
            } else {
                if (gunProperties.fireSound != null) {
                    ModSounds.clientSound(0.5f, 1.6f + ((float) Math.random() * 0.1f), player, gunProperties.fireSound.get());
                }
            }
        } else {
            if (gunProperties.fireSound != null) {
                ModSounds.clientSound(1f, 1f + ((float) Math.random() * 0.1f), player, gunProperties.fireSound.get());
            }
        }
    }

    @Override
    public void shoot(ItemStack stack, Player player, IGunFireMode fireMode) {
        int ammoLeft = getAmmoLeft(stack);
        if (ammoLeft > 0) {
            ICaliber caliber = gunProperties.caliber;
            caliber.fireBullet(null, null, this, player, stack);
        }
    }

    @Override
    public IGunFireMode getFireMode(ItemStack stack) {
        int index = checkAndGet(stack).getInt("fire_mode_index");
        List<IGunFireMode> fireModes = gunProperties.fireModes;
        return fireModes.get(index % fireModes.size());
    }

    @Override
    public int getBurstCount() {
        return 0;
    }

    @Override
    public ICaliber getCaliber() {
        return gunProperties.caliber;
    }

    @Override
    public CompoundTag getPropertiesTag(ItemStack stack) {
        return checkAndGet(stack).contains("properties") ? checkAndGet(stack).getCompound("properties") : new CompoundTag();
    }

    @Override
    public ListTag getAttachmentsListTag(ItemStack stack) {
        return checkAndGet(stack).contains("attachments") ? checkAndGet(stack).getList("attachments", Tag.TAG_COMPOUND) : new ListTag();
    }

    @Override
    public boolean shouldUpdate(int version) {
        return version != GCAA.INNER_VERSION;
    }

    @Override
    public void setPropertiesTag(ItemStack stack, CompoundTag tag) {
        checkAndGet(stack).put("properties", tag);
    }

    @Override
    public void switchFireMode(ItemStack stack) {
        CompoundTag tag = checkAndGet(stack);
        int index = (tag.getInt("fire_mode_index") + 1) % gunProperties.fireModes.size();
        tag.putInt("fire_mode_index", index);
    }

    @Override
    public int getFireDelay(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("fire_delay") ? properties.getInt("fire_delay") : 0;
    }

    @Override
    public String getMuzzleFlash(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("muzzle_flash") ? properties.getString("muzzle_flash") : "normal";
    }

    @Override
    public boolean isSniper() {
        return false;
    }

    @Override
    public float getRecoilPitch(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("recoil_pitch") ? properties.getFloat("recoil_pitch") : 0;
    }

    @Override
    public float getRecoilYaw(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("recoil_yaw") ? properties.getFloat("recoil_yaw") : 0;
    }

    @Override
    public float getRecoilPitchControl(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("recoil_pitch_control") ? properties.getFloat("recoil_pitch_control") : 0;
    }

    @Override
    public float getRecoilYawControl(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("recoil_yaw_control") ? properties.getFloat("recoil_yaw_control") : 0;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    protected CompoundTag checkAndGet(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt == null) {
            this.onCraftedBy(stack, null, null);
            nbt = stack.getTag();
        }
        return nbt;
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        super.onCraftedBy(pStack, pLevel, pPlayer);
        CompoundTag nbt = pStack.getTag();
        if (nbt == null) {
            nbt = new CompoundTag();
        }
        if (!nbt.contains("inner_version")) {
            nbt.putInt("inner_version", genVersion());
        }
        if (!nbt.contains("fire_mode_index")) {
            nbt.putInt("fire_mode_index", 0);
        }
        if (!nbt.contains("ammo_left")) {
            nbt.putInt("ammo_left", this.gunProperties.magSize);
        }
        if (!nbt.contains("properties")) {
            nbt.put("properties", gunProperties.getInitialData());
        }
        if (!nbt.contains("attachments")) {
            nbt.put("attachments", new ListTag());
        }
        pStack.setTag(nbt);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull Object getRenderPropertiesInternal() {
        return ArmPoseHandler.ARM_POSE_HANDLER;
    }


    protected int genVersion() {
        return GCAA.INNER_VERSION;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }


    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
        return EMPTY_ENCHANTMENT_MAP;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isRepairable(@NotNull ItemStack stack) {
        return false;
    }
}

