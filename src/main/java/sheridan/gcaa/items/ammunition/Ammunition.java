package sheridan.gcaa.items.ammunition;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.items.NoRepairNoEnchantmentItem;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.AmmunitionManagePacket;
import sheridan.gcaa.utils.FontUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Ammunition extends NoRepairNoEnchantmentItem implements IAmmunition{
    private static final int AMMUNITION_MANAGE_DELAY = 1000;
    private static long lastAmmunitionManageTime = 0;
    private final Set<IAmmunitionMod> suitableMods;
    private final int modCapacity;
    public static final String BASE_DAMAGE_RATE = "base_damage_rate";
    public static final String MIN_DAMAGE_RATE = "min_damage_rate";
    public static final String EFFECTIVE_RANGE_RATE = "effective_range_rate";
    public static final String SPEED_RATE = "speed_rate";
    public static final String PENETRATION_RATE = "penetration_rate";
    public static final String DATA_RATE = "data_rate";
    public static final float MIN_BASE_DAMAGE_RATE = 0.02f;
    public static final float MIN_MIN_DAMAGE_RATE = 0.01f;
    public static final float MIN_EFFECTIVE_RANGE_RATE = 0.01f;
    public static final float MIN_SPEED_RATE = 0.01f;
    public static final float MIN_PENETRATION_RATE = 0.01f;

    public Ammunition(int capacity, int modCapacity, Set<IAmmunitionMod> suitableMods)  {
        super(new Properties().defaultDurability(capacity).setNoRepair());
        this.suitableMods = new HashSet<>(suitableMods);
        this.modCapacity = modCapacity;
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientManageAmmunition(Player player, ItemStack stack) {
        if (System.currentTimeMillis() - lastAmmunitionManageTime > AMMUNITION_MANAGE_DELAY) {
            PacketHandler.simpleChannel.sendToServer(new AmmunitionManagePacket());
            AmmunitionHandler.manageAmmunition(player, stack);
            lastAmmunitionManageTime = System.currentTimeMillis();
        } else {
            Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("tooltip.screen_info.ammunition_manage_cool_down"), false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void onRightClick(Player player, ItemStack stack) {
        clientManageAmmunition(player, stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.ammunition_info.ammo_left").append(getAmmoLeft(pStack) + " /  " + getMaxCapacity(pStack)));
        List<IAmmunitionMod> mods = getMods(pStack);
        if (mods.size() > 0) {
            List<Component> specials = new ArrayList<>();
            for (IAmmunitionMod mod : mods) {
                pTooltipComponents.add(Component.translatable(mod.getDescriptionId()).withStyle(Style.EMPTY.withColor(mod.getThemeColor()).withBold(true)));
                Component component = mod.getSpecialDescription();
                if (component != null) {
                    specials.add(component.copy().withStyle(Style.EMPTY.withColor(mod.getThemeColor()).withItalic(true)));
                }
            }
            CompoundTag dataRate = getDataRateTag(pStack);
            float baseDamageRate = Math.max(dataRate.getFloat(BASE_DAMAGE_RATE), MIN_BASE_DAMAGE_RATE);
            if (baseDamageRate != 1.0f) {
                pTooltipComponents.add(Component.translatable("gcaa.ammunition_data.base_damage_rate")
                        .append(Component.literal(FontUtils.toPercentageStr(baseDamageRate)).withStyle(Style.EMPTY.withBold(true))));
            }
            float minDamageRate = Math.max(dataRate.getFloat(MIN_DAMAGE_RATE), MIN_MIN_DAMAGE_RATE);
            if (minDamageRate != 1.0f) {
                pTooltipComponents.add(Component.translatable("gcaa.ammunition_data.min_damage_rate")
                        .append(Component.literal(FontUtils.toPercentageStr(minDamageRate)).withStyle(Style.EMPTY.withBold(true))));
            }
            float effectiveRangeRate = Math.max(dataRate.getFloat(EFFECTIVE_RANGE_RATE), MIN_EFFECTIVE_RANGE_RATE);
            if (effectiveRangeRate != 1.0f) {
                pTooltipComponents.add(Component.translatable("gcaa.ammunition_data.effective_range_rate")
                        .append(Component.literal(FontUtils.toPercentageStr(effectiveRangeRate)).withStyle(Style.EMPTY.withBold(true))));
            }
            float speedRate = Math.max(dataRate.getFloat(SPEED_RATE), MIN_SPEED_RATE);
            if (speedRate != 1.0f) {
                pTooltipComponents.add(Component.translatable("gcaa.ammunition_data.speed_rate")
                        .append(Component.literal(FontUtils.toPercentageStr(speedRate)).withStyle(Style.EMPTY.withBold(true))));
            }
            float penetrationRate = Math.max(dataRate.getFloat(PENETRATION_RATE), MIN_PENETRATION_RATE);
            if (penetrationRate != 1.0f) {
                pTooltipComponents.add(Component.translatable("gcaa.ammunition_data.penetration_rate")
                        .append(Component.literal(FontUtils.toPercentageStr(penetrationRate)).withStyle(Style.EMPTY.withBold(true))));
            }
            pTooltipComponents.addAll(specials);
        }
        pTooltipComponents.add(FontUtils.helperTip(Component.literal(Component.translatable("tooltip.gcaa.manage_ammunition").getString())));
    }

    @Override
    public int getAmmoLeft(ItemStack itemStack) {
        return getMaxCapacity(itemStack) - itemStack.getDamageValue();
    }

    @Override
    public void setAmmoLeft(ItemStack itemStack, int leftCount) {
        itemStack.setDamageValue(getMaxCapacity(itemStack) - leftCount);
    }

    @Override
    public int getMaxCapacity(ItemStack itemStack) {
        return getMaxDamage(itemStack);
    }

    @Override
    public int getMaxModCapacity() {
        return modCapacity;
    }

    @Override
    public int getModCapacityUsed(ItemStack itemStack) {
        CompoundTag modTag = getModsTag(itemStack);
        return modTag.contains("capacity") ? modTag.getInt("capacity") : 0;
    }

    @Override
    public int getModCapacityLeft(ItemStack itemStack) {
        return getMaxModCapacity() - getModsTag(itemStack).getInt("capacity");
    }

    @Override
    public boolean isModSuitable(ItemStack itemStack, IAmmunitionMod ammunitionMod) {
        return suitableMods.contains(ammunitionMod) && getModCapacityLeft(itemStack) >= ammunitionMod.getCostFor(this);
    }

    @Override
    public Set<IAmmunitionMod> getSuitableMods() {
        return suitableMods;
    }

    @Override
    public List<IAmmunitionMod> getMods(ItemStack itemStack) {
        CompoundTag tag = checkAndGet(itemStack);
        if (tag.contains("mods")) {
            CompoundTag modsTag = tag.getCompound("mods");
            return getMods(modsTag);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<IAmmunitionMod> getMods(CompoundTag modsTag) {
        Set<String> allKeys = modsTag.getAllKeys();
        List<IAmmunitionMod> mods = new ArrayList<>();
        for (String key : allKeys) {
            if ("capacity".equals(key) || "modsUUID".equals(key)) {
                continue;
            }
            IAmmunitionMod mod = AmmunitionModRegister.getAmmunitionMod(key);
            if (mod != null) {
                mods.add(mod);
            }
        }
        return mods;
    }


    @Override
    public void addModById(String modId, ItemStack itemStack) {
        IAmmunitionMod mod = AmmunitionModRegister.getAmmunitionMod(modId);
        if (mod != null) {
            addMod(mod, itemStack);
        }
    }

    @Override
    public void addMod(IAmmunitionMod mod, ItemStack itemStack) {
        CompoundTag tag = checkAndGet(itemStack);
        CompoundTag modTag = tag.getCompound("mod");
        if (modTag.contains(mod.getId().toString())) {
            return;
        }
        int maxModCapacity = tag.getInt("max_mod_capacity");
        int capacity = modTag.getInt("capacity");
        if (maxModCapacity - capacity - mod.getCostFor(this) >= 0) {
            modTag.putByte(mod.getId().toString(), (byte) 0);
            String uuid = genModsUUID(itemStack);
            modTag.putString("modsUUID", uuid);
            tag.putString("modsUUID", uuid);
            modTag.putInt("capacity", capacity + mod.getCostFor(this));
            refineDataRate(itemStack);
        }
    }

    @Override
    public void addMods(List<IAmmunitionMod> mods, ItemStack itemStack) {
        CompoundTag tag = checkAndGet(itemStack);
        CompoundTag modTag = tag.getCompound("mods");
        int maxModCapacity = tag.getInt("max_mod_capacity");
        boolean resetUUID = false;
        for (IAmmunitionMod mod : mods) {
            int capacityLeft = maxModCapacity - modTag.getInt("capacity");
            if (!modTag.contains(mod.getId().toString()) && capacityLeft >= mod.getCostFor(this))  {
                resetUUID = true;
                modTag.putByte(mod.getId().toString(), (byte) 0);
                modTag.putInt("capacity", modTag.getInt("capacity") + mod.getCostFor(this));
            }
        }
        if (resetUUID) {
            String uuid = genModsUUID(itemStack);
            modTag.putString("modsUUID", uuid);
            tag.putString("modsUUID", uuid);
            refineDataRate(itemStack);
        }
    }

    @Override
    public void addModsById(List<String> modIdList, ItemStack itemStack) {
        CompoundTag tag = checkAndGet(itemStack);
        CompoundTag modTag = tag.getCompound("mods");
        int maxModCapacity = tag.getInt("max_mod_capacity");
        boolean resetUUID = false;
        for (String modId : modIdList) {
            IAmmunitionMod mod = AmmunitionModRegister.getAmmunitionMod(modId);
            if (mod == null) {
                continue;
            }
            int capacityLeft = maxModCapacity - modTag.getInt("capacity");
            if (!modTag.contains(mod.getId().toString()) && capacityLeft >= mod.getCostFor(this))  {
                resetUUID = true;
                modTag.putByte(mod.getId().toString(), (byte) 0);
                modTag.putInt("capacity", modTag.getInt("capacity") + mod.getCostFor(this));
            }
        }
        if (resetUUID) {
            String uuid = genModsUUID(itemStack);
            modTag.putString("modsUUID", uuid);
            tag.putString("modsUUID", uuid);
            refineDataRate(itemStack);
        }
    }


    @Override
    public boolean canMerge(ItemStack thisStack, ItemStack otherStack) {
        return thisStack.getItem() == otherStack.getItem() && Objects.equals(getModsUUID(thisStack), getModsUUID(otherStack));
    }

    @Override
    public Ammunition get() {
        return this;
    }

    public CompoundTag checkAndGet(ItemStack itemStack) {
        CompoundTag nbt = itemStack.getTag();
        if (nbt == null || !nbt.contains("mods") || !nbt.contains(DATA_RATE))  {
            this.onCraftedBy(itemStack, null, null);
            nbt = itemStack.getTag();
        }
        return nbt;
    }

    @Override
    public void onCraftedBy(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Player pPlayer) {
        super.onCraftedBy(pStack, pLevel, pPlayer);
        CompoundTag nbt = pStack.getTag();
        if (nbt == null) {
            nbt = new CompoundTag();
            pStack.setTag(nbt);
        }
        if (!nbt.contains("mods")) {
            CompoundTag modTag = new CompoundTag();
            modTag.putInt("capacity", 0);
            modTag.putString("modsUUID", "");
            nbt.put("mods", modTag);
        }
        if (!nbt.contains(DATA_RATE)) {
            CompoundTag rateTag = getWhiteDataTag();
            nbt.put(DATA_RATE, rateTag);
        }
        if (!nbt.contains("max_mod_capacity")) {
            nbt.putInt("max_mod_capacity", getMaxModCapacity());
        }
        if (!nbt.contains("modsUUID")) {
            nbt.putString("modsUUID", "");
        }
    }

    public static CompoundTag getWhiteDataTag() {
        CompoundTag rateTag = new CompoundTag();
        rateTag.putFloat(BASE_DAMAGE_RATE, 1);
        rateTag.putFloat(MIN_DAMAGE_RATE, 1);
        rateTag.putFloat(EFFECTIVE_RANGE_RATE, 1);
        rateTag.putFloat(SPEED_RATE, 1);
        rateTag.putFloat(PENETRATION_RATE, 1);
        return rateTag;
    }

    public static CompoundTag processDataRateByGivenMods(CompoundTag rateTag, List<IAmmunitionMod> mods, IAmmunition ammunition) {
        for (IAmmunitionMod mod : mods) {
            mod.onModifyAmmunition(ammunition,  rateTag);
        }
        return rateTag;
    }

    protected void refineDataRate(ItemStack itemStack) {
        CompoundTag tag = checkAndGet(itemStack);
        CompoundTag dataTag = getWhiteDataTag();
        List<IAmmunitionMod> mods = getMods(itemStack);
        for (IAmmunitionMod mod : mods) {
            mod.onModifyAmmunition(this, dataTag);
        }
        tag.put(DATA_RATE, dataTag);
    }

    @Override
    public String getModsUUID(ItemStack itemStack) {
        CompoundTag tag = checkAndGet(itemStack);
        if (tag.contains("modsUUID")) {
            return tag.getString("modsUUID");
        }
        String uuid = genModsUUID(itemStack);
        tag.putString("modsUUID", uuid);
        CompoundTag modsTag = getModsTag(itemStack);
        modsTag.putString("modsUUID", uuid);
        return uuid;
    }

    protected String genModsUUID(ItemStack itemStack) {
        List<IAmmunitionMod> mods = getMods(itemStack);
        return genModsUUID(mods);
    }

    @Override
    public CompoundTag getModsTag(ItemStack itemStack) {
        CompoundTag tag = checkAndGet(itemStack);
        return tag.contains("mods") ? tag.getCompound("mods") : new CompoundTag();
    }

    @Override
    public CompoundTag getDataRateTag(ItemStack itemStack) {
        return checkAndGet(itemStack).getCompound(DATA_RATE);
    }

    @Override
    public String getFullName(ItemStack itemStack) {
        String baseName = Component.translatable(getDescriptionId()).getString();
        List<IAmmunitionMod> mods = getMods(itemStack);
        if (mods.size() == 0) {
            return baseName;
        }
        StringBuilder suffix = new StringBuilder();
        suffix.append(baseName).append("-");
        for (IAmmunitionMod mod : mods) {
            suffix.append(Component.translatable(mod.getDescriptionId()).getString()).append(" ");
        }
        return suffix.toString();
    }

    @Override
    public String genModsUUID(List<IAmmunitionMod> mods) {
        if (mods.size() == 0) {
            return "";
        }
        mods.sort(Comparator.comparing(m -> m.getId().toString()));
        StringBuilder id = new StringBuilder();
        for (IAmmunitionMod mod : mods) {
            id.append(mod.getId().toString());
        }
        return UUID.nameUUIDFromBytes(id.toString().getBytes(StandardCharsets.UTF_8)).toString();
    }
}
