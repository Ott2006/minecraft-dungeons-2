package io.github.ott2006.dungeons2.world.gen;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.registry.ModEntities;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.core.particles.ParticleOptions;

/**
 * The biomes of the Sift. A vibrant landscape of red and orange stone, colourful healthy sculk, white willows and
 * pools of ichor, with souls drifting through the teal sky.
 */
public final class ModBiomes {
    public static final ResourceKey<Biome> SINGERS_MEADOW = key("singers_meadow");
    public static final ResourceKey<Biome> LULLABY_HILLS = key("lullaby_hills");
    public static final ResourceKey<Biome> THE_CARAPACE = key("the_carapace");
    public static final ResourceKey<Biome> ICHOR_RAVINES = key("ichor_ravines");

    private ModBiomes() {
    }

    private static ResourceKey<Biome> key(String name) {
        return ResourceKey.create(Registries.BIOME, Dungeons2.id(name));
    }

    public static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);
        context.register(SINGERS_MEADOW, singersMeadow(placed, carvers));
        context.register(LULLABY_HILLS, lullabyHills(placed, carvers));
        context.register(THE_CARAPACE, carapace(placed, carvers));
        context.register(ICHOR_RAVINES, ichorRavines(placed, carvers));
    }

    private static BiomeGenerationSettings.Builder baseGeneration(HolderGetter<PlacedFeature> placed, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder(placed, carvers);
        builder.addCarver(GenerationStep.Carving.AIR, ModCarvers.SIFT_CAVE);
        builder.addCarver(GenerationStep.Carving.AIR, ModCarvers.SIFT_CANYON);
        BiomeDefaultFeatures.addDefaultOres(builder);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.SIFTSTONE_COAL_ORE);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.SIFTSTONE_IRON_ORE);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.SIFTSTONE_COPPER_ORE);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.SIFTSTONE_GOLD_ORE);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.SIFTSTONE_DIAMOND_ORE);
        return builder;
    }

    private static BiomeSpecialEffects effects(int sky, int fog, int grass, ParticleOptions particle, float particleChance, Holder<SoundEvent> music) {
        return new BiomeSpecialEffects.Builder()
                .skyColor(sky)
                .fogColor(fog)
                .waterColor(0x3F76E4)
                .waterFogColor(0x050533)
                .grassColorOverride(grass)
                .foliageColorOverride(grass)
                .ambientParticle(new AmbientParticleSettings(particle, particleChance))
                .ambientLoopSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP)
                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                .backgroundMusic(Musics.createGameMusic(music))
                .build();
    }

    private static Biome biome(BiomeSpecialEffects effects, MobSpawnSettings spawns, BiomeGenerationSettings generation) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.8F)
                .downfall(0.0F)
                .specialEffects(effects)
                .mobSpawnSettings(spawns)
                .generationSettings(generation)
                .build();
    }

    private static MobSpawnSettings.Builder sifterSpawns() {
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SEEDLING.get(), 80, 2, 4));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SENTINEL.get(), 50, 1, 2));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.POLLINATOR.get(), 40, 1, 2));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.NESTER.get(), 40, 1, 2));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SPROUT.get(), 30, 1, 1));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.HARMONIZER.get(), 1, 1, 1));
        return spawns;
    }

    private static Biome singersMeadow(HolderGetter<PlacedFeature> placed, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        MobSpawnSettings.Builder spawns = sifterSpawns();
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.BLUB.get(), 10, 2, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.SINGER.get(), 2, 1, 1));
        spawns.creatureGenerationProbability(0.08F);

        BiomeGenerationSettings.Builder gen = baseGeneration(placed, carvers);
        gen.addFeature(GenerationStep.Decoration.LAKES, ModPlacedFeatures.ICHOR_POOL_COMMON);
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.WHITE_WILLOWS_SPARSE);
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.ORANGE_SCULK_GRASS);
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.TALL_SCULK_GRASS);
        return biome(effects(0x6CC9C4, 0xF2C2AE, 0xC96B58, ParticleTypes.SCULK_SOUL, 0.0012F, SoundEvents.MUSIC_BIOME_CHERRY_GROVE),
                spawns.build(), gen.build());
    }

    private static Biome lullabyHills(HolderGetter<PlacedFeature> placed, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        MobSpawnSettings.Builder spawns = sifterSpawns();
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.BLUB.get(), 8, 2, 3));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.SINGER.get(), 3, 1, 1));
        spawns.creatureGenerationProbability(0.08F);

        BiomeGenerationSettings.Builder gen = baseGeneration(placed, carvers);
        gen.addFeature(GenerationStep.Decoration.LAKES, ModPlacedFeatures.ICHOR_POOL_RARE);
        gen.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, ModPlacedFeatures.DORMANT_ECHO_GOLEM);
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.WHITE_WILLOWS_FOREST);
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.GREEN_SCULK_GRASS);
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.GREEN_TALL_SCULK_GRASS);
        return biome(effects(0x7CC7D6, 0xC9E8DC, 0x4F905C, ParticleTypes.SCULK_SOUL, 0.001F, SoundEvents.MUSIC_BIOME_MEADOW),
                spawns.build(), gen.build());
    }

    private static Biome carapace(HolderGetter<PlacedFeature> placed, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.HUNTER.get(), 50, 1, 2));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCAVENGER.get(), 40, 1, 2));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.STALKER.get(), 40, 1, 2));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.TRAPPER.get(), 30, 1, 1));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SEEDLING.get(), 15, 1, 3));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.BLUB.get(), 6, 1, 3));
        spawns.creatureGenerationProbability(0.05F);

        BiomeGenerationSettings.Builder gen = baseGeneration(placed, carvers);
        gen.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, ModPlacedFeatures.SIFTSTONE_ROCK);
        gen.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, ModPlacedFeatures.BONE_ARCH);
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.CARAPACE_DEAD_BUSH);
        return biome(effects(0x8CC6C0, 0xE6D6B2, 0xBFA97A, ParticleTypes.WHITE_ASH, 0.01F, SoundEvents.MUSIC_BIOME_DESERT),
                spawns.build(), gen.build());
    }

    private static Biome ichorRavines(HolderGetter<PlacedFeature> placed, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SEEDLING.get(), 60, 2, 4));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.POLLINATOR.get(), 40, 1, 2));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.NESTER.get(), 30, 1, 2));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.STALKER.get(), 20, 1, 1));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.DARTBACK.get(), 2, 1, 1));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.BLUB.get(), 12, 2, 4));
        spawns.creatureGenerationProbability(0.08F);

        BiomeGenerationSettings.Builder gen = baseGeneration(placed, carvers);
        gen.addCarver(GenerationStep.Carving.AIR, ModCarvers.ICHOR_RAVINE);
        gen.addFeature(GenerationStep.Decoration.LAKES, ModPlacedFeatures.ICHOR_POOL_COMMON);
        gen.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, ModPlacedFeatures.SIFTSTONE_ROCK);
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.ORANGE_SCULK_GRASS_SPARSE);
        return biome(effects(0x5FB3C8, 0xC98FA2, 0xB85A4A, ParticleTypes.SCULK_SOUL, 0.002F, SoundEvents.MUSIC_BIOME_SOUL_SAND_VALLEY),
                spawns.build(), gen.build());
    }
}
