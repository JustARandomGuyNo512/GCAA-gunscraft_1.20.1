package sheridan.gcaa.blocks.industrial;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.entities.industrial.BulletCraftingBlockEntity;


public class BulletCrafting extends BaseEntityBlock{

    public BulletCrafting(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            this.openContainer(pLevel, pPos, pPlayer);
            return InteractionResult.CONSUME;
        }
    }
    protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) {
        BlockEntity entity = pLevel.getBlockEntity(pPos);
        if (entity instanceof BulletCraftingBlockEntity) {
            pPlayer.openMenu((MenuProvider) entity);
//            pPlayer.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new BulletCraftingBlockEntity(pPos, pState);
    }
}

