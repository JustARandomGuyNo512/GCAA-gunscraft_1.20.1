package sheridan.gcaa.blocks;

import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AirLightBlock extends AirBlock {
    public AirLightBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState pState) {
        return false;
    }
}
