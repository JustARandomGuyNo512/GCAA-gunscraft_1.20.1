package sheridan.gcaa.items.gun.calibers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sheridan.gcaa.entities.ModEntities;
import sheridan.gcaa.entities.projectiles.Projectile;
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
    public void fireBullet(IAmmunition ammunition, ItemStack ammunitionStack, IGun gun, Player player, ItemStack gunStack, float spread) {
        Level level = player.level();
        float damage = (float) (5 * (Math.round((Math.random() - 0.5) * 20) * 0.01 + 1));
        Projectile projectile = new Projectile(
                ModEntities.PROJECTILE.get(), level, player,
                player.getLookAngle(), 3.6f, spread, damage,
                0, 0, 3 * 16, 0, gun);
        level.addFreshEntity(projectile);
    }

    @Override
    public int getCost() {
        return 5;
    }
}
