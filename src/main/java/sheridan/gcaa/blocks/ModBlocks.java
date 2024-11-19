package sheridan.gcaa.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.GCAA;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GCAA.MODID);

    public static final RegistryObject<Block> AMMUNITION_PROCESSOR = BLOCKS.register(
            "ammunition_processor", () -> new AmmunitionProcessor(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .sound(SoundType.METAL)
                            .strength(1.0f)
                            .explosionResistance(180)
                            .noOcclusion()
            ));

    public static final RegistryObject<Block> VENDING_MACHINE = BLOCKS.register(
            "vending_machine", () -> new VendingMachine(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .sound(SoundType.METAL)
                            .strength(1.0f)
                            .explosionResistance(180)
                            .pushReaction(PushReaction.DESTROY)
            ));

}
