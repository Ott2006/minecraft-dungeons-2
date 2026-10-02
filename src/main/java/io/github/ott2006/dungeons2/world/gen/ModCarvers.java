package io.github.ott2006.dungeons2.world.gen;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.registry.ModTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

public final class ModCarvers {
    public static final ResourceKey<ConfiguredWorldCarver<?>> SIFT_CAVE = key("sift_cave");
    public static final ResourceKey<ConfiguredWorldCarver<?>> SIFT_CANYON = key("sift_canyon");
    public static final ResourceKey<ConfiguredWorldCarver<?>> ICHOR_RAVINE = key("ichor_ravine");

    private ModCarvers() {
    }

    private static ResourceKey<ConfiguredWorldCarver<?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_CARVER, Dungeons2.id(name));
    }

    public static void bootstrap(BootstrapContext<ConfiguredWorldCarver<?>> context) {
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
        context.register(SIFT_CAVE, WorldCarver.CAVE.configured(new CaveCarverConfiguration(
                0.14F,
                UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)),
                UniformFloat.of(0.1F, 0.9F),
                VerticalAnchor.aboveBottom(8),
                CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()),
                blocks.getOrThrow(ModTags.Blocks.SIFT_CARVER_REPLACEABLES),
                UniformFloat.of(0.7F, 1.4F),
                UniformFloat.of(0.8F, 1.3F),
                UniformFloat.of(-1.0F, -0.4F))));
        context.register(SIFT_CANYON, WorldCarver.CANYON.configured(canyon(blocks, 0.015F, 3.0F)));
        context.register(ICHOR_RAVINE, WorldCarver.CANYON.configured(canyon(blocks, 0.09F, 4.0F)));
    }

    private static CanyonCarverConfiguration canyon(HolderGetter<Block> blocks, float probability, float yScale) {
        return new CanyonCarverConfiguration(
                probability,
                UniformHeight.of(VerticalAnchor.absolute(10), VerticalAnchor.absolute(72)),
                ConstantFloat.of(yScale),
                VerticalAnchor.aboveBottom(8),
                CarverDebugSettings.of(false, Blocks.WARPED_BUTTON.defaultBlockState()),
                blocks.getOrThrow(ModTags.Blocks.SIFT_CARVER_REPLACEABLES),
                UniformFloat.of(-0.125F, 0.125F),
                new CanyonCarverConfiguration.CanyonShapeConfiguration(
                        UniformFloat.of(0.75F, 1.0F), TrapezoidFloat.of(0.0F, 6.0F, 2.0F), 3, UniformFloat.of(0.75F, 1.0F), 1.0F, 0.0F));
    }
}
