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
import sheridan.gcaa.blocks.industrial.BulletCrafting;

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
                            .lightLevel(value -> 10)
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
    /** 弹药制造台注册 */
    public static final RegistryObject<Block> BULLET_CRAFTING = BLOCKS.register(
            "bullet_crafting_table", () -> new BulletCrafting(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .sound(SoundType.METAL)
                            .strength(3.0f)
                            .explosionResistance(180)
                            .lightLevel(value -> 10)
            )
    );
}
