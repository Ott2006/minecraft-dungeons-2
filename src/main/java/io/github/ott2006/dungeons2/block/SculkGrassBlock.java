package io.github.ott2006.dungeons2.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The grass of the Sift: a thin layer of colourful sculk grass growing on top of healthy sculk. It spreads onto
 * nearby healthy sculk and withers back into healthy sculk when covered.
 */
public class SculkGrassBlock extends Block implements BonemealableBlock {
    private final Supplier<? extends Block> soil;
    private final Supplier<? extends Block> plant;

    public SculkGrassBlock(Supplier<? extends Block> soil, Supplier<? extends Block> plant, Properties properties) {
        super(properties);
        this.soil = soil;
        this.plant = plant;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(p -> new SculkGrassBlock(this.soil, this.plant, p));
    }

    public Block getSoil() {
        return this.soil.get();
    }

    private static boolean canStayGrass(LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);
        return !aboveState.isFaceSturdy(level, above, Direction.DOWN) && aboveState.getFluidState().isEmpty();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canStayGrass(level, pos)) {
            if (level.isLoaded(pos)) {
                level.setBlockAndUpdate(pos, this.soil.get().defaultBlockState());
            }
            return;
        }
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
            for (int i = 0; i < 4; i++) {
                BlockPos target = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if (level.isLoaded(target) && level.getBlockState(target).is(this.soil.get()) && canStayGrass(level, target)) {
                    level.setBlockAndUpdate(target, this.defaultBlockState());
                }
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockState plantState = this.plant.get().defaultBlockState();
        for (int i = 0; i < 64; i++) {
            BlockPos target = pos.above();
            for (int j = 0; j < i / 16; j++) {
                target = target.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
            }
            target = target.offset(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
            if (level.getBlockState(target).isAir() && level.getBlockState(target.below()).getBlock() instanceof SculkGrassBlock
                    && plantState.canSurvive(level, target)) {
                level.setBlock(target, plantState, 3);
            }
        }
    }
}
