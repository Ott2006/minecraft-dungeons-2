package io.github.ott2006.dungeons2.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * The large bone structures of the Carapace: the half-buried rib cage of some enormous creature, with a skull at
 * one end.
 */
public class BoneArchFeature extends Feature<NoneFeatureConfiguration> {
    public BoneArchFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        if (!level.getBlockState(origin.below()).isSolid()) {
            return false;
        }
        Direction.Axis spineAxis = random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;
        Direction along = spineAxis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction across = spineAxis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
        Direction.Axis acrossAxis = across.getAxis();

        int length = 10 + random.nextInt(9);
        int halfWidth = 4 + random.nextInt(3);
        int height = 6 + random.nextInt(5);
        BlockPos start = origin.below(2).relative(along, -length / 2);

        // spine
        for (int i = -1; i <= length; i++) {
            this.setBone(level, start.relative(along, i), spineAxis);
        }
        // ribs: arches over the spine, smaller towards both ends
        for (int i = 1; i < length; i += 3) {
            double taper = Math.sin(Math.PI * i / length) * 0.5 + 0.5;
            double w = halfWidth * taper;
            double h = height * taper;
            if (random.nextInt(6) == 0) {
                continue; // a missing rib
            }
            BlockPos ribBase = start.relative(along, i);
            int steps = 24;
            BlockPos last = null;
            for (int s = 0; s <= steps; s++) {
                double angle = Math.PI * s / steps;
                int dx = (int) Math.round(Math.cos(angle) * w);
                int dy = (int) Math.round(Math.sin(angle) * h);
                BlockPos pos = ribBase.relative(across, dx).above(dy);
                if (pos.equals(last)) {
                    continue;
                }
                boolean vertical = Math.abs(Math.sin(angle) * h) < h * 0.7;
                this.setBone(level, pos, vertical ? Direction.Axis.Y : acrossAxis);
                last = pos;
            }
        }
        // skull
        BlockPos skull = start.relative(along, -2).above(1);
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = skull.relative(across, x).above(y).relative(along, z);
                    boolean eye = z == -1 && y == 1 && x != 0;
                    if (!eye) {
                        this.setBone(level, pos, Direction.Axis.Y);
                    } else if (!level.getBlockState(pos).is(BlockTags.FEATURES_CANNOT_REPLACE)) {
                        this.setBlock(level, pos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
        return true;
    }

    private void setBone(WorldGenLevel level, BlockPos pos, Direction.Axis axis) {
        BlockState existing = level.getBlockState(pos);
        if (existing.is(BlockTags.FEATURES_CANNOT_REPLACE) || !existing.getFluidState().isEmpty() || level.isOutsideBuildHeight(pos)) {
            return;
        }
        this.setBlock(level, pos, Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis));
    }
}
