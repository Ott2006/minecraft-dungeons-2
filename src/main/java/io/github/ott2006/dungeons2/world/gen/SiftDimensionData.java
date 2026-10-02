package io.github.ott2006.dungeons2.world.gen;

import com.mojang.datafixers.util.Pair;
import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.registry.ModBlocks;
import io.github.ott2006.dungeons2.world.ModDimensions;
import java.util.List;
import java.util.OptionalLong;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

/**
 * Data-driven definition of the Sift dimension: its dimension type, terrain (noise settings and surface rules) and
 * biome layout.
 */
public final class SiftDimensionData {
    public static final ResourceKey<NoiseGeneratorSettings> SIFT_NOISE = ResourceKey.create(Registries.NOISE_SETTINGS, Dungeons2.id("the_sift"));
    public static final int SEA_LEVEL = 50;

    private SiftDimensionData() {
    }

    public static void bootstrapType(BootstrapContext<DimensionType> context) {
        context.register(ModDimensions.SIFT_TYPE, new DimensionType(
                OptionalLong.empty(),
                true,      // has sky light
                false,     // has ceiling
                false,     // ultra warm
                true,      // natural
                1.0,       // coordinate scale
                true,      // beds work
                false,     // respawn anchors work
                -64,
                384,
                384,
                BlockTags.INFINIBURN_OVERWORLD,
                BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                0.05F,
                new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)));
    }

    public static void bootstrapNoise(BootstrapContext<NoiseGeneratorSettings> context) {
        NoiseGeneratorSettings overworld = NoiseGeneratorSettings.overworld(context, false, false);
        context.register(SIFT_NOISE, new NoiseGeneratorSettings(
                overworld.noiseSettings(),
                ModBlocks.SIFTSTONE.get().defaultBlockState(),
                ModBlocks.ICHOR.get().defaultBlockState(),
                overworld.noiseRouter(),
                surfaceRules(),
                overworld.spawnTarget(),
                SEA_LEVEL,
                false,
                true,
                false,
                false));
    }

    private static SurfaceRules.RuleSource state(BlockState state) {
        return SurfaceRules.state(state);
    }

    private static SurfaceRules.RuleSource surfaceRules() {
        SurfaceRules.RuleSource orangeGrass = state(ModBlocks.ORANGE_SCULK_GRASS_BLOCK.get().defaultBlockState());
        SurfaceRules.RuleSource lightOrangeGrass = state(ModBlocks.LIGHT_ORANGE_SCULK_GRASS_BLOCK.get().defaultBlockState());
        SurfaceRules.RuleSource greenGrass = state(ModBlocks.GREEN_SCULK_GRASS_BLOCK.get().defaultBlockState());
        SurfaceRules.RuleSource orangeSculk = state(ModBlocks.ORANGE_HEALTHY_SCULK.get().defaultBlockState());
        SurfaceRules.RuleSource greenSculk = state(ModBlocks.GREEN_HEALTHY_SCULK.get().defaultBlockState());
        SurfaceRules.RuleSource sand = state(ModBlocks.CARAPACE_SAND.get().defaultBlockState());
        SurfaceRules.RuleSource sandstone = state(ModBlocks.CARAPACE_SANDSTONE.get().defaultBlockState());
        SurfaceRules.RuleSource siftstone = state(ModBlocks.SIFTSTONE.get().defaultBlockState());
        SurfaceRules.RuleSource cobbled = state(ModBlocks.COBBLED_SIFTSTONE.get().defaultBlockState());

        SurfaceRules.ConditionSource dry = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.ConditionSource patchy = SurfaceRules.noiseCondition(Noises.SURFACE, 0.12);
        SurfaceRules.ConditionSource veryPatchy = SurfaceRules.noiseCondition(Noises.PATCH, 0.3);

        SurfaceRules.RuleSource meadow = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(dry,
                        SurfaceRules.sequence(SurfaceRules.ifTrue(patchy, lightOrangeGrass), orangeGrass))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, orangeSculk));

        SurfaceRules.RuleSource hills = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.steep(), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, siftstone)),
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(dry, greenGrass)),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, greenSculk));

        SurfaceRules.RuleSource carapace = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, sandstone),
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, sand),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, sand),
                SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, sandstone));

        SurfaceRules.RuleSource ravines = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(dry,
                        SurfaceRules.sequence(SurfaceRules.ifTrue(veryPatchy, orangeGrass), SurfaceRules.ifTrue(patchy, cobbled), siftstone))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(veryPatchy, orangeSculk)));

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)),
                        state(Blocks.BEDROCK.defaultBlockState())),
                SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.SINGERS_MEADOW), meadow),
                        SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.LULLABY_HILLS), hills),
                        SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.THE_CARAPACE), carapace),
                        SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.ICHOR_RAVINES), ravines))),
                SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)),
                        state(Blocks.DEEPSLATE.defaultBlockState())));
    }

    public static void bootstrapStem(BootstrapContext<LevelStem> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> types = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noise = context.lookup(Registries.NOISE_SETTINGS);

        Climate.Parameter full = Climate.Parameter.span(-1.0F, 1.0F);
        Climate.Parameter depth = Climate.Parameter.span(-1.0F, 2.0F);
        Climate.Parameter inland = Climate.Parameter.span(-0.16F, 1.0F);
        Climate.Parameter lowlands = Climate.Parameter.span(-1.2F, -0.16F);
        Climate.Parameter flat = Climate.Parameter.span(-0.2F, 1.0F);
        Climate.Parameter hilly = Climate.Parameter.span(-1.0F, -0.2F);

        Holder<Biome> meadow = biomes.getOrThrow(ModBiomes.SINGERS_MEADOW);
        Holder<Biome> hills = biomes.getOrThrow(ModBiomes.LULLABY_HILLS);
        Holder<Biome> carapace = biomes.getOrThrow(ModBiomes.THE_CARAPACE);
        Holder<Biome> ravines = biomes.getOrThrow(ModBiomes.ICHOR_RAVINES);

        List<Pair<Climate.ParameterPoint, Holder<Biome>>> points = List.of(
                // ichor ravines: everything that would be ocean in the overworld
                Pair.of(Climate.parameters(full, full, lowlands, full, depth, full, 0.0F), ravines),
                // the carapace: hot areas
                Pair.of(Climate.parameters(Climate.Parameter.span(0.0F, 1.0F), full, inland, flat, depth, full, 0.0F), carapace),
                Pair.of(Climate.parameters(Climate.Parameter.span(0.3F, 1.0F), full, inland, hilly, depth, full, 0.0F), carapace),
                // lullaby hills: the hilly areas
                Pair.of(Climate.parameters(Climate.Parameter.span(-1.0F, 0.3F), full, inland, hilly, depth, full, 0.0F), hills),
                // singer's meadow: everything else
                Pair.of(Climate.parameters(Climate.Parameter.span(-1.0F, 0.0F), full, inland, flat, depth, full, 0.0F), meadow));

        context.register(ModDimensions.SIFT_STEM, new LevelStem(types.getOrThrow(ModDimensions.SIFT_TYPE),
                new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromList(new Climate.ParameterList<>(points)),
                        noise.getOrThrow(SIFT_NOISE))));
    }
}
