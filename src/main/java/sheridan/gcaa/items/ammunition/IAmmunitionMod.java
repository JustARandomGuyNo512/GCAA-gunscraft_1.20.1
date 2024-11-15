package sheridan.gcaa.items.ammunition;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
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

    @OnlyIn(Dist.CLIENT)
    default void onShootInOwnClient(IGun gun, Player shooter) {}
    @OnlyIn(Dist.CLIENT)
    default void onHitBlockClient(BlockPos pos, Vector3f hitVec, Direction direction, Vector3f normalVec, Player player) {}

    default boolean syncClientHooks() {
        return false;
    }
}
