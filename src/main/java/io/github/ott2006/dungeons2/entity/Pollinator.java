package io.github.ott2006.dungeons2.entity;

import io.github.ott2006.dungeons2.entity.ai.ActionGoal;
import io.github.ott2006.dungeons2.entity.ai.KeepDistanceGoal;
import io.github.ott2006.dungeons2.entity.projectile.GooGlob;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Pollinator: a frog-like, ranged sifter. It puffs up and regurgitates large globs that burst into yellow goo. Heroes
 * that come too close get struck by its tongue. Pollinators keep their distance, but never retreat.
 */
public class Pollinator extends SiftMonster implements Sifter {
    public static final int ACTION_PUFF = 1;
    public static final int ACTION_TONGUE = 2;
    public static final int PUFF_TIME = 22;
    public static final int TONGUE_TIME = 12;

    public Pollinator(EntityType<? extends Pollinator> type, Level level) {
        super(type, level);
        this.xpReward = 6;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 28.0)
                .add(Attributes.JUMP_STRENGTH, 0.5);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TongueGoal(this));
        this.goalSelector.addGoal(2, new LobGoal(this));
        this.goalSelector.addGoal(4, new KeepDistanceGoal(this, 1.0, 5.0, 10.0, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addDefaultTargets(1);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // frog-like hops while moving
        if (!this.level().isClientSide && this.onGround() && this.getNavigation().isInProgress() && this.tickCount % 16 == 0) {
            this.getJumpControl().jump();
        }
    }

    private void lob(LivingEntity target) {
        GooGlob glob = new GooGlob(this.level(), this);
        double dx = target.getX() - this.getX();
        double dy = target.getEyeY() - 1.1 - glob.getY();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        glob.setPos(this.getX(), this.getEyeY(), this.getZ());
        glob.shoot(dx, dy + dist * 0.2, dz, 0.8F, 4.0F);
        this.level().addFreshEntity(glob);
        this.playSound(SoundEvents.FROG_TONGUE, 1.0F, 0.6F);
        this.playSound(SoundEvents.SLIME_JUMP, 1.0F, 0.6F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FROG_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.FROG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.FROG_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.7F;
    }

    static class LobGoal extends ActionGoal<Pollinator> {
        LobGoal(Pollinator mob) {
            super(mob, ACTION_PUFF, PUFF_TIME, 70);
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return distSqr > 3.5 * 3.5 && distSqr < 18 * 18 && this.mob.getSensing().hasLineOfSight(target);
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            if (time == 0) {
                this.mob.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, 1.0F, 0.7F);
            }
            if (time == PUFF_TIME - 2 && target != null) {
                this.mob.lob(target);
            }
        }
    }

    static class TongueGoal extends ActionGoal<Pollinator> {
        TongueGoal(Pollinator mob) {
            super(mob, ACTION_TONGUE, TONGUE_TIME, 30);
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return distSqr < 3.5 * 3.5;
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            if (time == 5 && target != null && this.mob.distanceToSqr(target) < 4.5 * 4.5) {
                this.mob.playSound(SoundEvents.FROG_TONGUE, 1.0F, 1.0F);
                this.mob.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(target);
            }
        }
    }
}
