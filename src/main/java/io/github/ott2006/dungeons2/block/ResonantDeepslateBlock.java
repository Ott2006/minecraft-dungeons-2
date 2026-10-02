package io.github.ott2006.dungeons2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Deepslate infused with echo shards. It forms the frame of a Deep Dark portal to the Sift, just like the reinforced
 * deepslate frame found in ancient cities.
 */
public class ResonantDeepslateBlock extends Block {
    public ResonantDeepslateBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(12) != 0) {
            return;
        }
        Direction dir = Direction.getRandom(random);
        BlockPos side = pos.relative(dir);
        if (level.getBlockState(side).isSolidRender(level, side)) {
            return;
        }
        double x = pos.getX() + 0.5 + dir.getStepX() * 0.55 + (dir.getStepX() == 0 ? random.nextDouble() - 0.5 : 0);
        double y = pos.getY() + 0.5 + dir.getStepY() * 0.55 + (dir.getStepY() == 0 ? random.nextDouble() - 0.5 : 0);
        double z = pos.getZ() + 0.5 + dir.getStepZ() * 0.55 + (dir.getStepZ() == 0 ? random.nextDouble() - 0.5 : 0);
        level.addParticle(ParticleTypes.SCULK_CHARGE_POP, x, y, z, 0.0, 0.01, 0.0);
    }
}
