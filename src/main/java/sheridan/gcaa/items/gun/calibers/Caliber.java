package sheridan.gcaa.items.gun.calibers;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.entities.ModEntities;
import sheridan.gcaa.entities.projectiles.Projectile;
import sheridan.gcaa.items.ammunitions.IAmmunition;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.FontUtils;

import java.util.List;


public class Caliber {
    private final ResourceLocation name;
    public float baseDamage;
    public float minDamage;
    public float effectiveRange;
    public float speed;

    public Caliber(ResourceLocation name, float baseDamage, float minDamage, float effectiveRange, float speed) {
        this.name = name;
        this.baseDamage = baseDamage;
        this.minDamage = minDamage;
        this.effectiveRange = effectiveRange;
        this.speed = speed;
    }

    public final ResourceLocation getName() {
        return name;
    }

    public void fireBullet(IAmmunition ammunition, ItemStack ammunitionStack, IGun gun, Player player, ItemStack gunStack, float spread) {
        Level level = player.level();
        level.addFreshEntity(defaultProjectile(player, level, spread, gun));
    }

    public void handleTooltip(ItemStack stack, IGun gun, Level levelIn, List<Component> tooltip, TooltipFlag flagIn, boolean detail) {
        tooltip.add(FontUtils.dataTip("tooltip.gun_info.damage", baseDamage, 35, 1));
        if (detail) {
            tooltip.add(FontUtils.dataTip("tooltip.gun_info.effective_range", effectiveRange, 160, 16));
            tooltip.add(FontUtils.dataTip("tooltip.gun_info.bullet_speed", speed, 12, 1, "gcaa.unit.chunk_pre_second"));
        }
    }

    protected Projectile defaultProjectile(Player player, Level level, float spread, IGun gun) {
        float damage = (float) (baseDamage * (Math.round((Math.random() - 0.5) * 20) * 0.01 + 1));
        return new Projectile(
                ModEntities.PROJECTILE.get(), level, player,
                player.getLookAngle(), speed, spread, damage,
                minDamage, 0, effectiveRange, 0, gun);
    }
}