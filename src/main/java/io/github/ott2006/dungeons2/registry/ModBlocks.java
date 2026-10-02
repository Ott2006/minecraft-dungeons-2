package io.github.ott2006.dungeons2.registry;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.block.IchorBlock;
import io.github.ott2006.dungeons2.block.ResonantDeepslateBlock;
import io.github.ott2006.dungeons2.block.SculkGrassBlock;
import io.github.ott2006.dungeons2.block.SiftGrassPlantBlock;
import io.github.ott2006.dungeons2.block.SiftPortalBlock;
import io.github.ott2006.dungeons2.block.SoulBlock;
import io.github.ott2006.dungeons2.block.SpectralBarrierBlock;
import io.github.ott2006.dungeons2.block.StrippableLogBlock;
import io.github.ott2006.dungeons2.block.WeepingWillowBlock;
import io.github.ott2006.dungeons2.world.ModTreeGrowers;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Dungeons2.MOD_ID);

    /** Every block that has an item, in creative tab order. */
    public static final List<DeferredBlock<?>> BLOCKS_WITH_ITEMS = new ArrayList<>();

    // ------------------------------------------------------------------ siftstone
    public static final DeferredBlock<Block> SIFTSTONE = withItem("siftstone",
            () -> new Block(stone(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<Block> COBBLED_SIFTSTONE = withItem("cobbled_siftstone",
            () -> new Block(stone(MapColor.TERRACOTTA_RED).strength(2.0F, 6.0F)));
    public static final DeferredBlock<StairBlock> COBBLED_SIFTSTONE_STAIRS = withItem("cobbled_siftstone_stairs",
            () -> new StairBlock(COBBLED_SIFTSTONE.get().defaultBlockState(), stone(MapColor.TERRACOTTA_RED).strength(2.0F, 6.0F)));
    public static final DeferredBlock<SlabBlock> COBBLED_SIFTSTONE_SLAB = withItem("cobbled_siftstone_slab",
            () -> new SlabBlock(stone(MapColor.TERRACOTTA_RED).strength(2.0F, 6.0F)));
    public static final DeferredBlock<WallBlock> COBBLED_SIFTSTONE_WALL = withItem("cobbled_siftstone_wall",
            () -> new WallBlock(stone(MapColor.TERRACOTTA_RED).strength(2.0F, 6.0F).forceSolidOn()));
    public static final DeferredBlock<Block> POLISHED_SIFTSTONE = withItem("polished_siftstone",
            () -> new Block(stone(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<StairBlock> POLISHED_SIFTSTONE_STAIRS = withItem("polished_siftstone_stairs",
            () -> new StairBlock(POLISHED_SIFTSTONE.get().defaultBlockState(), stone(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<SlabBlock> POLISHED_SIFTSTONE_SLAB = withItem("polished_siftstone_slab",
            () -> new SlabBlock(stone(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<WallBlock> POLISHED_SIFTSTONE_WALL = withItem("polished_siftstone_wall",
            () -> new WallBlock(stone(MapColor.TERRACOTTA_RED).forceSolidOn()));
    public static final DeferredBlock<Block> SIFTSTONE_BRICKS = withItem("siftstone_bricks",
            () -> new Block(stone(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<StairBlock> SIFTSTONE_BRICK_STAIRS = withItem("siftstone_brick_stairs",
            () -> new StairBlock(SIFTSTONE_BRICKS.get().defaultBlockState(), stone(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<SlabBlock> SIFTSTONE_BRICK_SLAB = withItem("siftstone_brick_slab",
            () -> new SlabBlock(stone(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<WallBlock> SIFTSTONE_BRICK_WALL = withItem("siftstone_brick_wall",
            () -> new WallBlock(stone(MapColor.TERRACOTTA_RED).forceSolidOn()));
    public static final DeferredBlock<Block> CHISELED_SIFTSTONE = withItem("chiseled_siftstone",
            () -> new Block(stone(MapColor.TERRACOTTA_RED)));

    public static final DeferredBlock<DropExperienceBlock> SIFTSTONE_COAL_ORE = withItem("siftstone_coal_ore",
            () -> new DropExperienceBlock(UniformInt.of(0, 2), stone(MapColor.TERRACOTTA_RED).strength(3.0F, 3.0F)));
    public static final DeferredBlock<DropExperienceBlock> SIFTSTONE_IRON_ORE = withItem("siftstone_iron_ore",
            () -> new DropExperienceBlock(ConstantInt.of(0), stone(MapColor.TERRACOTTA_RED).strength(3.0F, 3.0F)));
    public static final DeferredBlock<DropExperienceBlock> SIFTSTONE_COPPER_ORE = withItem("siftstone_copper_ore",
            () -> new DropExperienceBlock(ConstantInt.of(0), stone(MapColor.TERRACOTTA_RED).strength(3.0F, 3.0F)));
    public static final DeferredBlock<DropExperienceBlock> SIFTSTONE_GOLD_ORE = withItem("siftstone_gold_ore",
            () -> new DropExperienceBlock(ConstantInt.of(0), stone(MapColor.TERRACOTTA_RED).strength(3.0F, 3.0F)));
    public static final DeferredBlock<DropExperienceBlock> SIFTSTONE_DIAMOND_ORE = withItem("siftstone_diamond_ore",
            () -> new DropExperienceBlock(UniformInt.of(3, 7), stone(MapColor.TERRACOTTA_RED).strength(3.0F, 3.0F)));

    // ------------------------------------------------------------------ the carapace
    public static final DeferredBlock<ColoredFallingBlock> CARAPACE_SAND = withItem("carapace_sand",
            () -> new ColoredFallingBlock(new ColorRGBA(0xCDB48CFF), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND)));
    public static final DeferredBlock<Block> CARAPACE_SANDSTONE = withItem("carapace_sandstone",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops().strength(0.8F)));

    // ------------------------------------------------------------------ healthy sculk
    public static final DeferredBlock<Block> GREEN_HEALTHY_SCULK = withItem("green_healthy_sculk",
            () -> new Block(soil(MapColor.COLOR_CYAN)));
    public static final DeferredBlock<Block> ORANGE_HEALTHY_SCULK = withItem("orange_healthy_sculk",
            () -> new Block(soil(MapColor.TERRACOTTA_BROWN)));
    public static final DeferredBlock<SculkGrassBlock> ORANGE_SCULK_GRASS_BLOCK = withItem("orange_sculk_grass_block",
            () -> new SculkGrassBlock(ORANGE_HEALTHY_SCULK, () -> ModBlocks.ORANGE_SHORT_SCULK_GRASS.get(),
                    soil(MapColor.TERRACOTTA_ORANGE).randomTicks()));
    public static final DeferredBlock<SculkGrassBlock> LIGHT_ORANGE_SCULK_GRASS_BLOCK = withItem("light_orange_sculk_grass_block",
            () -> new SculkGrassBlock(ORANGE_HEALTHY_SCULK, () -> ModBlocks.LIGHT_ORANGE_SHORT_SCULK_GRASS.get(),
                    soil(MapColor.COLOR_PINK).randomTicks()));
    public static final DeferredBlock<SculkGrassBlock> GREEN_SCULK_GRASS_BLOCK = withItem("green_sculk_grass_block",
            () -> new SculkGrassBlock(GREEN_HEALTHY_SCULK, () -> ModBlocks.GREEN_SHORT_SCULK_GRASS.get(),
                    soil(MapColor.COLOR_GREEN).randomTicks()));

    public static final DeferredBlock<DoublePlantBlock> TALL_SCULK_GRASS = withItem("tall_sculk_grass",
            () -> new DoublePlantBlock(plant(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<DoublePlantBlock> GREEN_TALL_SCULK_GRASS = withItem("green_tall_sculk_grass",
            () -> new DoublePlantBlock(plant(MapColor.COLOR_GREEN)));
    public static final DeferredBlock<SiftGrassPlantBlock> ORANGE_SHORT_SCULK_GRASS = withItem("orange_short_sculk_grass",
            () -> new SiftGrassPlantBlock(TALL_SCULK_GRASS, plant(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<SiftGrassPlantBlock> LIGHT_ORANGE_SHORT_SCULK_GRASS = withItem("light_orange_short_sculk_grass",
            () -> new SiftGrassPlantBlock(TALL_SCULK_GRASS, plant(MapColor.COLOR_PINK)));
    public static final DeferredBlock<SiftGrassPlantBlock> GREEN_SHORT_SCULK_GRASS = withItem("green_short_sculk_grass",
            () -> new SiftGrassPlantBlock(GREEN_TALL_SCULK_GRASS, plant(MapColor.COLOR_GREEN)));

    // ------------------------------------------------------------------ white willow
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_WHITE_WILLOW_LOG = withItem("stripped_white_willow_log",
            () -> new RotatedPillarBlock(wood(MapColor.QUARTZ)));
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_WHITE_WILLOW_WOOD = withItem("stripped_white_willow_wood",
            () -> new RotatedPillarBlock(wood(MapColor.QUARTZ)));
    public static final DeferredBlock<StrippableLogBlock> WHITE_WILLOW_LOG = withItem("white_willow_log",
            () -> new StrippableLogBlock(STRIPPED_WHITE_WILLOW_LOG, wood(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<StrippableLogBlock> WHITE_WILLOW_WOOD = withItem("white_willow_wood",
            () -> new StrippableLogBlock(STRIPPED_WHITE_WILLOW_WOOD, wood(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<Block> WHITE_WILLOW_PLANKS = withItem("white_willow_planks",
            () -> new Block(wood(MapColor.QUARTZ).strength(2.0F, 3.0F)));
    public static final DeferredBlock<StairBlock> WHITE_WILLOW_STAIRS = withItem("white_willow_stairs",
            () -> new StairBlock(WHITE_WILLOW_PLANKS.get().defaultBlockState(), wood(MapColor.QUARTZ).strength(2.0F, 3.0F)));
    public static final DeferredBlock<SlabBlock> WHITE_WILLOW_SLAB = withItem("white_willow_slab",
            () -> new SlabBlock(wood(MapColor.QUARTZ).strength(2.0F, 3.0F)));
    public static final DeferredBlock<FenceBlock> WHITE_WILLOW_FENCE = withItem("white_willow_fence",
            () -> new FenceBlock(wood(MapColor.QUARTZ).strength(2.0F, 3.0F)));
    public static final DeferredBlock<FenceGateBlock> WHITE_WILLOW_FENCE_GATE = withItem("white_willow_fence_gate",
            () -> new FenceGateBlock(WoodType.OAK, wood(MapColor.QUARTZ).strength(2.0F, 3.0F).forceSolidOn()));
    public static final DeferredBlock<DoorBlock> WHITE_WILLOW_DOOR = withItem("white_willow_door",
            () -> new DoorBlock(BlockSetType.OAK, wood(MapColor.QUARTZ).strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<TrapDoorBlock> WHITE_WILLOW_TRAPDOOR = withItem("white_willow_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.OAK, wood(MapColor.QUARTZ).strength(3.0F).noOcclusion()
                    .isValidSpawn((s, l, p, e) -> false)));
    public static final DeferredBlock<ButtonBlock> WHITE_WILLOW_BUTTON = withItem("white_willow_button",
            () -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.of().noCollission().strength(0.5F)
                    .pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<PressurePlateBlock> WHITE_WILLOW_PRESSURE_PLATE = withItem("white_willow_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.OAK, wood(MapColor.QUARTZ).forceSolidOn().noCollission()
                    .strength(0.5F).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<LeavesBlock> WHITE_WILLOW_LEAVES = withItem("white_willow_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.2F).randomTicks()
                    .sound(SoundType.AZALEA_LEAVES).noOcclusion().isValidSpawn((s, l, p, e) -> false)
                    .isSuffocating((s, l, p) -> false).isViewBlocking((s, l, p) -> false).ignitedByLava()
                    .pushReaction(PushReaction.DESTROY).isRedstoneConductor((s, l, p) -> false)));
    public static final DeferredBlock<SaplingBlock> WHITE_WILLOW_SAPLING = withItem("white_willow_sapling",
            () -> new SaplingBlock(ModTreeGrowers.WHITE_WILLOW, BlockBehaviour.Properties.of().mapColor(MapColor.SNOW)
                    .noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<WeepingWillowBlock> WEEPING_WHITE_WILLOW = withItem("weeping_white_willow",
            () -> new WeepingWillowBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).noCollission()
                    .instabreak().sound(SoundType.HANGING_ROOTS).replaceable().pushReaction(PushReaction.DESTROY)));

    // ------------------------------------------------------------------ soul & echo
    public static final DeferredBlock<SoulBlock> SOUL_BLOCK = withItem("soul_block",
            () -> new SoulBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(1.5F, 6.0F)
                    .lightLevel(s -> 10).sound(SoundType.SCULK_CATALYST).emissiveRendering((s, l, p) -> true)));
    public static final DeferredBlock<ResonantDeepslateBlock> RESONANT_DEEPSLATE = withItem("resonant_deepslate",
            () -> new ResonantDeepslateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 1200.0F)
                    .lightLevel(s -> 4).sound(SoundType.DEEPSLATE_TILES)));

    // ------------------------------------------------------------------ technical blocks (no items)
    public static final DeferredBlock<SiftPortalBlock> SIFT_PORTAL = BLOCKS.register("sift_portal",
            () -> new SiftPortalBlock(BlockBehaviour.Properties.of().noCollission().strength(-1.0F).sound(SoundType.GLASS)
                    .lightLevel(s -> 11).pushReaction(PushReaction.BLOCK).noLootTable()));
    public static final DeferredBlock<SpectralBarrierBlock> SPECTRAL_BARRIER = BLOCKS.register("spectral_barrier",
            () -> new SpectralBarrierBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(-1.0F, 3600000.0F)
                    .noOcclusion().noLootTable().lightLevel(s -> 6).sound(SoundType.AMETHYST)
                    .isValidSpawn((s, l, p, e) -> false).isSuffocating((s, l, p) -> false).isViewBlocking((s, l, p) -> false)
                    .pushReaction(PushReaction.BLOCK)));
    public static final DeferredBlock<IchorBlock> ICHOR = BLOCKS.register("ichor",
            () -> new IchorBlock(ModFluids.ICHOR.get(), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED)
                    .replaceable().noCollission().strength(100.0F).lightLevel(s -> 7).pushReaction(PushReaction.DESTROY)
                    .noLootTable().liquid().sound(SoundType.EMPTY)));

    private ModBlocks() {
    }

    private static <B extends Block> DeferredBlock<B> withItem(String name, Supplier<B> block) {
        DeferredBlock<B> holder = BLOCKS.register(name, block);
        ModItems.ITEMS.registerSimpleBlockItem(holder);
        BLOCKS_WITH_ITEMS.add(holder);
        return holder;
    }

    private static BlockBehaviour.Properties stone(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.TUFF);
    }

    private static BlockBehaviour.Properties soil(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).strength(0.6F).sound(SoundType.SCULK);
    }

    private static BlockBehaviour.Properties plant(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).replaceable().noCollission().instabreak()
                .sound(SoundType.SCULK_SENSOR).offsetType(BlockBehaviour.OffsetType.XYZ).ignitedByLava()
                .pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties wood(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).instrument(NoteBlockInstrument.BASS).strength(2.0F)
                .sound(SoundType.WOOD).ignitedByLava();
    }
}
