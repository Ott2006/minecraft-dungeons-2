package io.github.ott2006.dungeons2.entity;

import io.github.ott2006.dungeons2.entity.ai.ActionGoal;
import io.github.ott2006.dungeons2.entity.projectile.SiftDart;
import io.github.ott2006.dungeons2.registry.ModEntities;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Dartback: a large, six-legged sifter mini-boss that lurks in the ichor-filled ravines of the Sift. It raises its
 * body to spew volleys of darts, dashes at heroes and calls stalkers and scavengers to its side. It hoards stolen
 * soul blocks.
 */
public class Dartback extends SiftMonster implements Sifter {
    public static final int ACTION_SPEW = 1;
    public static final int ACTION_DASH = 2;
    public static final int ACTION_SUMMON = 3;
    public static final int SPEW_TIME = 40;
    public static final int DASH_TIME = 28;
    public static final int SUMMON_TIME = 24;

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10).setDarkenScreen(false);
    private final List<UUID> minions = new ArrayList<>();

    public Dartback(EntityType<? extends Dartback> type, Level level) {
        super(type, level);
        this.xpReward = 60;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 180.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SummonGoal(this));
        this.goalSelector.addGoal(2, new SpewGoal(this));
        this.goalSelector.addGoal(2, new DashGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addDefaultTargets(1);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    private boolean hasLivingMinions(ServerLevel level) {
        this.minions.removeIf(uuid -> {
            Entity e = level.getEntity(uuid);
            return e == null || !e.isAlive();
        });
        return !this.minions.isEmpty();
    }

    private void spewDart(LivingEntity target, int index) {
        SiftDart dart = new SiftDart(this.level(), this);
        Vec3 look = this.getLookAngle();
        dart.setPos(this.getX() + look.x * 1.2, this.getY() + 1.6, this.getZ() + look.z * 1.2);
        Vec3 toTarget = target.getBoundingBox().getCenter().subtract(dart.position()).normalize();
        float spread = (index % 5 - 2) * 0.18F;
        Vec3 dir = toTarget.yRot(spread);
        dart.shoot(dir.x, dir.y + 0.08, dir.z, 1.1F, 3.0F);
        dart.setDamage(4.0F);
        dart.withEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
        this.level().addFreshEntity(dart);
        this.playSound(SoundEvents.LLAMA_SPIT, 1.0F, 0.8F);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return 1.3F;
    }

    static class SpewGoal extends ActionGoal<Dartback> {
        SpewGoal(Dartback mob) {
            super(mob, ACTION_SPEW, SPEW_TIME, 90);
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return distSqr > 4 * 4 && distSqr < 20 * 20 && this.mob.getSensing().hasLineOfSight(target);
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            if (time == 4) {
                this.mob.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.4F);
            }
            if (target != null && time >= 18 && time <= 36 && time % 2 == 0) {
                this.mob.spewDart(target, time / 2);
            }
        }
    }

    static class DashGoal extends ActionGoal<Dartback> {
        private final Set<Entity> hit = new HashSet<>();
        private Vec3 direction = Vec3.ZERO;

        DashGoal(Dartback mob) {
            super(mob, ACTION_DASH, DASH_TIME, 150);
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return distSqr > 5 * 5 && distSqr < 16 * 16 && this.mob.onGround();
        }

        @Override
        public void start() {
            super.start();
            this.hit.clear();
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            if (time < 10) {
                if (target != null) {
                    this.direction = target.position().subtract(this.mob.position()).multiply(1, 0, 1).normalize();
                }
                if (time == 2) {
                    this.mob.playSound(SoundEvents.RAVAGER_STEP, 1.5F, 0.7F);
                }
                return;
            }
            if (time < 24) {
                this.mob.getLookControl().setLookAt(this.mob.getX() + this.direction.x, this.mob.getEyeY(), this.mob.getZ() + this.direction.z);
                this.mob.setDeltaMovement(this.direction.x * 0.9, this.mob.getDeltaMovement().y, this.direction.z * 0.9);
                if (this.mob.level() instanceof ServerLevel level) {
                    level.sendParticles(ParticleTypes.CLOUD, this.mob.getX(), this.mob.getY() + 0.2, this.mob.getZ(), 2, 0.5, 0.1, 0.5, 0.0);
                    for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate(0.6),
                            e -> e != this.mob && (!isSiftNative(e) || e instanceof EchoGolem))) {
                        if (this.hit.add(e)) {
                            e.hurt(this.mob.damageSources().mobAttack(this.mob), 10.0F);
                            this.mob.knockAway(e, 1.5, 0.5);
                        }
                    }
                }
            }
        }
    }

    static class SummonGoal extends ActionGoal<Dartback> {
        SummonGoal(Dartback mob) {
            super(mob, ACTION_SUMMON, SUMMON_TIME, 500);
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return this.mob.getHealth() < this.mob.getMaxHealth() * 0.7F && this.mob.level() instanceof ServerLevel level
                    && !this.mob.hasLivingMinions(level);
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            if (time == 12 && this.mob.level() instanceof ServerLevel level) {
                this.mob.playSound(SoundEvents.RAVAGER_ROAR, 2.0F, 0.8F);
                List<EntityType<? extends Mob>> types = List.of(ModEntities.STALKER.get(), ModEntities.STALKER.get(),
                        ModEntities.SCAVENGER.get(), ModEntities.SCAVENGER.get());
                for (Mob mob : this.mob.summon(level, types)) {
                    this.mob.minions.add(mob.getUUID());
                }
            }
        }
    }
}
