package io.github.ott2006.dungeons2.world;

import io.github.ott2006.dungeons2.block.SiftPortalBlock;
import io.github.ott2006.dungeons2.registry.ModBlocks;
import io.github.ott2006.dungeons2.registry.ModTags;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Finds the inside of a portal frame. Unlike nether portals, Deep Dark portals may have any shape: the ancient city
 * portal frame is far from rectangular. The interior is found with a flood fill in the vertical plane, which must be
 * completely enclosed by frame blocks.
 */
public final class SiftPortalShape {
    public static final int MAX_BLOCKS = 900;
    public static final int MAX_EXTENT = 40;

    private SiftPortalShape() {
    }

    public static boolean isFrame(BlockState state) {
        return state.is(ModTags.Blocks.SIFT_PORTAL_FRAMES);
    }

    private static boolean isFillable(BlockState state) {
        return state.isAir() || state.is(ModBlocks.SIFT_PORTAL.get());
    }

    /**
     * Flood fills the interior that contains {@code start} in the plane of {@code axis}.
     *
     * @param axis X means the portal spans the X and Y axes (like a nether portal with axis X).
     */
    public static Optional<Set<BlockPos>> findInterior(LevelAccessor level, BlockPos start, Direction.Axis axis) {
        if (!isFillable(level.getBlockState(start))) {
            return Optional.empty();
        }
        Direction[] dirs = axis == Direction.Axis.X
                ? new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}
                : new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH};
        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start.immutable());
        visited.add(start.immutable());
        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            for (Direction dir : dirs) {
                BlockPos next = pos.relative(dir);
                if (visited.contains(next)) {
                    continue;
                }
                BlockState state = level.getBlockState(next);
                if (isFrame(state)) {
                    continue;
                }
                if (!isFillable(state)) {
                    return Optional.empty();
                }
                if (Math.abs(next.getX() - start.getX()) > MAX_EXTENT || Math.abs(next.getY() - start.getY()) > MAX_EXTENT
                        || Math.abs(next.getZ() - start.getZ()) > MAX_EXTENT || visited.size() >= MAX_BLOCKS
                        || level.isOutsideBuildHeight(next)) {
                    return Optional.empty();
                }
                visited.add(next);
                queue.add(next);
            }
        }
        return Optional.of(visited);
    }

    /** Tries to light a portal whose interior contains {@code start}. Returns true on success. */
    public static boolean tryLight(LevelAccessor level, BlockPos start) {
        for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}) {
            Optional<Set<BlockPos>> interior = findInterior(level, start, axis);
            if (interior.isPresent()) {
                BlockState portal = ModBlocks.SIFT_PORTAL.get().defaultBlockState().setValue(SiftPortalBlock.AXIS, axis);
                for (BlockPos pos : interior.get()) {
                    level.setBlock(pos, portal, 2 | 16);
                }
                return true;
            }
        }
        return false;
    }
}
