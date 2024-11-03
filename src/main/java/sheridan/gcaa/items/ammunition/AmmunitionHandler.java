package sheridan.gcaa.items.ammunition;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.gun.IGun;

public class AmmunitionHandler {

    public static void checkAndUpdateAmmunition(Player player, ItemStack itemStack, IGun gun) {

    }

    public static void manageAmmunition(Player player, ItemStack ammunitionStack) {
        if (ammunitionStack.getItem() instanceof IAmmunition ammunition) {
            String modsUUID = ammunition.getModsUUID(ammunitionStack);

        }
    }
}
