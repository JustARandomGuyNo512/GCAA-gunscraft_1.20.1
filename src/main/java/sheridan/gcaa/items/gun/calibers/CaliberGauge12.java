package sheridan.gcaa.items.gun.calibers;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import sheridan.gcaa.items.ammunitions.IAmmunition;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.common.server.projetile.ProjectileHandler;

import java.util.List;

public class CaliberGauge12 extends Caliber {
    public int projectileNum;

    public CaliberGauge12(ResourceLocation name, float baseDamage, float minDamage, float effectiveRange, float speed, int projectileNum) {
        super(name, baseDamage, minDamage, effectiveRange, speed);
        this.projectileNum = projectileNum;
    }

    @Override
    public void fireBullet(IAmmunition ammunition, ItemStack ammunitionStack, IGun gun, Player player, ItemStack gunStack, float spread) {
        for (int i = 0; i < projectileNum; i ++) {
            ProjectileHandler.fire(player, speed, baseDamage, spread * 1.5f, effectiveRange, gun);
        }
    }

    @Override
    public void handleTooltip(ItemStack stack, IGun gun, Level levelIn, List<Component> tooltip, TooltipFlag flagIn, boolean detail) {
        super.handleTooltip(stack, gun, levelIn, tooltip, flagIn, detail);
    }
}
