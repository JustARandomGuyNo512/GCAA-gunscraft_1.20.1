package sheridan.gcaa.items.ammunition;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.gun.IGun;

public class AmmunitionHandler {

    public static void checkAndUpdateAmmunitionBind(Player player, ItemStack gunStack, IGun gun) {
        String modsUUID = gun.getSelectedAmmunitionTypeUUID(gunStack);
        NonNullList<ItemStack> items = player.getInventory().items;
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof IAmmunition stackAmmo) {

            }
        }
    }

    public static void manageAmmunition(Player player, ItemStack ammunitionStack) {
        if (ammunitionStack.getItem() instanceof IAmmunition ammunition) {
            String modsUUID = ammunition.getModsUUID(ammunitionStack);

        }
    }

    public static void reloadFor(Player player, ItemStack gunItem, IGun gun, int exceptedNum) {
        int findCount = 0;
        NonNullList<ItemStack> items = player.getInventory().items;
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof IAmmunition ammunition) {

            }
        }

    }

    public static int getAmmunitionCount(ItemStack itemStack, IAmmunition ammunition, Player player) {
        int findCount = 0;
        NonNullList<ItemStack> items = player.getInventory().items;
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof IAmmunition stackAmmo) {

            }
        }
        return findCount;
    }

    public static boolean hasAmmunition(ItemStack itemStack, IAmmunition ammunition, Player player) {
        NonNullList<ItemStack> items = player.getInventory().items;
        for (ItemStack stack : items) {
            if (stack.getItem() == ammunition) {
                return ammunition.getAmmoLeft(stack) > 0;
            }
        }
        return false;
    }
}
