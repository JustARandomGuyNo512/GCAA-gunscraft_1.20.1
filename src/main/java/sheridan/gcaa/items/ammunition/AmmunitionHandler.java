package sheridan.gcaa.items.ammunition;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import sheridan.gcaa.items.gun.IGun;

import java.util.*;

public class AmmunitionHandler {

    public static void checkAndUpdateAmmunitionBind(Player player, ItemStack gunStack, IGun gun) {
        String modsUUID = gun.getSelectedAmmunitionTypeUUID(gunStack);
        NonNullList<ItemStack> items = player.getInventory().items;
        IAmmunition gunAmmunition = gun.getGunProperties().caliber.ammunition;
        ItemStack useAmmo = null;
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof IAmmunition Ammo && Ammo == gunAmmunition) {
                if (useAmmo == null) {
                    useAmmo = stack;
                }
                String stackAmmoModsUUID = Ammo.getModsUUID(stack);
                if (Objects.equals(stackAmmoModsUUID, modsUUID)) {
                    useAmmo = stack;
                    break;
                }
            }
        }
        if (useAmmo == null) {
            gun.setSelectedAmmunitionTypeUUID(gunStack, "");
            return;
        }
        gun.setSelectedAmmunitionTypeUUID(gunStack, gunAmmunition.getModsUUID(useAmmo));
    }

    public static void manageAmmunition(Player player, ItemStack ammunitionStack) {
        if (ammunitionStack.getItem() instanceof IAmmunition ammunition) {
            NonNullList<ItemStack> items = player.getInventory().items;
            int totalCount = 0;
            CompoundTag mods = ammunition.get().checkAndGet(ammunitionStack);
            for (int i = 0; i < items.size(); i ++) {
                ItemStack itemStack = items.get(i);
                if (itemStack.getItem() instanceof IAmmunition stackAmmunition) {
                    stackAmmunition.get().checkAndGet(itemStack);
                    if (ammunition.canMerge(ammunitionStack, itemStack)) {
                        totalCount += ammunition.getAmmoLeft(itemStack);
                        items.set(i, new ItemStack(Items.AIR));
                    }
                }
            }
            if (totalCount == 0) {
                return;
            }
            int capacity = ammunition.getMaxCapacity(ammunitionStack);
            while (totalCount > 0) {
                int ammoCount = Math.min(totalCount, capacity);
                ItemStack itemStack = new ItemStack(ammunitionStack.getItem());
                itemStack.setTag(mods.copy());
                ammunition.setAmmoLeft(itemStack, ammoCount);
                totalCount -= ammoCount;
                if (!player.addItem(itemStack)) {
                    player.drop(itemStack, false);
                }
            }
        }
    }

    public static void andAmmunition(Player player, IAmmunition ammunition, int count) {
        NonNullList<ItemStack> items = player.getInventory().items;
        int left = count;
        for (ItemStack itemStack : items) {
            if (itemStack.getItem() instanceof IAmmunition ammo && ammo == ammunition && Objects.equals(ammo.getModsUUID(itemStack), "")) {
                int capacityLeft = ammo.getMaxCapacity(itemStack) - ammo.getAmmoLeft(itemStack);
                if (capacityLeft > 0) {
                    int add = Math.min(capacityLeft, left);
                    ammo.setAmmoLeft(itemStack, ammo.getAmmoLeft(itemStack) + add);
                    left -= add;
                    if (left == 0) {
                        return;
                    }
                }
            }
        }
        int capacity = ammunition.get().getMaxDamage();
        while (left > 0) {
            int ammoCount = Math.min(left, capacity);
            ItemStack itemStack = new ItemStack(ammunition.get());
            ammunition.setAmmoLeft(itemStack, ammoCount);
            if (!player.addItem(itemStack)) {
                player.drop(itemStack, false);
            }
            left -= ammoCount;
        }
    }

    public static void andAmmunition(Player player, IAmmunition ammunition, ItemStack itemStack) {

    }

    public static void reloadFor(Player player, ItemStack gunStack, IGun gun, int exceptedReloadNum) {
        if (exceptedReloadNum <= 0 || gunStack != player.getMainHandItem()) {
            return;
        }
        exceptedReloadNum = Math.min(exceptedReloadNum, gun.getMagSize(gunStack) - gun.getAmmoLeft(gunStack));
        int findCount = 0;
        NonNullList<ItemStack> items = player.getInventory().items;
        IAmmunition gunAmmunition = gun.getGunProperties().caliber.ammunition;
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.getItem() instanceof IAmmunition ammunition &&
                    ammunition == gunAmmunition && Objects.equals(ammunition.getModsUUID(stack), gunAmmunition.getModsUUID(gunStack))) {
                int ammoLeft = ammunition.getAmmoLeft(stack);
                if (ammoLeft >= exceptedReloadNum) {
                    findCount = exceptedReloadNum;
                    if (ammoLeft - exceptedReloadNum == 0) {
                        items.set(i, new ItemStack(Items.AIR));
                    } else {
                        ammunition.setAmmoLeft(stack, ammoLeft - exceptedReloadNum);
                    }
                    break;
                } else {
                    if (findCount + ammoLeft <= exceptedReloadNum) {
                        findCount += ammoLeft;
                        items.set(i, new ItemStack(Items.AIR));
                    } else {
                        int need = exceptedReloadNum - findCount;
                        ammunition.setAmmoLeft(stack, ammoLeft - need);
                        findCount = exceptedReloadNum;
                        break;
                    }
                }
            }
        }
        if (findCount != 0) {
            gun.setAmmoLeft(gunStack, gun.getAmmoLeft(gunStack) + findCount);
        }
    }

    public static int getAmmunitionCount(ItemStack gunStack, IGun gun, Player player) {
        int findCount = 0;
        NonNullList<ItemStack> items = player.getInventory().items;
        IAmmunition gunAmmunition = gun.getGunProperties().caliber.ammunition;
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof IAmmunition ammunition &&
                    ammunition == gunAmmunition && Objects.equals(ammunition.getModsUUID(stack), gunAmmunition.getModsUUID(gunStack))) {
                int ammoLeft = ammunition.getAmmoLeft(stack);
                findCount += ammoLeft;
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
