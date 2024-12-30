package sheridan.gcaa.items.gun;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.SprintingHandler;

public interface IGunFireMode {
    String getName();

    @OnlyIn(Dist.CLIENT)
    boolean canFire(Player player, ItemStack itemStack, IGun gun);

    @OnlyIn(Dist.CLIENT)
    default boolean fireInSprinting(Player player, ItemStack itemStack, IGun gun, int sprintingCoolDownSet) {
        SprintingHandler.INSTANCE.exitSprinting(sprintingCoolDownSet);
        return gun.getAmmoLeft(itemStack) > 0 && SprintingHandler.INSTANCE.getSprintingProgress() == 0;
    }

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

    default void onSwitchOff(IGun gun, ItemStack stack) {}
    default void onSwitchOn(IGun gun, ItemStack stack) {}
}
