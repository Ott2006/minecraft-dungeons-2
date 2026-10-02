package io.github.ott2006.dungeons2.world.gen;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.registry.ModBlocks;
import io.github.ott2006.dungeons2.registry.ModFeatures;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> WHITE_WILLOW = key("white_willow");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LARGE_WHITE_WILLOW = key("large_white_willow");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WHITE_WILLOWS = key("white_willows");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORANGE_SCULK_GRASS_PATCH = key("orange_sculk_grass_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GREEN_SCULK_GRASS_PATCH = key("green_sculk_grass_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_SCULK_GRASS_PATCH = key("tall_sculk_grass_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GREEN_TALL_SCULK_GRASS_PATCH = key("green_tall_sculk_grass_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEAD_BUSH_PATCH = key("carapace_dead_bush_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ICHOR_LAKE = key("ichor_lake");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SIFTSTONE_ROCK = key("siftstone_rock");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BONE_ARCH = key("bone_arch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DORMANT_ECHO_GOLEM = key("dormant_echo_golem");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SIFTSTONE_COAL_ORE = key("siftstone_coal_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SIFTSTONE_IRON_ORE = key("siftstone_iron_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SIFTSTONE_COPPER_ORE = key("siftstone_copper_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SIFTSTONE_GOLD_ORE = key("siftstone_gold_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SIFTSTONE_DIAMOND_ORE = key("siftstone_diamond_ore");

    private ModConfiguredFeatures() {
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Dungeons2.id(name));
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);

        BlockState log = ModBlocks.WHITE_WILLOW_LOG.get().defaultBlockState();
        BlockState leaves = ModBlocks.WHITE_WILLOW_LEAVES.get().defaultBlockState();
        BlockState weeping = ModBlocks.WEEPING_WHITE_WILLOW.get().defaultBlockState();
        BlockState soil = ModBlocks.GREEN_HEALTHY_SCULK.get().defaultBlockState();

        register(context, WHITE_WILLOW, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(log),
                new StraightTrunkPlacer(5, 2, 1),
                BlockStateProvider.simple(leaves),
                new BlobFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1))
                .dirt(BlockStateProvider.simple(soil))
                .decorators(List.of(new AttachedToLeavesDecorator(0.45F, 1, 0, BlockStateProvider.simple(weeping), 2, List.of(Direction.DOWN))))
                .ignoreVines()
                .build());

        register(context, LARGE_WHITE_WILLOW, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(log),
                new FancyTrunkPlacer(4, 9, 0),
                BlockStateProvider.simple(leaves),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))
                .dirt(BlockStateProvider.simple(soil))
                .decorators(List.of(new AttachedToLeavesDecorator(0.5F, 1, 0, BlockStateProvider.simple(weeping), 2, List.of(Direction.DOWN))))
                .ignoreVines()
                .build());

        register(context, WHITE_WILLOWS, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(new WeightedPlacedFeature(PlacementUtils.inlinePlaced(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(LARGE_WHITE_WILLOW),
                        PlacementUtils.filteredByBlockSurvival(ModBlocks.WHITE_WILLOW_SAPLING.get())), 0.3F)),
                PlacementUtils.inlinePlaced(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(WHITE_WILLOW),
                        PlacementUtils.filteredByBlockSurvival(ModBlocks.WHITE_WILLOW_SAPLING.get()))));

        register(context, ORANGE_SCULK_GRASS_PATCH, Feature.RANDOM_PATCH, plantPatch(48, new WeightedStateProvider(
                SimpleWeightedRandomList.<BlockState>builder()
                        .add(ModBlocks.ORANGE_SHORT_SCULK_GRASS.get().defaultBlockState(), 3)
                        .add(ModBlocks.LIGHT_ORANGE_SHORT_SCULK_GRASS.get().defaultBlockState(), 2))));
        register(context, GREEN_SCULK_GRASS_PATCH, Feature.RANDOM_PATCH,
                plantPatch(48, BlockStateProvider.simple(ModBlocks.GREEN_SHORT_SCULK_GRASS.get())));
        register(context, TALL_SCULK_GRASS_PATCH, Feature.RANDOM_PATCH,
                plantPatch(24, BlockStateProvider.simple(ModBlocks.TALL_SCULK_GRASS.get())));
        register(context, GREEN_TALL_SCULK_GRASS_PATCH, Feature.RANDOM_PATCH,
                plantPatch(24, BlockStateProvider.simple(ModBlocks.GREEN_TALL_SCULK_GRASS.get())));
        register(context, DEAD_BUSH_PATCH, Feature.RANDOM_PATCH,
                plantPatch(6, BlockStateProvider.simple(Blocks.DEAD_BUSH)));

        register(context, ICHOR_LAKE, Feature.LAKE, new LakeFeature.Configuration(
                BlockStateProvider.simple(ModBlocks.ICHOR.get().defaultBlockState()),
                BlockStateProvider.simple(ModBlocks.SIFTSTONE.get().defaultBlockState())));
        register(context, SIFTSTONE_ROCK, Feature.FOREST_ROCK, new BlockStateConfiguration(ModBlocks.COBBLED_SIFTSTONE.get().defaultBlockState()));
        register(context, BONE_ARCH, ModFeatures.BONE_ARCH.get(), NoneFeatureConfiguration.INSTANCE);
        register(context, DORMANT_ECHO_GOLEM, ModFeatures.DORMANT_ECHO_GOLEM.get(), NoneFeatureConfiguration.INSTANCE);

        RuleTest siftstone = new BlockMatchTest(ModBlocks.SIFTSTONE.get());
        register(context, SIFTSTONE_COAL_ORE, Feature.ORE, new OreConfiguration(siftstone, ModBlocks.SIFTSTONE_COAL_ORE.get().defaultBlockState(), 17));
        register(context, SIFTSTONE_IRON_ORE, Feature.ORE, new OreConfiguration(siftstone, ModBlocks.SIFTSTONE_IRON_ORE.get().defaultBlockState(), 9));
        register(context, SIFTSTONE_COPPER_ORE, Feature.ORE, new OreConfiguration(siftstone, ModBlocks.SIFTSTONE_COPPER_ORE.get().defaultBlockState(), 10));
        register(context, SIFTSTONE_GOLD_ORE, Feature.ORE, new OreConfiguration(siftstone, ModBlocks.SIFTSTONE_GOLD_ORE.get().defaultBlockState(), 9));
        register(context, SIFTSTONE_DIAMOND_ORE, Feature.ORE, new OreConfiguration(siftstone, ModBlocks.SIFTSTONE_DIAMOND_ORE.get().defaultBlockState(), 6, 0.5F));
    }

    private static RandomPatchConfiguration plantPatch(int tries, BlockStateProvider provider) {
        return FeatureUtils.simpleRandomPatchConfiguration(tries, PlacementUtils.filtered(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(provider),
                BlockPredicate.allOf(BlockPredicate.replaceable(), BlockPredicate.noFluid())));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                        ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }
}
