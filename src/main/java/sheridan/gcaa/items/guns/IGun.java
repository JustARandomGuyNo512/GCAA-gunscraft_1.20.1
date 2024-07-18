package sheridan.gcaa.items.guns;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.client.IReloadingTask;
import sheridan.gcaa.items.GunProperties;

public interface IGun {
    GunProperties getGunProperties();
    @Deprecated
    default boolean canHoldInOneHand() {return false;}
    Gun getGun();
    int getAmmoLeft(ItemStack stack);
    void setAmmoLeft(ItemStack stack, int ammoLeft);
    int getMagSize(ItemStack stack);
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
    boolean isSniper();
    boolean isPistol();
    float getRecoilPitch(ItemStack stack);
    float getRecoilYaw(ItemStack stack);
    float getRecoilPitchControl(ItemStack stack);
    float getRecoilYawControl(ItemStack stack);
    float getWeight(ItemStack stack);
    float[] getSpread(ItemStack stack);
    boolean clientReload(ItemStack stack, Player player);
    void reload(ItemStack stack, Player player);
    int getReloadLength(ItemStack stack, boolean fullReload);
    IReloadingTask getReloadingTask(ItemStack stack);
    int getInnerVersion(ItemStack stack);
    default int applySprintingPoseDelay() {
        return 1000;
    }
    default boolean allowShootWhileReloading() {return false;}
}
