package sheridan.gcaa.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
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
                            .strength(1.0f)
                            .explosionResistance(180)
                            .lightLevel(value -> 10)
            )
    );
    public static final RegistryObject<Block> ORE_LEAD = BLOCKS.register(
            "ore_lead", () -> new Block(
                    BlockBehaviour.Properties.copy(Blocks.IRON_ORE))
    );
    public static final RegistryObject<Block> LEAD_BLOCK = BLOCKS.register(
            "lead_block", () -> new Block(
                    BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            )
    );
    public static final RegistryObject<Block> ORE_ASPHALT = BLOCKS.register(
            "ore_asphalt", () -> new Block(
                    BlockBehaviour.Properties.copy(Blocks.COAL_ORE)
            )
    );
    public static final RegistryObject<Block> AIR_LIGHT_BLOCK = BLOCKS.register(
            "air_light_block", () -> new AirLightBlock(
                    BlockBehaviour.Properties.of().replaceable().noCollission().noLootTable().air().lightLevel((x) -> 10)
            )
    );
}
