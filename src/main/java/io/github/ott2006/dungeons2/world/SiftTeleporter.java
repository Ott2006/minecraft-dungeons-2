package io.github.ott2006.dungeons2.world;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.block.SiftPortalBlock;
import io.github.ott2006.dungeons2.registry.ModBlocks;
import io.github.ott2006.dungeons2.registry.ModPoiTypes;
import java.util.Comparator;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

/**
 * Finds or builds the exit portal when travelling between the Overworld and the Sift. Portals are found through
 * their point of interest, so the irregular ancient city portal is found just as well as a built one.
 */
public final class SiftTeleporter {
    public static final int SEARCH_RADIUS = 96;
    private static final int PLACEMENT_RADIUS = 24;

    private SiftTeleporter() {
    }

    @Nullable
    public static DimensionTransition getTransition(ServerLevel level, Entity entity, BlockPos portalPos) {
        ResourceKey<Level> destKey = level.dimension() == ModDimensions.SIFT ? Level.OVERWORLD : ModDimensions.SIFT;
        ServerLevel dest = level.getServer().getLevel(destKey);
        if (dest == null) {
            Dungeons2.LOGGER.error("Destination dimension {} is missing", destKey.location());
            return null;
        }
        WorldBorder border = dest.getWorldBorder();
        BlockPos target = border.clampToBounds(entity.getX(), entity.getY(), entity.getZ());
        Direction.Axis axis = level.getBlockState(portalPos).getOptionalValue(SiftPortalBlock.AXIS).orElse(Direction.Axis.X);

        BlockPos exit = findPortal(dest, target).orElse(null);
        if (exit == null) {
            exit = createPortal(dest, target, axis);
        }
        if (exit == null) {
            Dungeons2.LOGGER.error("Unable to create a Sift portal near {}", target);
            return null;
        }
        BlockPos.MutableBlockPos bottom = exit.mutable();
        while (dest.getBlockState(bottom.below()).is(ModBlocks.SIFT_PORTAL.get())) {
            bottom.move(Direction.DOWN);
        }
        Vec3 pos = new Vec3(bottom.getX() + 0.5, bottom.getY(), bottom.getZ() + 0.5);
        BlockPos ticketPos = bottom.immutable();
        return new DimensionTransition(dest, pos, Vec3.ZERO, entity.getYRot(), entity.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND.then(e -> e.placePortalTicket(ticketPos)));
    }

    public static Optional<BlockPos> findPortal(ServerLevel level, BlockPos target) {
        PoiManager poi = level.getPoiManager();
        WorldBorder border = level.getWorldBorder();
        poi.ensureLoadedAndValid(level, target, SEARCH_RADIUS);
        return poi.getInSquare(h -> h.value() == ModPoiTypes.SIFT_PORTAL.get(), target, SEARCH_RADIUS, PoiManager.Occupancy.ANY)
                .map(PoiRecord::getPos)
                .filter(border::isWithinBounds)
                .filter(p -> level.getBlockState(p).is(ModBlocks.SIFT_PORTAL.get()))
                .min(Comparator.<BlockPos>comparingDouble(p -> p.distSqr(target)).thenComparingInt(Vec3i::getY));
    }

    @Nullable
    public static BlockPos createPortal(ServerLevel level, BlockPos target, Direction.Axis axis) {
        WorldBorder border = level.getWorldBorder();
        Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        BlockPos base = null;
        search:
        for (int r = 0; r <= PLACEMENT_RADIUS; r += 2) {
            for (int dx = -r; dx <= r; dx += 2) {
                for (int dz = -r; dz <= r; dz += 2) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r) {
                        continue;
                    }
                    int x = target.getX() + dx;
                    int z = target.getZ() + dz;
                    if (!border.isWithinBounds(x, z)) {
                        continue;
                    }
                    int y = surfaceHeight(level, x, z);
                    BlockPos candidate = new BlockPos(x, y, z);
                    if (fits(level, candidate, right)) {
                        base = candidate;
                        break search;
                    }
                }
            }
        }
        if (base == null) {
            int x = target.getX();
            int z = target.getZ();
            int y = Math.max(surfaceHeight(level, x, z), level.getChunkSource().getGenerator().getSeaLevel() + 1);
            y = Math.min(y, level.getMaxBuildHeight() - 8);
            y = Math.max(y, level.getMinBuildHeight() + 2);
            base = new BlockPos(x, y, z);
        }
        build(level, base, axis);
        return base;
    }

    /** First free block above the surface. Loads (and if needed generates) the chunk first. */
    private static int surfaceHeight(ServerLevel level, int x, int z) {
        level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
    }

    private static boolean fits(ServerLevel level, BlockPos base, Direction right) {
        if (base.getY() <= level.getMinBuildHeight() + 2 || base.getY() + 6 >= level.getMaxBuildHeight()) {
            return false;
        }
        for (int i = 0; i <= 1; i++) {
            BlockPos ground = base.relative(right, i).below();
            BlockState state = level.getBlockState(ground);
            if (!state.getFluidState().isEmpty() || !state.isFaceSturdy(level, ground, Direction.UP)) {
                return false;
            }
        }
        for (int i = -1; i <= 2; i++) {
            for (int j = 0; j <= 3; j++) {
                BlockState state = level.getBlockState(base.relative(right, i).above(j));
                if (!state.canBeReplaced() || !state.getFluidState().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void build(ServerLevel level, BlockPos base, Direction.Axis axis) {
        Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction front = axis == Direction.Axis.X ? Direction.NORTH : Direction.EAST;
        BlockState frame = ModBlocks.RESONANT_DEEPSLATE.get().defaultBlockState();
        BlockState floor = level.dimension() == ModDimensions.SIFT
                ? ModBlocks.POLISHED_SIFTSTONE.get().defaultBlockState()
                : Blocks.POLISHED_DEEPSLATE.defaultBlockState();

        // a small platform in front of and behind the portal, and some room to step out
        for (int i = -1; i <= 2; i++) {
            for (int f = -1; f <= 1; f += 2) {
                BlockPos ground = base.relative(right, i).relative(front, f).below();
                if (!level.getBlockState(ground).isFaceSturdy(level, ground, Direction.UP)) {
                    level.setBlockAndUpdate(ground, floor);
                }
                for (int j = 0; j <= 2; j++) {
                    BlockPos air = ground.above(j + 1);
                    if (!level.getBlockState(air).isAir()) {
                        level.setBlockAndUpdate(air, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
        for (int i = -1; i <= 2; i++) {
            for (int j = -1; j <= 3; j++) {
                if (i == -1 || i == 2 || j == -1 || j == 3) {
                    level.setBlockAndUpdate(base.relative(right, i).above(j), frame);
                }
            }
        }
        BlockState portal = ModBlocks.SIFT_PORTAL.get().defaultBlockState().setValue(SiftPortalBlock.AXIS, axis);
        for (int i = 0; i <= 1; i++) {
            for (int j = 0; j <= 2; j++) {
                level.setBlock(base.relative(right, i).above(j), portal, 2 | 16);
            }
        }
    }
}
