package io.github.ott2006.dungeons2.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.IShearable;

/** Short sculk grass of the Sift. Grows into tall sculk grass when bone mealed. */
public class SiftGrassPlantBlock extends BushBlock implements BonemealableBlock, IShearable {
    protected static final VoxelShape SHAPE = BushBlock.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);
    private final Supplier<? extends DoublePlantBlock> tall;

    public SiftGrassPlantBlock(Supplier<? extends DoublePlantBlock> tall, Properties properties) {
        super(properties);
        this.tall = tall;
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return simpleCodec(p -> new SiftGrassPlantBlock(this.tall, p));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockState tallState = this.tall.get().defaultBlockState();
        if (tallState.canSurvive(level, pos) && level.isEmptyBlock(pos.above())) {
            DoublePlantBlock.placeAt(level, tallState, pos, 2);
        }
    }
}
