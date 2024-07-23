package sheridan.gcaa.items.gun.calibers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sheridan.gcaa.items.ammunitions.IAmmunition;
import sheridan.gcaa.items.gun.Caliber;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.ProjectileData;

public class Caliber7_62x39mm extends Caliber {

    public Caliber7_62x39mm(ProjectileData projectileData) {
        super(projectileData);
    }

    @Override
    public String getName() {
        return "7.62x39mm";
    }

    @Override
    public void fireBullet(IAmmunition ammunition, ItemStack ammunitionStack, IGun gun, Player player, ItemStack gunStack, float spread) {
        Level level = player.level();
        level.addFreshEntity(defaultProjectile(player, level, spread, gun));
    }

    @Override
    public int getCost() {
        return 7;
    }
}
