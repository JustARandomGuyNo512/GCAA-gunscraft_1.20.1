package sheridan.gcaa.blocks.industrial;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.entities.industrial.BulletCraftingBlockEntity;


public class BulletCrafting extends  BaseEntityBlock{

    protected BulletCrafting(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BulletCraftingBlockEntity(pPos, pState);
    }
}

