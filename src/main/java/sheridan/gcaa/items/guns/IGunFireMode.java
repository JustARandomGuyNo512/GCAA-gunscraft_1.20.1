package sheridan.gcaa.items.guns;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IGunFireMode {
    String getName();

    boolean canFire(Player player, ItemStack itemStack, IGun gun);

    /**
     * Handle fire in client side
     * */
    @OnlyIn(Dist.CLIENT)
    void preShoot(Player player, ItemStack itemStack, IGun gun);

    /**
     * Handle fire in server side
     * */
    void shoot(Player player, ItemStack itemStack, IGun gun);

    Component getTooltipName();
}
