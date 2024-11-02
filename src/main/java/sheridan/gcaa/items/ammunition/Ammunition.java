package sheridan.gcaa.items.ammunition;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.items.NoRepairNoEnchantmentItem;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.AmmunitionManagePacket;

import java.util.List;
import java.util.Set;

public class Ammunition extends NoRepairNoEnchantmentItem implements IAmmunition{
    private static final int AMMUNITION_MANAGE_DELAY = 1000;
    private static long lastAmmunitionManageTime = 0;

    private final Set<IAmmunitionMod> suitableMods;
    private int modCapacity;

    public Ammunition(int capacity, int modCapacity, Set<IAmmunitionMod> suitableMods)  {
        super(new Properties().defaultDurability(capacity).setNoRepair());
        this.suitableMods = suitableMods;
        this.modCapacity = modCapacity;
    }

    public static void manageAmmunition(Player player, ItemStack stack) {

    }

    @OnlyIn(Dist.CLIENT)
    public static void clientManageAmmunition(Player player, ItemStack stack) {
        if (System.currentTimeMillis() - lastAmmunitionManageTime > AMMUNITION_MANAGE_DELAY) {
            PacketHandler.simpleChannel.sendToServer(new AmmunitionManagePacket());
            manageAmmunition(player, stack);
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
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.ammunition_info.ammo_left").append(getAmmoLeft(pStack) + " /  " + getMaxCapacity(pStack)));
    }

    @Override
    public int getAmmoLeft(ItemStack itemStack) {
        return getMaxCapacity(itemStack) - itemStack.getDamageValue();
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
    public Set<IAmmunitionMod> getMods(ItemStack itemStack) {
        throw new NotImplementedException();
    }

    @Override
    public void putMod(IAmmunitionMod mod) {

    }

    @Override
    public void removeMod(IAmmunitionMod mod) {

    }
}
