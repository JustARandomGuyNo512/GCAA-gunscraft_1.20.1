package sheridan.gcaa.items.guns;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.GunProperties;

public interface IGun {
    GunProperties getGunProperties();
    @Deprecated
    default boolean canHoldInOneHand() {return false;}
    Gun getGun();
    int getAmmoLeft(ItemStack stack);
    void clientShoot(ItemStack stack, Player player, IGunFireMode fireMode);
    void shoot(ItemStack stack, Player player, IGunFireMode fireMode);
    IGunFireMode getFireMode(ItemStack stack);
    int getBurstCount();
    ICaliber getCaliber();
    CompoundTag getPropertiesTag(ItemStack stack);
    ListTag getAttachmentsListTag(ItemStack stack);
    boolean shouldUpdate(int version);
    void setPropertiesTag(ItemStack stack, CompoundTag tag);
    void switchFireMode(ItemStack stack);
    int getFireDelay(ItemStack stack);
    String getMuzzleFlash(ItemStack stack);
    void setMuzzleFlash(ItemStack stack, String status);
}
