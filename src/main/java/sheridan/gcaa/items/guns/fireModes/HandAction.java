package sheridan.gcaa.items.guns.fireModes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.guns.IGun;
import sheridan.gcaa.items.guns.IGunFireMode;
import sheridan.gcaa.items.guns.IHandActionGun;

public class HandAction implements IGunFireMode {
    @Override
    public String getName() {
        return "hand_action";
    }

    @Override
    public boolean canFire(Player player, ItemStack itemStack, IGun gun) {
        if (gun.getGun() instanceof IHandActionGun handActionGun) {
            return gun.getAmmoLeft(itemStack) > 0 && !handActionGun.needHandAction(itemStack);
        }
        return false;
    }

    @Override
    public void clientShoot(Player player, ItemStack itemStack, IGun gun) {

    }

    @Override
    public void shoot(Player player, ItemStack itemStack, IGun gun) {

    }

    @Override
    public Component getTooltipName() {
        return Component.translatable("tooltip.gunscraft.hand_action");
    }
}
