package sheridan.gcaa.items.gun.calibers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.ammunitions.IAmmunition;
import sheridan.gcaa.items.gun.ICaliber;
import sheridan.gcaa.items.gun.IGun;

public class Caliber9x19mm implements ICaliber {

    public static final Caliber9x19mm INSTANCE = new Caliber9x19mm();

    @Override
    public String getName() {
        return "9x19mm";
    }

    @Override
    public void fireBullet(IAmmunition ammunition, ItemStack ammunitionStack, IGun gun, Player player, ItemStack gunStack) {

    }

    @Override
    public int getCost() {
        return 5;
    }
}
