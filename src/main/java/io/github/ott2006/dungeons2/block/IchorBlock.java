package io.github.ott2006.dungeons2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

/**
 * Ichor: the multicoloured, thick liquid of the Sift. It sets Overworld creatures alight with soul fire, but Sift
 * natives love bathing in it. Items left in ichor are slowly re-forged. The effects are applied in
 * {@link io.github.ott2006.dungeons2.world.IchorHandler}.
 */
public class IchorBlock extends LiquidBlock {
    public IchorBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(40) == 0 && level.getBlockState(pos.above()).isAir()) {
            level.addParticle(random.nextBoolean() ? ParticleTypes.SOUL : ParticleTypes.SOUL_FIRE_FLAME,
                    pos.getX() + random.nextDouble(), pos.getY() + 0.95, pos.getZ() + random.nextDouble(), 0.0, 0.04, 0.0);
        }
    }
}
