package sheridan.gcaa.items.gun;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IGunFireMode {
    String getName();

    @OnlyIn(Dist.CLIENT)
    boolean canFire(Player player, ItemStack itemStack, IGun gun);

    /**
     * Handle fire in client side
     * */
    @OnlyIn(Dist.CLIENT)
    void clientShoot(Player player, ItemStack itemStack, IGun gun);

    /**
     * Handle fire in server side
     * */
    void shoot(Player player, ItemStack itemStack, IGun gun, float spread);

    Component getTooltipName();
}
