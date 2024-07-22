package sheridan.gcaa.items.gun.calibers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sheridan.gcaa.entities.ModEntities;
import sheridan.gcaa.entities.projectiles.Projectile;
import sheridan.gcaa.items.ammunitions.IAmmunition;
import sheridan.gcaa.items.gun.ICaliber;
import sheridan.gcaa.items.gun.IGun;

public class Caliber7_62x39mm implements ICaliber {
    public static final Caliber7_62x39mm INSTANCE = new Caliber7_62x39mm();

    @Override
    public String getName() {
        return "7.62x39mm";
    }

    @Override
    public void fireBullet(IAmmunition ammunition, ItemStack ammunitionStack, IGun gun, Player player, ItemStack gunStack, float spread) {
        Level level = player.level();
        float damage = (float) (8 * (Math.round((Math.random() - 0.5) * 20) * 0.01 + 1));
        Projectile projectile = new Projectile(
                ModEntities.PROJECTILE.get(), level, player,
                player.getLookAngle(), 7.2f, spread, damage,
                0, 0, 5 * 16, 0, gun);
        level.addFreshEntity(projectile);
    }

    @Override
    public int getCost() {
        return 7;
    }
}
