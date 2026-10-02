package io.github.ott2006.dungeons2.world.gen.feature;

import com.mojang.serialization.Codec;
import io.github.ott2006.dungeons2.entity.EchoGolem;
import io.github.ott2006.dungeons2.registry.ModBlocks;
import io.github.ott2006.dungeons2.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A dormant echo golem resting on an old siftstone pedestal in Lullaby Hills, waiting to be awakened with a soul
 * block.
 */
public class DormantEchoGolemFeature extends Feature<NoneFeatureConfiguration> {
    public DormantEchoGolemFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        BlockState below = level.getBlockState(origin.below());
        if (!below.isSolid() || !level.getFluidState(origin).isEmpty()) {
            return false;
        }
        BlockState floor = ModBlocks.POLISHED_SIFTSTONE.get().defaultBlockState();
        BlockState accent = ModBlocks.CHISELED_SIFTSTONE.get().defaultBlockState();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos pos = origin.offset(dx, -1, dz);
                this.setBlock(level, pos, dx == 0 && dz == 0 ? accent : floor);
                for (int dy = 0; dy <= 2; dy++) {
                    BlockPos air = pos.above(dy + 1);
                    if (!level.getBlockState(air).isAir() && level.getBlockState(air).canBeReplaced()) {
                        this.setBlock(level, air, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
        EchoGolem golem = ModEntities.ECHO_GOLEM.get().create(level.getLevel());
        if (golem == null) {
            return true;
        }
        golem.moveTo(origin.getX() + 0.5, origin.getY(), origin.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
        golem.finalizeSpawn(level, level.getCurrentDifficultyAt(origin), MobSpawnType.STRUCTURE, null);
        golem.setDormant(true);
        golem.setPersistenceRequired();
        level.addFreshEntityWithPassengers(golem);
        return true;
    }
}
