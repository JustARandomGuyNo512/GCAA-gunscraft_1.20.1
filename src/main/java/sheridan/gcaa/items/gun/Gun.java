package sheridan.gcaa.items.gun;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
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
import sheridan.gcaa.Commons;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.client.*;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.animation.recoilAnimation.RecoilCameraHandler;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.fx.bulletShell.BulletShellRenderer;
import sheridan.gcaa.items.NoRepairNoEnchantmentItem;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.AmmunitionHandler;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.ammunition.IAmmunitionMod;
import sheridan.gcaa.items.attachments.IArmReplace;
import sheridan.gcaa.items.attachments.Scope;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.GunFirePacket;
import sheridan.gcaa.sounds.ModSounds;
import sheridan.gcaa.utils.FontUtils;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Gun extends NoRepairNoEnchantmentItem implements IGun {
    public static final String MUZZLE_STATE_NORMAL = "normal";
    public static final String MUZZLE_STATE_SUPPRESSOR = "suppressor";
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
        Clients.MAIN_HAND_STATUS.lastShoot = System.currentTimeMillis();
        PlayerStatusProvider.setLastShoot(player, System.currentTimeMillis());
        PacketHandler.simpleChannel.sendToServer(new GunFirePacket(Clients.getSpread(this, player, stack)));
        DisplayData data = GunModelRegister.getDisplayData(this);
        IArmReplace leftArmReplace = Clients.MAIN_HAND_STATUS.getLeftArmReplaceAttachment();
        IArmReplace rightArmReplace = Clients.MAIN_HAND_STATUS.getRightArmReplaceAttachment();
        CompoundTag tag = getPropertiesTag(stack);
        float directionY = RenderAndMathUtils.randomIndex();
        float pControl = gunProperties.getPropertyRate(GunProperties.RECOIL_PITCH_CONTROL, tag);
        float yControl = gunProperties.getPropertyRate(GunProperties.RECOIL_YAW_CONTROL, tag);
        float pControlIncRate = 0;
        float yControlIncRate = 0;
        if (leftArmReplace != null) {
            pControlIncRate += leftArmReplace.getPitchRecoilControlIncRate();
            yControlIncRate += leftArmReplace.getYawRecoilControlIncRate();
        }
        if (rightArmReplace != null) {
            pControlIncRate += rightArmReplace.getPitchRecoilControlIncRate();
            yControlIncRate += rightArmReplace.getYawRecoilControlIncRate();
        }
        pControl *= Mth.clamp(1 + pControlIncRate, 0, 99999);
        yControl *= Mth.clamp(1 + yControlIncRate, 0, 99999);
        if (data != null) {
            InertialRecoilData inertialRecoilData = data.getInertialRecoilData();
            if (inertialRecoilData != null) {
                float directionX = RenderAndMathUtils.randomIndex();
                Clients.MAIN_HAND_STATUS.lastRecoilDirection = directionX;
                float pRate = gunProperties.getPropertyRate(GunProperties.RECOIL_PITCH, tag);
                float yRate = gunProperties.getPropertyRate(GunProperties.RECOIL_YAW, tag);
                AnimationHandler.INSTANCE.pushRecoil(inertialRecoilData, directionX, directionY,
                        Mth.clamp((pRate - Math.max(0, pControl - 1) * 0.3f), 0.5f, 1f),
                        Mth.clamp((yRate - Math.max(0, yControl - 1) * 0.3f), 0.5f, 1f));
            }
        }
        RecoilCameraHandler.INSTANCE.onShoot(this, stack, directionY, player,
                Math.max(1, 1 + pControlIncRate),
                Math.max(1, 1 + yControlIncRate));
        handleFireSoundClient(stack, player);
        float spread = getShootSpread(stack);
        if (player.isCrouching()) {
            spread *= 0.8f;
        }
        if (Clients.MAIN_HAND_STATUS.ads && Clients.MAIN_HAND_STATUS.adsProgress > 0.7f) {
            spread *= 0.7f;
        }
        Clients.MAIN_HAND_STATUS.spread += spread;
        setAmmoLeft(stack, getAmmoLeft(stack) > 0 ? getAmmoLeft(stack) - 1 : 0);
    }

    @OnlyIn(Dist.CLIENT)
    protected void handleFireSoundClient(ItemStack stack, Player player) {
        String muzzleState = getMuzzleFlash(stack);
        if (MUZZLE_STATE_SUPPRESSOR.equals(muzzleState)) {
            if (gunProperties.suppressedSound != null) {
                ModSounds.sound(1, 1f + ((float) Math.random() * 0.1f), player, gunProperties.suppressedSound.get());
            } else {
                if (gunProperties.fireSound != null) {
                    ModSounds.sound(0.5f, 1.6f + ((float) Math.random() * 0.1f), player, gunProperties.fireSound.get());
                }
            }
        } else {
            if (gunProperties.fireSound != null) {
                ModSounds.sound(1, 1f + ((float) Math.random() * 0.1f), player, gunProperties.fireSound.get());
            }
        }
    }

    protected void handleFireSoundServer(ItemStack stack, Player player) {
        String muzzleState = getMuzzleFlash(stack);
        float vol = getFireSoundVol(stack);
        float pitch = 1f + ((float) Math.random() * 0.1f);
        if (MUZZLE_STATE_SUPPRESSOR.equals(muzzleState)) {
            if (gunProperties.suppressedSound != null) {
                ModSounds.boardCastSound(gunProperties.suppressedSound.get(), vol, 1, pitch, (ServerPlayer) player);
            } else {
                ModSounds.boardCastSound(gunProperties.fireSound.get(), vol, 0.5f, 1.6f + ((float) Math.random() * 0.1f), (ServerPlayer) player);
            }
        } else {
            ModSounds.boardCastSound(gunProperties.fireSound.get(), vol, 1f, pitch, (ServerPlayer) player);
        }
    }

    @Override
    public void shoot(ItemStack stack, Player player, IGunFireMode fireMode, float spread) {
        int ammoLeft = getAmmoLeft(stack);
        if (ammoLeft > 0) {
            Caliber caliber = gunProperties.caliber;
            if (!player.level().isClientSide) {
                caliber.fireBullet(null, null, this, player, stack, spread);
                handleFireSoundServer(stack, player);
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
    public void setAttachmentsListTag(ItemStack stack, ListTag list) {
        checkAndGet(stack).put("attachments", list);
        newAttachmentsModifiedUUID(stack);
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
        return properties.contains("recoil_pitch") ?
                Math.max(0, properties.getFloat("recoil_pitch") * gunProperties.recoilPitch) : 0;
    }

    @Override
    public float getRecoilYaw(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("recoil_yaw") ?
                Math.max(0, properties.getFloat("recoil_yaw") * gunProperties.recoilYaw) : 0;
    }

    @Override
    public float getRecoilPitchControl(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("recoil_pitch_control") ?
                Math.max(0, properties.getFloat("recoil_pitch_control") * gunProperties.recoilPitchControl) : 0;
    }

    @Override
    public float getRecoilYawControl(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("recoil_yaw_control") ?
                Math.max(0, properties.getFloat("recoil_yaw_control") * gunProperties.recoilYawControl) : 0;
    }

    @Override
    public float getWalkingSpreadFactor(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("walking_spread_factor") ?
                Math.max(1, properties.getFloat("walking_spread_factor") * gunProperties.walkingSpreadFactor) : 1.3f;
    }

    @Override
    public float getSprintingSpreadFactor(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("sprinting_spread_factor") ?
                Math.max(1, properties.getFloat("sprinting_spread_factor") * gunProperties.sprintingSpreadFactor) : 1.6f;
    }

    @Override
    public float getShootSpread(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("shoot_spread") ?
                Math.max(0, properties.getFloat("shoot_spread") * gunProperties.shootSpread) : 0;
    }

    @Override
    public float getSpreadRecover(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("spread_recover") ?
                Math.max(0, properties.getFloat("spread_recover") * gunProperties.spreadRecover) : 0.1f;
    }

    @Override
    public float getWeight(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("weight") ? Mth.clamp(properties.getFloat("weight"), GunProperties.MIN_WEIGHT, GunProperties.MAX_WEIGHT) : 16;
    }

    @Override
    public float getAgility(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("agility") ? Mth.clamp(properties.getFloat("a"), 0.5f, 2f) : 1f;
    }

    @Override
    public float getAdsSpeed(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        return properties.contains("ads_speed") ?
                Math.max(0, properties.getFloat("ads_speed") * gunProperties.adsSpeed) : 0;
    }

    @Override
    public float[] getSpread(ItemStack stack) {
        CompoundTag properties = getPropertiesTag(stack);
        float minSpread = properties.contains("min_spread") ? Math.max(0, properties.getFloat("min_spread") * gunProperties.minSpread) : 0;
        float maxSpread = properties.contains("max_spread") ? Math.max(0, properties.getFloat("max_spread") * gunProperties.maxSpread) : 0;
        return new float[] {minSpread, maxSpread};
    }

    @Override
    public float getFireSoundVol(ItemStack stack) {
        return getPropertiesTag(stack).contains("fire_sound_vol") ?
                Math.max(0, getPropertiesTag(stack).getFloat("fire_sound_vol") * gunProperties.fireSoundVol) : gunProperties.fireSoundVol;
    }

    @Override
    public boolean clientReload(ItemStack stack, Player player) {
        boolean allow = getAmmoLeft(stack) < getMagSize(stack);
        if (allow) {
            IAmmunition ammunition = this.gunProperties.caliber.ammunition;
            if (ammunition == null) {
                PlayerStatusProvider.setReloading(player, true);
            } else {
                if (AmmunitionHandler.hasAmmunition(this, stack, ammunition, player)) {
                    PlayerStatusProvider.setReloading(player, true);
                    return true;
                } else {
                    String ammunitionName = getFullAmmunitionUsedName(stack);
                    String[] split = Component.translatable("tooltip.screen_info.no_ammo").getString().split("@ammo");
                    Minecraft.getInstance().gui.setOverlayMessage(
                            Component.literal(split[0])
                                    .append(Component.literal(ammunitionName)
                                            .withStyle(Style.EMPTY
                                                    .withColor(new Color(0xe84015).getRGB())
                                                    .withItalic(true)
                                                    .withBold(true)))
                                    .append(Component.literal(split[1]))
                            , false);
                    return false;
                }
            }
        }
        return allow;
    }

    @Override
    public void reload(ItemStack stack, Player player) {
        AmmunitionHandler.reloadFor(player, stack, this, getMagSize(stack));
    }

    @Override
    public int getReloadLength(ItemStack stack, boolean fullReload) {
        CompoundTag properties = getPropertiesTag(stack);
        return fullReload ? properties.getInt("full_reload_length") : properties.getInt("reload_length");
    }

    @Override
    public boolean isUsingSelectedAmmo(ItemStack itemStack) {
        CompoundTag ammunitionData = getAmmunitionData(itemStack);
        if (!ammunitionData.contains("using")) {
            return true;
        }
        String usingID = ammunitionData.getCompound("using").getCompound("mods").getString("modsUUID");
        String selectedID = ammunitionData.getCompound("selected").getCompound("mods").getString("modsUUID");
        return Objects.equals(usingID, selectedID);
    }

    public boolean shouldUseFullReload(ItemStack itemStack) {
        return getAmmoLeft(itemStack) == 0 || !isUsingSelectedAmmo(itemStack);
    }

    @Override
    public IReloadTask getReloadingTask(ItemStack stack, Player player) {
        return new ReloadTask(stack, this);
    }

    @Override
    public IReloadTask getUnloadingTask(ItemStack stack, Player player) {
        return new UnloadTask(this, stack, UnloadTask.RIFLE);
    }


    @Override
    public long getDate(ItemStack stack) {
        CompoundTag tag = checkAndGet(stack);
        return tag.getLong("date");
    }

    @Override
    public void updateDate(ItemStack stack) {
        CompoundTag tag = checkAndGet(stack);
        tag.putLong("date", Commons.SERVER_START_TIME);
    }

    @Override
    public String getAttachmentsModifiedUUID(ItemStack stack) {
        return checkAndGet(stack).getString("attachments_modified_uuid");
    }

    @Override
    public String getEffectiveSightUUID(ItemStack stack) {
        return checkAndGet(stack).getString("effective_sight_uuid");
    }

    @Override
    public void setEffectiveSightUUID(ItemStack stack, String uuid) {
        checkAndGet(stack).putString("effective_sight_uuid", uuid);
    }

    @Override
    public void newAttachmentsModifiedUUID(ItemStack stack) {
        CompoundTag tag = checkAndGet(stack);
        tag.putString("attachments_modified_uuid", UUID.randomUUID().toString());
    }

    @Override
    public String getSelectedAmmunitionTypeUUID(ItemStack stack) {
        CompoundTag tag = checkAndGet(stack);
        if (tag.contains("selected_ammunition_type_uuid")) {
            return tag.getString("selected_ammunition_type_uuid");
        } else {
            return "";
        }
    }

    @Override
    public void bindAmmunition(ItemStack gunStack, ItemStack ammunitionStack, IAmmunition ammunition) {
        String typeUUID = ammunition.getModsUUID(ammunitionStack);
        CompoundTag gunTag = checkAndGet(gunStack);
        gunTag.putString("selected_ammunition_type_uuid", typeUUID);
        if (!gunTag.contains("ammunition_data")) {
            CompoundTag ammunitionData = new CompoundTag();
            gunTag.put("ammunition_data", ammunitionData);
        }
        CompoundTag ammunitionData = gunTag.getCompound("ammunition_data");
        CompoundTag selected = new CompoundTag();
        selected.put("mods", ammunition.getModsTag(ammunitionStack));
        selected.put("data_rate", ammunition.getDataRateTag(ammunitionStack));
        ammunitionData.put("selected", selected);
        if (!ammunitionData.contains("using")) {
            CompoundTag using = new CompoundTag();
            CompoundTag usingMods = new CompoundTag();
            usingMods.putInt("capacity", 0);
            usingMods.putString("modsUUID", "");
            using.put("mods", usingMods);
            CompoundTag usingDataRate = Ammunition.getWhiteDataTag();
            using.put("data_rate", usingDataRate);
            ammunitionData.put("using", using);
        }
    }

    @Override
    public void clearAmmo(ItemStack gunStack, Player player) {

    }

    public CompoundTag checkAndGet(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt == null) {
            this.onCraftedBy(stack, null, null);
            nbt = stack.getTag();
        }
        return nbt;
    }

    public void setMagnificationsRateFor(String scopeId, ItemStack stack, float rate) {
        CompoundTag magnifications = checkAndGetMagnificationsTag(stack);
        magnifications.putFloat(scopeId, Mth.clamp(rate, 0, 1));
    }

    public float getMagnificationsRateFor(String scopeId, ItemStack stack) {
        CompoundTag magnifications = checkAndGetMagnificationsTag(stack);
        return magnifications.getFloat(scopeId);
    }

    public float getMagnificationsRateFor(Scope scope, ItemStack stack) {
        String id = AttachmentsRegister.getStrKey(scope);
        if (id == null) {
            return 0;
        }
        return getMagnificationsRateFor(id, stack);
    }

    public CompoundTag checkAndGetMagnificationsTag(ItemStack stack) {
        CompoundTag tag = checkAndGet(stack);
        if (!tag.contains("scope_magnifications")) {
            CompoundTag magnificationMap = new CompoundTag();
            tag.put("scope_magnifications", magnificationMap);
        }
        return tag.getCompound("scope_magnifications");
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        super.onCraftedBy(pStack, pLevel, pPlayer);
        CompoundTag nbt = pStack.getTag();
        if (nbt == null) {
            nbt = new CompoundTag();
        }
        if (!nbt.contains("date")) {
            nbt.putLong("date", Commons.SERVER_START_TIME);
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
        if (!nbt.contains("attachments_modified_uuid")) {
            nbt.putString("attachments_modified_uuid", UUID.randomUUID().toString());
        }
        if (!nbt.contains("effective_sight_uuid")) {
            nbt.putString("effective_sight_uuid", "none");
        }
        if (!nbt.contains("scope_magnifications")) {
            CompoundTag magnificationMap = new CompoundTag();
            nbt.put("scope_magnifications", magnificationMap);
        }
        if (!nbt.contains("selected_ammunition_type_uuid"))  {
            nbt.putString("selected_ammunition_type_uuid", "");
        }
        pStack.setTag(nbt);
    }

    public String getIdentity(ItemStack stack) {
        CompoundTag nbt = checkAndGet(stack);
        return nbt.contains("identity_temp") ? nbt.getString("identity_temp") : "";
    }

    public boolean isAmmunitionBind(ItemStack stack) {
        return checkAndGet(stack).contains("ammunition_data");
    }

    @Override
    public void afterGunDataUpdate(Player player, ItemStack stack) {
        CompoundTag scopeMagnifications = checkAndGetMagnificationsTag(stack);
        Set<String> keyToRemove = new HashSet<>();
        for (String key : scopeMagnifications.getAllKeys()) {
            if (!(AttachmentsRegister.get(key) instanceof Scope)) {
                keyToRemove.add(key);
            }
        }
        for (String key : keyToRemove) {
            scopeMagnifications.remove(key);
        }
    }

    public String getIdentityTemp(ItemStack stack) {
        CompoundTag nbt = checkAndGet(stack);
        return nbt.contains("identity_temp") ? nbt.getString("identity_temp") : "";
    }

    public void onEquipped(ItemStack itemStack, Player player) {
        CompoundTag tag = checkAndGet(itemStack);
        if (!tag.contains("identity_temp")) {
            tag.putString("identity_temp", UUID.randomUUID().toString());
        }
        if (!isAmmunitionBind(itemStack)) {
            AmmunitionHandler.checkAndUpdateAmmunitionBind(player, itemStack, this);
        }
    }

    protected String getFullAmmunitionUsedName(ItemStack itemStack) {
        IAmmunition ammunition = gunProperties.caliber.ammunition;
        StringBuilder baseName = new StringBuilder(Component.translatable(ammunition.get().getDescriptionId()).getString());
        CompoundTag tag = getAmmunitionData(itemStack);
        CompoundTag selected = tag.getCompound("selected");
        CompoundTag modsTag = selected.getCompound("mods");
        List<IAmmunitionMod> mods = ammunition.getMods(modsTag);
        if (mods.size() > 0) {
            baseName.append("(");
            for (int i = 0; i < mods.size(); i++) {
                IAmmunitionMod mod = mods.get(i);
                baseName.append(Component.translatable(mod.getDescriptionId()).getString());
                baseName.append(i == mods.size() - 1 ? ")" : ", ");
            }
        } else {
            baseName.append(Component.translatable("tooltip.screen_info.unmodified").getString());
        }
        return baseName.toString();
    }

    public CompoundTag getAmmunitionData(ItemStack itemStack) {
        CompoundTag tag = checkAndGet(itemStack);
        return tag.contains("ammunition_data") ? tag.getCompound("ammunition_data") : new CompoundTag();
    }

    public CompoundTag getUsingAmmunitionData(ItemStack itemStack) {
        CompoundTag tag = checkAndGet(itemStack);
        if (!tag.contains("ammunition_data")) {
            return null;
        }
        CompoundTag ammunitionData = tag.getCompound("ammunition_data");
        return ammunitionData.getCompound("using");
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull Object getRenderPropertiesInternal() {
        return ArmPoseHandler.ARM_POSE_HANDLER;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        Player player = Minecraft.getInstance().player;
        if (player == null || newStack == player.getOffhandItem()) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
        }
        if (slotChanged) {
            ReloadingHandler.INSTANCE.cancelTask();
            HandActionHandler.INSTANCE.breakTask();
            SprintingHandler.INSTANCE.exitSprinting(40);
            BulletShellRenderer.clear();
            Clients.MAIN_HAND_STATUS.buttonDown.set(false);
            Clients.MAIN_HAND_STATUS.ads = false;
            Clients.setEquipDelay(3);
            player.resetAttackStrengthTicker();
        }
        return Clients.getEquipDelay() > 0;
    }

    public float getEquipSpeedModifier(ItemStack itemStack, IGun gun) {
        float weight = gun.getWeight(itemStack);
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
        gunBaseInfo(stack, levelIn, tooltip, flagIn);
        if (Clients.displayGunInfoDetails) {
            gunDetailInfo(stack, levelIn, tooltip, flagIn);
        } else {
            String showDetail = Component.translatable("tooltip.gcaa.show_full_gun_info").getString();
            showDetail = showDetail.replace("$key", KeyBinds.SHOW_FULL_GUN_INFO.getTranslatedKeyMessage().getString());
            tooltip.add(FontUtils.helperTip(Component.literal(showDetail)));
            tooltip.add(FontUtils.getExcellentWorse());
        }
    }

    protected void gunBaseInfo(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(FontUtils.dataTip("tooltip.gun_info.mag_size", getMagSize(stack), 100, 0));
        tooltip.add(FontUtils.dataTip("tooltip.gun_info.rpm", gunProperties.getRPM(), 1200, 200));
        tooltip.add(FontUtils.dataTip("tooltip.gun_info.weight", getWeight(stack), 5, 40));
        gunProperties.caliber.handleTooltip(stack, this, levelIn, tooltip, flagIn, false);
        if (gunProperties.caliber.ammunition != null) {
            String ammo = Component.translatable("tooltip.gun_info.ammunition").getString();
            String name = getFullAmmunitionUsedName(stack);
            tooltip.add(Component.literal(ammo + name));
        }
    }

    protected void gunDetailInfo(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
        gunProperties.caliber.handleTooltip(stack, this, levelIn, tooltip, flagIn, true);
        tooltip.add(FontUtils.getExcellentWorse());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
        return ItemStack.TooltipPart.MODIFIERS.getMask();
    }

    protected static int getTicks(float seconds) {
        return RenderAndMathUtils.secondsToTicks(seconds);
    }

    public int getCrosshairType() {
        return 0;
    }
}

