package io.github.ott2006.dungeons2.entity;

import io.github.ott2006.dungeons2.entity.ai.ActionGoal;
import io.github.ott2006.dungeons2.entity.ai.KeepDistanceGoal;
import io.github.ott2006.dungeons2.entity.projectile.SiftDart;
import java.util.EnumSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Sentinel: a crested, ranged sifter. It shakes its head from side to side, releasing a barrage of darts that quickly
 * rotate towards the nearest hero. Afterwards it stands idly for a few seconds, open to attack.
 */
public class Sentinel extends SiftMonster implements Sifter {
    public static final int ACTION_VOLLEY = 1;
    public static final int ACTION_EXHAUSTED = 2;
    public static final int VOLLEY_TIME = 26;
    public static final int EXHAUSTED_TIME = 60;

    public Sentinel(EntityType<? extends Sentinel> type, Level level) {
        super(type, level);
        this.xpReward = 6;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new VolleyGoal(this));
        this.goalSelector.addGoal(4, new KeepDistanceGoal(this, 1.0, 6.0, 12.0, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addDefaultTargets(1);
    }

    private void shootDart(LivingEntity target) {
        SiftDart dart = new SiftDart(this.level(), this);
        Vec3 look = this.getLookAngle();
        double side = (this.random.nextDouble() - 0.5) * 2.0;
        dart.setPos(this.getX() + look.x * 0.6, this.getEyeY() - 0.35, this.getZ() + look.z * 0.6);
        // darts leave in a fan, then quickly rotate towards the target and lock on
        Vec3 dir = look.yRot((float) (side * 0.9)).add(0, 0.25, 0).normalize();
        dart.shoot(dir.x, dir.y, dir.z, 0.8F, 2.0F);
        dart.setHoming(target, 10, 0.35);
        dart.setDamage(3.0F);
        this.level().addFreshEntity(dart);
        this.playSound(SoundEvents.LLAMA_SPIT, 0.8F, 1.6F + this.random.nextFloat() * 0.2F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SNIFFER_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SNIFFER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SNIFFER_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 1.4F;
    }

    static class VolleyGoal extends ActionGoal<Sentinel> {
        VolleyGoal(Sentinel mob) {
            super(mob, ACTION_VOLLEY, VOLLEY_TIME + EXHAUSTED_TIME, 50, 30, EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return distSqr < 18 * 18 && this.mob.getSensing().hasLineOfSight(target);
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            this.mob.getNavigation().stop();
            if (time == 2) {
                this.mob.level().playSound(null, this.mob.blockPosition(), SoundEvents.SNIFFER_SNIFFING, SoundSource.HOSTILE, 1.0F, 1.5F);
            }
            if (target != null && time >= 8 && time <= 22 && (time - 8) % 3 == 0) {
                this.mob.shootDart(target);
            }
            if (time == VOLLEY_TIME) {
                this.mob.setAction(ACTION_EXHAUSTED);
            }
        }

        @Override
        public void stop() {
            if (this.mob.getAction() == ACTION_EXHAUSTED) {
                this.mob.setAction(ACTION_NONE);
            }
            super.stop();
        }
    }
}
