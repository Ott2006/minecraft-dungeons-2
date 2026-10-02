package io.github.ott2006.dungeons2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Soul blocks power the Echo Golems of the Sift. Singers turn them into new golems, and dormant golems can be
 * awakened with one.
 */
public class SoulBlock extends Block {
    public SoulBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.SCULK_SOUL, pos.getX() + random.nextDouble(), pos.getY() + 1.05,
                    pos.getZ() + random.nextDouble(), 0.0, 0.03, 0.0);
        }
    }
}
