package sheridan.gcaa.items.ammunition;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Vector4i;
import sheridan.gcaa.common.server.projetile.Projectile;
import sheridan.gcaa.common.server.projetile.ProjectileHandler;
import sheridan.gcaa.items.gun.IGun;

public interface IAmmunitionMod {
    int defaultCost();
    int getCostFor(IAmmunition ammunition);
    /*
    *The id returned by this method must be a ResourceLocation object with unique content; if the id is duplicate, this mod overrides the previous one
    * */
    ResourceLocation getId();

    ResourceLocation getIconTexture();
    Vector4i getIconUV();

    int getThemeColor();

    String getDescriptionId();

    Component getSpecialDescription();

    void onModifyAmmunition(IAmmunition ammunition, CompoundTag dataRateTag);

    default void onShootInServer(Projectile projectile, IGun gun) {}
    default void onHitEntity(Projectile projectile, Entity entity, boolean isHeadSHot, IGun gun, ProjectileHandler.AmmunitionDataCache cache) {}
    default void onHitBlockServer(Projectile projectile, BlockHitResult hitResult, BlockState blockState) {}

    default void onShootInOwnClient(IGun gun, Player shooter) {}
    default void onHitBlockClient() {}

    default boolean syncClientHooks() {
        return false;
    }
}
