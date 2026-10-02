package io.github.ott2006.dungeons2.entity.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

/** Moves towards the target until in range for ranged attacks; optionally backs away if it gets too close. */
public class KeepDistanceGoal extends Goal {
    private final PathfinderMob mob;
    private final double speed;
    private final double minDist;
    private final double maxDist;
    private final boolean retreat;
    private int repath;

    public KeepDistanceGoal(PathfinderMob mob, double speed, double minDist, double maxDist, boolean retreat) {
        this.mob = mob;
        this.speed = speed;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.retreat = retreat;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return;
        }
        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double dist = this.mob.distanceTo(target);
        boolean canSee = this.mob.getSensing().hasLineOfSight(target);
        if (--this.repath > 0) {
            return;
        }
        this.repath = 10;
        if (dist > this.maxDist || !canSee) {
            this.mob.getNavigation().moveTo(target, this.speed);
        } else if (this.retreat && dist < this.minDist) {
            double dx = this.mob.getX() - target.getX();
            double dz = this.mob.getZ() - target.getZ();
            double len = Math.max(0.01, Math.sqrt(dx * dx + dz * dz));
            this.mob.getNavigation().moveTo(this.mob.getX() + dx / len * 4, this.mob.getY(), this.mob.getZ() + dz / len * 4, this.speed);
        } else {
            this.mob.getNavigation().stop();
        }
    }
}
