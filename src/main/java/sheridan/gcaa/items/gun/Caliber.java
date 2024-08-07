package sheridan.gcaa.items.gun;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.entities.ModEntities;
import sheridan.gcaa.entities.projectiles.Projectile;
import sheridan.gcaa.items.ammunitions.IAmmunition;

import java.util.List;


public abstract class Caliber {
    public ProjectileData projectileData;

    public Caliber(ProjectileData projectileData) {
        this.projectileData = projectileData;
    }

    public abstract String getName();
    public abstract void fireBullet(IAmmunition ammunition, ItemStack ammunitionStack, IGun gun, Player player, ItemStack gunStack, float spread);
    public abstract int getCost();

    public void handleTooltip(ItemStack stack, IGun gun, Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {

    }

    protected Projectile defaultProjectile(Player player, Level level, float spread, IGun gun) {
        float damage = (float) (projectileData.baseDamage * (Math.round((Math.random() - 0.5) * 20) * 0.01 + 1));
        return new Projectile(
                ModEntities.PROJECTILE.get(), level, player,
                player.getLookAngle(), projectileData.speed, spread, damage,
                projectileData.minDamage, 0, projectileData.effectiveRange, 0, gun);
    }
}
