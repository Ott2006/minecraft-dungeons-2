package io.github.ott2006.dungeons2.datagen;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.registry.ModDamageTypes;
import io.github.ott2006.dungeons2.world.gen.ModBiomes;
import io.github.ott2006.dungeons2.world.gen.ModCarvers;
import io.github.ott2006.dungeons2.world.gen.ModConfiguredFeatures;
import io.github.ott2006.dungeons2.world.gen.ModPlacedFeatures;
import io.github.ott2006.dungeons2.world.gen.SiftDimensionData;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/** Runs all data generators of the mod ({@code ./gradlew runData}). */
public final class DataGenerators {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DIMENSION_TYPE, SiftDimensionData::bootstrapType)
            .add(Registries.CONFIGURED_CARVER, ModCarvers::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(Registries.BIOME, ModBiomes::bootstrap)
            .add(Registries.NOISE_SETTINGS, SiftDimensionData::bootstrapNoise)
            .add(Registries.LEVEL_STEM, SiftDimensionData::bootstrapStem)
            .add(Registries.DAMAGE_TYPE, ModDamageTypes::bootstrap);

    private DataGenerators() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(DataGenerators::gather);
    }

    private static void gather(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper files = event.getExistingFileHelper();

        DatapackBuiltinEntriesProvider datapack = new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), BUILDER,
                Set.of(Dungeons2.MOD_ID));
        generator.addProvider(event.includeServer(), datapack);
        CompletableFuture<HolderLookup.Provider> lookup = datapack.getRegistryProvider();

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, files));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, files));
        generator.addProvider(event.includeClient(), new ModLanguageProvider.English(output));
        generator.addProvider(event.includeClient(), new ModLanguageProvider.German(output));

        ModTagProviders.Blocks blockTags = new ModTagProviders.Blocks(output, lookup, files);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new ModTagProviders.Items(output, lookup, blockTags.contentsGetter(), files));
        generator.addProvider(event.includeServer(), new ModTagProviders.EntityTypes(output, lookup, files));
        generator.addProvider(event.includeServer(), new ModTagProviders.Biomes(output, lookup, files));
        generator.addProvider(event.includeServer(), new ModTagProviders.DamageTypes(output, lookup, files));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(output, lookup));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(ModLootTables.BlockLoot::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(ModLootTables.EntityLoot::new, LootContextParamSets.ENTITY)), lookup));
    }
}
