package sheridan.gcaa.items.gun;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.ReloadingTask;
import sheridan.gcaa.client.IReloadingTask;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.animation.recoilAnimation.RecoilCameraHandler;
import sheridan.gcaa.client.model.registry.GunModelRegistry;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.items.NoRepairNoEnchantmentItem;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.GunFirePacket;
import sheridan.gcaa.sounds.ModSounds;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.List;

public class Gun extends NoRepairNoEnchantmentItem implements IGun {
    public static final String MUZZLE_STATE_NORMAL = "normal";
    public static final String MUZZLE_STATE_SUPPRESSED = "suppressed";
    public static final String MUZZLE_STATE_COMPENSATOR = "compensator";
    private final GunProperties gunProperties;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public Gun(GunProperties gunProperties) {
        super(new Properties().stacksTo(1));
        this.gunProperties = gunProperties;
        defaultModifiers = ArrayListMultimap.create();
        defaultModifiers.put(Attributes.ATTACK_SPEED, new EditableAttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", 0, AttributeModifier.Operation.ADDITION));
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
        return checkAndGet(stack).getInt("ammo_left");
    }

    @Override
    public void setAmmoLeft(ItemStack stack, int ammoLeft) {
        checkAndGet(stack).putInt("ammo_left", ammoLeft);
    }

    @Override
    public int getMagSize(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("mag_size") ? properties.getInt("mag_size") : -1;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientShoot(ItemStack stack, Player player, IGunFireMode fireMode) {
        Clients.mainHandStatus.lastShoot = System.currentTimeMillis();
        PlayerStatusProvider.setLastShoot(player, System.currentTimeMillis());
        PacketHandler.simpleChannel.sendToServer(new GunFirePacket(Clients.getSpread(this, player, stack)));
        DisplayData data = GunModelRegistry.getDisplayData(this);
        float directionY = RenderAndMathUtils.randomIndex();
        if (data != null) {
            InertialRecoilData inertialRecoilData = data.getInertialRecoilData();
            if (inertialRecoilData != null) {
                float directionX = RenderAndMathUtils.randomIndex();
                Clients.mainHandStatus.lastRecoilDirection = directionX;
                AnimationHandler.INSTANCE.pushRecoil(inertialRecoilData, directionX, directionY);
            }
        }
        RecoilCameraHandler.INSTANCE.onShoot(this, stack, directionY);
        handleFireSound(stack, player);
        float spread = getShootSpread(stack);
        if (player.isCrouching()) {
            spread *= 0.8f;
        }
        if (Clients.mainHandStatus.ads && Clients.mainHandStatus.adsProgress > 0.7f) {
            spread *= 0.7f;
        }
        Clients.mainHandStatus.spread += spread;
    }

    protected void handleFireSound(ItemStack stack, Player player) {
        String muzzleState = getMuzzleFlash(stack);
        if (MUZZLE_STATE_SUPPRESSED.equals(muzzleState)) {
            if (gunProperties.suppressedSound != null) {
                ModSounds.sound(gunProperties.fireSoundVol, 1f + ((float) Math.random() * 0.1f), player, gunProperties.suppressedSound.get());
            } else {
                if (gunProperties.fireSound != null) {
                    ModSounds.sound(gunProperties.fireSoundVol * 0.4f, 1.6f + ((float) Math.random() * 0.1f), player, gunProperties.fireSound.get());
                }
            }
        } else {
            if (gunProperties.fireSound != null) {
                ModSounds.sound(gunProperties.fireSoundVol, 1f + ((float) Math.random() * 0.1f), player, gunProperties.fireSound.get());
            }
        }
    }

    @Override
    public void shoot(ItemStack stack, Player player, IGunFireMode fireMode, float spread) {
        int ammoLeft = getAmmoLeft(stack);
        if (ammoLeft > 0) {
            Caliber caliber = gunProperties.caliber;
            if (!player.level().isClientSide) {
                caliber.fireBullet(null, null, this, player, stack, spread);
                handleFireSound(stack, player);
            }
            setAmmoLeft(stack, ammoLeft - 1);
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
    public Caliber getCaliber() {
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
    public boolean isPistol() {
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
    public float getWalkingSpreadFactor(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("walking_spread_factor") ? properties.getFloat("walking_spread_factor") : 1.3f;
    }

    @Override
    public float getSprintingSpreadFactor(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("sprinting_spread_factor") ? properties.getFloat("sprinting_spread_factor") : 1.6f;
    }

    @Override
    public float getShootSpread(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("shoot_spread") ? properties.getFloat("shoot_spread") : 0;
    }

    @Override
    public float getSpreadRecover(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("spread_recover") ? properties.getFloat("spread_recover") : 0.1f;
    }

    @Override
    public float getWeight(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("weight") ? properties.getFloat("weight") : 0;
    }

    @Override
    public float getAdsSpeed(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("ads_speed") ? properties.getFloat("ads_speed") : 0;
    }

    @Override
    public float[] getSpread(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        float minSpread = properties.contains("min_spread") ? properties.getFloat("min_spread") : 0;
        float maxSpread = properties.contains("max_spread") ? properties.getFloat("max_spread") : 0;
        return new float[] {minSpread, maxSpread};
    }

    @Override
    public boolean clientReload(ItemStack stack, Player player) {
        boolean allow = getAmmoLeft(stack) < getMagSize(stack);
        if (allow) {
            PlayerStatusProvider.setReloading(player, true);
        }
        return allow;
    }

    @Override
    public void reload(ItemStack stack, Player player) {
        //TODO handle reloading features
        setAmmoLeft(stack, getMagSize(stack));
    }

    @Override
    public int getReloadLength(ItemStack stack, boolean fullReload) {
        CompoundTag properties = getPropertiesTag(stack);
        return fullReload ? properties.getInt("full_reload_length") : properties.getInt("reload_length");
    }

    @Override
    public IReloadingTask getReloadingTask(ItemStack stack) {
        return new ReloadingTask(stack, this);
    }

    @Override
    public int getInnerVersion(ItemStack stack) {
        return checkAndGet(stack).getInt("inner_version");
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


    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (slotChanged) {
            Clients.mainHandStatus.buttonDown.set(false);
            Clients.setEquipDelay(3);
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.resetAttackStrengthTicker();
            }
        }
        return Clients.getEquipDelay() > 0;
    }

    public float getEquipSpeedModifier(ItemStack itemStack, IGun gun) {
        float weight = Mth.clamp(gun.getWeight(itemStack), 5, 40);
        return ((weight - 5f) / (35f)) * (-3.5f);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        List<AttributeModifier> attributeModifiers = (List<AttributeModifier>) defaultModifiers.get(Attributes.ATTACK_SPEED);
        EditableAttributeModifier attributeModifier = (EditableAttributeModifier) attributeModifiers.get(0);
        attributeModifier.setAmount(getEquipSpeedModifier(stack, this));
        return this.defaultModifiers;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
        //tooltip.add(Component.literal("test"));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
        return ItemStack.TooltipPart.MODIFIERS.getMask();
    }
}

