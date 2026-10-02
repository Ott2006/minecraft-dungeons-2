package io.github.ott2006.dungeons2.world;

import io.github.ott2006.dungeons2.world.gen.ModConfiguredFeatures;
import java.util.Optional;
import net.minecraft.world.level.block.grower.TreeGrower;

public final class ModTreeGrowers {
    public static final TreeGrower WHITE_WILLOW = new TreeGrower("dungeons2:white_willow", 0.15F,
            Optional.empty(), Optional.empty(),
            Optional.of(ModConfiguredFeatures.WHITE_WILLOW), Optional.of(ModConfiguredFeatures.LARGE_WHITE_WILLOW),
            Optional.empty(), Optional.empty());

    private ModTreeGrowers() {
    }
}
