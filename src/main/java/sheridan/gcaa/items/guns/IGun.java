package sheridan.gcaa.items.guns;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.GunProperties;

public interface IGun {
    GunProperties getGunProperties();
    @Deprecated
    default boolean canHoldInOneHand() {return false;}
    Gun getGun();
    int getAmmoLeft(ItemStack stack);
    void clientShoot(ItemStack stack, Player player);
    void shoot(ItemStack stack, Player player);
    IGunFireMode getFireMode(ItemStack stack);
    int getBurstCount();
    ICaliber getCaliber();
}
