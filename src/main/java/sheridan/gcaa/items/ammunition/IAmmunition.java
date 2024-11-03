package sheridan.gcaa.items.ammunition;

import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;

public interface IAmmunition {
    /*
    * returns the amount of ammo left in the item
    * */
    int getAmmoLeft(ItemStack itemStack);
    /*
    * returns the maximum amount of ammo the item can hold
    */
    int getMaxCapacity(ItemStack itemStack);
    /*
     * returns the capacity of mods the ammunition can hold
     */
    int getMaxModCapacity();
    int getModCapacityLeft(ItemStack itemStack);
    boolean isModSuitable(ItemStack itemStack, IAmmunitionMod ammunitionMod);
    /*
     * returns the set of mods that can be applied to the ammunition
     */
    Set<IAmmunitionMod> getSuitableMods();
    /*
     * returns the set of mods that are currently applied to the ammunition
     */
    List<IAmmunitionMod> getMods(ItemStack itemStack);
    void addMod(IAmmunitionMod mod, ItemStack itemStack);
    void removeMod(IAmmunitionMod mod, ItemStack itemStack);
    /*
     * returns true if the two stacks can be merged
     */
    boolean canMerge(ItemStack thisStack, ItemStack otherStack);
    /*
    * get the Ammunition class instance of the item
    * */
    Ammunition get();

    String getModsUUID(ItemStack itemStack);
}
