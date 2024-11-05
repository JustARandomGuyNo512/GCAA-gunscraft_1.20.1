package sheridan.gcaa.items.ammunition;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.items.NoRepairNoEnchantmentItem;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.AmmunitionManagePacket;
import sheridan.gcaa.utils.FontUtils;

import java.util.*;

public class Ammunition extends NoRepairNoEnchantmentItem implements IAmmunition{
    private static final int AMMUNITION_MANAGE_DELAY = 1000;
    private static long lastAmmunitionManageTime = 0;

    private final Set<IAmmunitionMod> suitableMods;
    private int modCapacity;

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

    public void setModCapacity(int modCapacity)  {
        this.modCapacity = modCapacity;
    }

    /*
    * Adjust the mod capacity of ammo to ensure that the capacity is a rate multiple of the total capacity of getSuitableMods()
    * */
    public void refineModCapacity(float rate) {
        Set<IAmmunitionMod> suitableMods = getSuitableMods();
        for (IAmmunitionMod mod : suitableMods) {
            modCapacity += mod.cost();
        }
        modCapacity = (int) (modCapacity * rate);
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
            //TODO: add mod info
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
    public int getModCapacityLeft(ItemStack itemStack) {
        return 0;
    }

    @Override
    public boolean isModSuitable(ItemStack itemStack, IAmmunitionMod ammunitionMod) {
        return suitableMods.contains(ammunitionMod) && getModCapacityLeft(itemStack) >= ammunitionMod.cost();
    }

    @Override
    public Set<IAmmunitionMod> getSuitableMods() {
        return suitableMods;
    }

    @Override
    public List<IAmmunitionMod> getMods(ItemStack itemStack) {
        CompoundTag tag = checkAndGet(itemStack);
        List<IAmmunitionMod> mods = new ArrayList<>();
        if (tag.contains("mods")) {
            CompoundTag modsTag = tag.getCompound("mods");
            Set<String> allKeys = modsTag.getAllKeys();
            for (String key : allKeys) {
                if ("capacity".equals(key)) {
                    continue;
                }
                IAmmunitionMod mod = AmmunitionModRegister.getAmmunitionMod(key);
                if (mod != null) {
                    mods.add(mod);
                }
            }
        }
        return mods;
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
        if (maxModCapacity - capacity - mod.cost() < 0) {
            throw new NotImplementedException();
        }
    }

    @Override
    public void removeMod(IAmmunitionMod mod, ItemStack itemStack) {

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
        if (nbt == null) {
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
            nbt.put("mods", modTag);
        }
        nbt.putInt("max_mod_capacity", getMaxModCapacity());
        nbt.putString("modsUUID", "");
    }

    @Override
    public String getModsUUID(ItemStack itemStack) {
        CompoundTag tag = checkAndGet(itemStack);
        if (tag.contains("modsUUID")) {
            return tag.getString("modsUUID");
        }
        List<IAmmunitionMod> mods = getMods(itemStack);
        if (mods.size() == 0) {
            return "";
        }
        mods.sort(Comparator.comparing(m -> m.getId().toString()));
        StringBuilder id = new StringBuilder();
        for (IAmmunitionMod mod : mods) {
            id.append(mod.getId().toString());
        }
        String uuid = UUID.fromString(id.toString()).toString();
        tag.putString("modsUUID", uuid);
        return uuid;
    }
}
