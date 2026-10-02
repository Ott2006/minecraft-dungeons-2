package io.github.ott2006.dungeons2.world.gen;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.registry.ModBlocks;
import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public final class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> WHITE_WILLOWS_SPARSE = key("white_willows_sparse");
    public static final ResourceKey<PlacedFeature> WHITE_WILLOWS_FOREST = key("white_willows_forest");
    public static final ResourceKey<PlacedFeature> ORANGE_SCULK_GRASS = key("orange_sculk_grass");
    public static final ResourceKey<PlacedFeature> ORANGE_SCULK_GRASS_SPARSE = key("orange_sculk_grass_sparse");
    public static final ResourceKey<PlacedFeature> GREEN_SCULK_GRASS = key("green_sculk_grass");
    public static final ResourceKey<PlacedFeature> TALL_SCULK_GRASS = key("tall_sculk_grass");
    public static final ResourceKey<PlacedFeature> GREEN_TALL_SCULK_GRASS = key("green_tall_sculk_grass");
    public static final ResourceKey<PlacedFeature> CARAPACE_DEAD_BUSH = key("carapace_dead_bush");
    public static final ResourceKey<PlacedFeature> ICHOR_POOL_COMMON = key("ichor_pool_common");
    public static final ResourceKey<PlacedFeature> ICHOR_POOL_RARE = key("ichor_pool_rare");
    public static final ResourceKey<PlacedFeature> SIFTSTONE_ROCK = key("siftstone_rock");
    public static final ResourceKey<PlacedFeature> BONE_ARCH = key("bone_arch");
    public static final ResourceKey<PlacedFeature> DORMANT_ECHO_GOLEM = key("dormant_echo_golem");
    public static final ResourceKey<PlacedFeature> SIFTSTONE_COAL_ORE = key("siftstone_coal_ore");
    public static final ResourceKey<PlacedFeature> SIFTSTONE_IRON_ORE = key("siftstone_iron_ore");
    public static final ResourceKey<PlacedFeature> SIFTSTONE_COPPER_ORE = key("siftstone_copper_ore");
    public static final ResourceKey<PlacedFeature> SIFTSTONE_GOLD_ORE = key("siftstone_gold_ore");
    public static final ResourceKey<PlacedFeature> SIFTSTONE_DIAMOND_ORE = key("siftstone_diamond_ore");

    private ModPlacedFeatures() {
    }

    private static ResourceKey<PlacedFeature> key(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Dungeons2.id(name));
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        PlacementUtils.register(context, WHITE_WILLOWS_SPARSE, configured.getOrThrow(ModConfiguredFeatures.WHITE_WILLOWS),
                VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(3), ModBlocks.WHITE_WILLOW_SAPLING.get()));
        PlacementUtils.register(context, WHITE_WILLOWS_FOREST, configured.getOrThrow(ModConfiguredFeatures.WHITE_WILLOWS),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.5F, 2), ModBlocks.WHITE_WILLOW_SAPLING.get()));

        PlacementUtils.register(context, ORANGE_SCULK_GRASS, configured.getOrThrow(ModConfiguredFeatures.ORANGE_SCULK_GRASS_PATCH), surface(6));
        PlacementUtils.register(context, ORANGE_SCULK_GRASS_SPARSE, configured.getOrThrow(ModConfiguredFeatures.ORANGE_SCULK_GRASS_PATCH), surface(1));
        PlacementUtils.register(context, GREEN_SCULK_GRASS, configured.getOrThrow(ModConfiguredFeatures.GREEN_SCULK_GRASS_PATCH), surface(6));
        PlacementUtils.register(context, TALL_SCULK_GRASS, configured.getOrThrow(ModConfiguredFeatures.TALL_SCULK_GRASS_PATCH), surface(2));
        PlacementUtils.register(context, GREEN_TALL_SCULK_GRASS, configured.getOrThrow(ModConfiguredFeatures.GREEN_TALL_SCULK_GRASS_PATCH), surface(2));
        PlacementUtils.register(context, CARAPACE_DEAD_BUSH, configured.getOrThrow(ModConfiguredFeatures.DEAD_BUSH_PATCH), surface(2));

        PlacementUtils.register(context, ICHOR_POOL_COMMON, configured.getOrThrow(ModConfiguredFeatures.ICHOR_LAKE),
                rare(4, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)));
        PlacementUtils.register(context, ICHOR_POOL_RARE, configured.getOrThrow(ModConfiguredFeatures.ICHOR_LAKE),
                rare(12, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)));
        PlacementUtils.register(context, SIFTSTONE_ROCK, configured.getOrThrow(ModConfiguredFeatures.SIFTSTONE_ROCK),
                rare(3, PlacementUtils.HEIGHTMAP));
        PlacementUtils.register(context, BONE_ARCH, configured.getOrThrow(ModConfiguredFeatures.BONE_ARCH),
                rare(9, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)));
        PlacementUtils.register(context, DORMANT_ECHO_GOLEM, configured.getOrThrow(ModConfiguredFeatures.DORMANT_ECHO_GOLEM),
                rare(40, PlacementUtils.HEIGHTMAP));

        PlacementUtils.register(context, SIFTSTONE_COAL_ORE, configured.getOrThrow(ModConfiguredFeatures.SIFTSTONE_COAL_ORE),
                ore(24, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.top())));
        PlacementUtils.register(context, SIFTSTONE_IRON_ORE, configured.getOrThrow(ModConfiguredFeatures.SIFTSTONE_IRON_ORE),
                ore(14, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112))));
        PlacementUtils.register(context, SIFTSTONE_COPPER_ORE, configured.getOrThrow(ModConfiguredFeatures.SIFTSTONE_COPPER_ORE),
                ore(12, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112))));
        PlacementUtils.register(context, SIFTSTONE_GOLD_ORE, configured.getOrThrow(ModConfiguredFeatures.SIFTSTONE_GOLD_ORE),
                ore(5, HeightRangePlacement.triangle(VerticalAnchor.absolute(-32), VerticalAnchor.absolute(64))));
        PlacementUtils.register(context, SIFTSTONE_DIAMOND_ORE, configured.getOrThrow(ModConfiguredFeatures.SIFTSTONE_DIAMOND_ORE),
                ore(4, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(40))));
    }

    private static List<PlacementModifier> ore(int count, PlacementModifier height) {
        return List.of(CountPlacement.of(count), InSquarePlacement.spread(), height, BiomeFilter.biome());
    }

    private static List<PlacementModifier> surface(int count) {
        return List.of(CountPlacement.of(count), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
    }

    private static List<PlacementModifier> rare(int chance, PlacementModifier heightmap) {
        return List.of(RarityFilter.onAverageOnceEvery(chance), InSquarePlacement.spread(), heightmap, BiomeFilter.biome());
    }
}
