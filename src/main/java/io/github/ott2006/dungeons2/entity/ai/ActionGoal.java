package io.github.ott2006.dungeons2.entity.ai;

import io.github.ott2006.dungeons2.entity.SiftMonster;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * A timed special attack. While running, the monster's synchronised action is set to {@link #action} so the client
 * can animate it. Subclasses decide when the attack may start and what happens on each tick.
 */
public abstract class ActionGoal<T extends SiftMonster> extends Goal {
    protected final T mob;
    protected final int action;
    protected final int duration;
    protected final int cooldownTime;
    protected int nextUseTick;
    protected int time;

    protected ActionGoal(T mob, int action, int duration, int cooldownTime, int initialCooldown, EnumSet<Flag> flags) {
        this.mob = mob;
        this.action = action;
        this.duration = duration;
        this.cooldownTime = cooldownTime;
        this.nextUseTick = initialCooldown;
        this.setFlags(flags);
    }

    protected ActionGoal(T mob, int action, int duration, int cooldownTime) {
        this(mob, action, duration, cooldownTime, cooldownTime / 2, EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mob.tickCount < this.nextUseTick) {
            return false;
        }
        if (this.mob.getAction() != SiftMonster.ACTION_NONE) {
            return false;
        }
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive() && this.canStart(target, this.mob.distanceToSqr(target));
    }

    /** Whether the attack can start against {@code target} at the given squared distance. */
    protected abstract boolean canStart(LivingEntity target, double distSqr);

    @Override
    public boolean canContinueToUse() {
        return this.time < this.duration && this.mob.isAlive();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.time = 0;
        this.mob.setAction(this.action);
        if (this.getFlags().contains(Flag.MOVE)) {
            this.mob.getNavigation().stop();
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null && this.getFlags().contains(Flag.LOOK)) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
        this.onTick(this.time, target);
        this.time++;
    }

    /** Called every tick while the attack runs. {@code target} may be null if it was lost. */
    protected abstract void onTick(int time, LivingEntity target);

    @Override
    public void stop() {
        if (this.mob.getAction() == this.action) {
            this.mob.setAction(SiftMonster.ACTION_NONE);
        }
        this.nextUseTick = this.mob.tickCount + this.cooldownTime + this.mob.getRandom().nextInt(Math.max(1, this.cooldownTime / 4));
    }
}
