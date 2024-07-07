package sheridan.gcaa.items.guns;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.entities.projectiles.effects.IBulletEffectProcessor;
import sheridan.gcaa.items.ammunitions.IAmmunition;

import java.util.List;

public interface ICaliber {
    String getName();
    void fireBullet(IAmmunition ammunition, ItemStack ammunitionStack, IGun gun, Player player, ItemStack gunStack);
    int getCost();
}
