package io.github.ott2006.dungeons2.world;

import io.github.ott2006.dungeons2.Dungeons2;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

public final class ModDimensions {
    public static final ResourceKey<LevelStem> SIFT_STEM = ResourceKey.create(Registries.LEVEL_STEM, Dungeons2.id("the_sift"));
    public static final ResourceKey<Level> SIFT = ResourceKey.create(Registries.DIMENSION, Dungeons2.id("the_sift"));
    public static final ResourceKey<DimensionType> SIFT_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, Dungeons2.id("the_sift"));

    private ModDimensions() {
    }

    public static boolean isSift(Level level) {
        return level.dimension() == SIFT;
    }
}
