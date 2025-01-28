package sheridan.gcaa.items.gun;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.Commons;
import sheridan.gcaa.client.IReloadTask;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.gun.calibers.Caliber;

public interface IGun {
    GunProperties getGunProperties();
    Gun getGun();
    int getAmmoLeft(ItemStack stack);
    void setAmmoLeft(ItemStack stack, int ammoLeft);
    int getMagSize(ItemStack stack);
    void clientShoot(ItemStack stack, Player player, IGunFireMode fireMode);
    void shoot(ItemStack stack, Player player, IGunFireMode fireMode, float spread);
    IGunFireMode getFireMode(ItemStack stack);
    Caliber getCaliber();
    CompoundTag getPropertiesTag(ItemStack stack);
    ListTag getAttachmentsListTag(ItemStack stack);
    void setAttachmentsListTag(ItemStack stack, ListTag list);
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
    float getWalkingSpreadFactor(ItemStack stack);
    float getSprintingSpreadFactor(ItemStack stack);
    float getShootSpread(ItemStack stack);
    float getSpreadRecover(ItemStack stack);
    float getWeight(ItemStack stack);
    float getAgility(ItemStack stack);
    float getAdsSpeed(ItemStack stack);
    float[] getSpread(ItemStack stack);
    float getFireSoundVol(ItemStack stack);
    boolean clientReload(ItemStack stack, Player player);
    void reload(ItemStack stack, Player player);
    int getReloadLength(ItemStack stack, boolean fullReload);
    IReloadTask getReloadingTask(ItemStack stack, Player player);
    IReloadTask getUnloadingTask(ItemStack stack, Player player);
    long getDate(ItemStack stack);
    void updateDate(ItemStack stack);
    String getAttachmentsModifiedUUID(ItemStack stack);
    String getEffectiveSightUUID(ItemStack stack);
    void setEffectiveSightUUID(ItemStack stack, String uuid);
    void newAttachmentsModifiedUUID(ItemStack stack);
    String getSelectedAmmunitionTypeUUID(ItemStack stack);
    void bindAmmunition(ItemStack gunStack, ItemStack ammunitionStack, IAmmunition ammunition);
    void clearAmmo(ItemStack gunStack, Player player);
    boolean isNotUsingSelectedAmmo(ItemStack itemStack);
    @NotNull
    CompoundTag getAmmunitionData(ItemStack itemStack);
    @Nullable
    CompoundTag getUsingAmmunitionData(ItemStack itemStack);
    CompoundTag getUsingAmmunitionDataRate(ItemStack itemStack, boolean createNew);
    String getIdentity(ItemStack stack);
    GunType getGunType();
    enum GunType {
        PISTOL,
        RIFLE,
        ASSAULT_RIFLE,
        SHOTGUN,
        SMG,
        SNIPER
    }

    default boolean allowShootWhileReloading() {return false;}
    default boolean shouldHandleAds(ItemStack stack) {return true;}
    default boolean isFreeBlot() {
        return false;
    }
    default void beforeGunDataUpdate(Player player, ItemStack stack) {}
    default void afterGunDataUpdate(Player player, ItemStack stack) {}
    default boolean shouldUpdate(ItemStack stack) { return getDate(stack) != Commons.SERVER_START_TIME;}
    default boolean shootCreateBulletShell() {return true;}
    default boolean canUseWithShield() {return false;}
}
