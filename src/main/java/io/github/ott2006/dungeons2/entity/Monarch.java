package io.github.ott2006.dungeons2.entity;

import io.github.ott2006.dungeons2.entity.projectile.MonarchFeather;
import io.github.ott2006.dungeons2.registry.ModEntities;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
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
 * The Monarch: a huge, long-clawed sculker boss that strides slowly through the Carapace.
 * <ul>
 *     <li>Summons two stalkers and two scavengers (not while they live).</li>
 *     <li>Dashes forward with a twirling slash, leaving feathers behind.</li>
 *     <li>Spins, creating eight feathers that fire after five seconds.</li>
 *     <li>Multi-spin: slash, dash and spin in sequence.</li>
 *     <li>At 75% health it summons a Monarch Echo, a spectral clone that dies with the original.</li>
 * </ul>
 */
public class Monarch extends SiftMonster {
    public static final int ACTION_SUMMON = 1;
    public static final int ACTION_DASH = 2;
    public static final int ACTION_SPIN = 3;
    public static final int ACTION_TWIRL = 4;
    public static final int ACTION_MULTI = 5;

    private static final EntityDataAccessor<Boolean> DATA_ECHO = SynchedEntityData.defineId(Monarch.class, EntityDataSerializers.BOOLEAN);

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true).setPlayBossMusic(true);
    private final List<UUID> minions = new ArrayList<>();
    @Nullable
    private UUID echoId;
    @Nullable
    private UUID originalId;
    private boolean echoSpawned;

    public Monarch(EntityType<? extends Monarch> type, Level level) {
        super(type, level);
        this.xpReward = 120;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 320.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.STEP_HEIGHT, 1.5);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ECHO, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MonarchAttackGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addDefaultTargets(1);
    }

    public boolean isEcho() {
        return this.entityData.get(DATA_ECHO);
    }

    private void makeEcho(Monarch original) {
        this.entityData.set(DATA_ECHO, true);
        this.originalId = original.getUUID();
        this.xpReward = 0;
        this.bossEvent.setVisible(false);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100.0);
        this.setHealth(100.0F);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return !this.isEcho() && super.shouldDropLoot();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        ServerLevel level = (ServerLevel) this.level();
        if (this.isEcho()) {
            if (this.tickCount % 20 == 0) {
                Entity original = this.originalId == null ? null : level.getEntity(this.originalId);
                if (original == null || !original.isAlive()) {
                    this.vanish(level);
                }
            }
            return;
        }
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (!this.echoSpawned && this.getHealth() < this.getMaxHealth() * 0.75F) {
            this.echoSpawned = true;
            this.spawnEcho(level);
        }
    }

    private void spawnEcho(ServerLevel level) {
        Monarch echo = ModEntities.MONARCH.get().create(level);
        if (echo == null) {
            return;
        }
        Vec3 side = this.getLookAngle().yRot((float) (Math.PI / 2)).multiply(3, 0, 3);
        echo.moveTo(this.getX() + side.x, this.getY(), this.getZ() + side.z, this.getYRot(), 0);
        echo.finalizeSpawn(level, level.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
        echo.makeEcho(this);
        echo.setTarget(this.getTarget());
        level.addFreshEntity(echo);
        this.echoId = echo.getUUID();
        level.sendParticles(ParticleTypes.SCULK_SOUL, echo.getX(), echo.getY() + 2, echo.getZ(), 50, 0.8, 2.0, 0.8, 0.05);
        this.playSound(SoundEvents.WARDEN_SONIC_CHARGE, 3.0F, 0.8F);
    }

    private void vanish(ServerLevel level) {
        level.sendParticles(ParticleTypes.SCULK_SOUL, this.getX(), this.getY() + 2, this.getZ(), 40, 0.6, 2.0, 0.6, 0.05);
        this.discard();
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.isEcho() && this.echoId != null && this.level() instanceof ServerLevel level) {
            Entity echo = level.getEntity(this.echoId);
            if (echo instanceof Monarch monarch) {
                monarch.vanish(level);
            }
        }
    }

    private boolean hasLivingMinions(ServerLevel level) {
        this.minions.removeIf(uuid -> {
            Entity e = level.getEntity(uuid);
            return e == null || !e.isAlive();
        });
        return !this.minions.isEmpty();
    }

    private void spawnFeathers(ServerLevel level, int count, double radius, int delay) {
        for (int i = 0; i < count; i++) {
            double a = i / (double) count * Math.PI * 2 + this.getYRot() * Math.PI / 180;
            Vec3 pos = new Vec3(this.getX() + Math.cos(a) * radius, this.getY() + 2.5 + (i % 2) * 0.6, this.getZ() + Math.sin(a) * radius);
            MonarchFeather feather = new MonarchFeather(level, this, pos, delay + i * 2);
            level.addFreshEntity(feather);
        }
        this.playSound(SoundEvents.PHANTOM_FLAP, 2.0F, 0.7F);
    }

    private void hitAround(double radius, float damage, double knock) {
        for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius, 0.5, radius),
                e -> e != this && (!isSiftNative(e) || e instanceof EchoGolem))) {
            if (e.hurt(this.damageSources().mobAttack(this), damage) && knock > 0) {
                this.knockAway(e, knock, 0.3);
            }
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (!this.isEcho()) {
            this.bossEvent.addPlayer(player);
        }
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
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Echo", this.isEcho());
        tag.putBoolean("EchoSpawned", this.echoSpawned);
        if (this.echoId != null) {
            tag.putUUID("EchoId", this.echoId);
        }
        if (this.originalId != null) {
            tag.putUUID("OriginalId", this.originalId);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_ECHO, tag.getBoolean("Echo"));
        this.echoSpawned = tag.getBoolean("EchoSpawned");
        this.echoId = tag.hasUUID("EchoId") ? tag.getUUID("EchoId") : null;
        this.originalId = tag.hasUUID("OriginalId") ? tag.getUUID("OriginalId") : null;
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
        if (this.isEcho()) {
            this.bossEvent.setVisible(false);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WARDEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WARDEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return this.isEcho() ? 1.4F : 1.1F;
    }

    /** The Monarch's brain: walks slowly towards its target and picks one of its attacks. */
    static class MonarchAttackGoal extends Goal {
        private final Monarch mob;
        private final Set<Entity> dashHit = new HashSet<>();
        private int time;
        private int pause = 40;
        private int summonCooldown = 60;
        private int spinCooldown;
        private int repath;
        private Vec3 dashDir = Vec3.ZERO;

        MonarchAttackGoal(Monarch mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() || this.mob.getAction() != ACTION_NONE;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            this.mob.setAction(ACTION_NONE);
            this.mob.getNavigation().stop();
        }

        private int duration(int action) {
            return switch (action) {
                case ACTION_SUMMON -> 30;
                case ACTION_DASH -> 30;
                case ACTION_SPIN -> 50;
                case ACTION_TWIRL -> 20;
                case ACTION_MULTI -> 85;
                default -> 0;
            };
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            ServerLevel level = (ServerLevel) this.mob.level();
            this.summonCooldown--;
            this.spinCooldown--;
            int action = this.mob.getAction();
            if (action == ACTION_NONE) {
                if (target == null) {
                    return;
                }
                this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                double dist = this.mob.distanceTo(target);
                if (--this.repath <= 0) {
                    this.repath = 10;
                    if (dist > 2.5) {
                        this.mob.getNavigation().moveTo(target, 1.0);
                    } else {
                        this.mob.getNavigation().stop();
                    }
                }
                if (--this.pause > 0) {
                    return;
                }
                boolean lowHealth = this.mob.getHealth() < this.mob.getMaxHealth() * 0.5F;
                int next;
                if (!this.mob.isEcho() && this.summonCooldown <= 0 && !this.mob.hasLivingMinions(level)) {
                    next = ACTION_SUMMON;
                    this.summonCooldown = 500;
                } else if (dist < 3.5) {
                    next = lowHealth && this.mob.random.nextFloat() < 0.4F ? ACTION_MULTI : ACTION_TWIRL;
                } else if (dist < 10 && this.spinCooldown <= 0 && this.mob.random.nextBoolean()) {
                    next = ACTION_SPIN;
                    this.spinCooldown = 200;
                } else if (dist < 18) {
                    next = lowHealth && this.mob.random.nextFloat() < 0.3F ? ACTION_MULTI : ACTION_DASH;
                } else {
                    return;
                }
                this.time = 0;
                this.dashHit.clear();
                this.mob.getNavigation().stop();
                this.mob.setAction(next);
                return;
            }

            int t = this.time++;
            switch (action) {
                case ACTION_SUMMON -> {
                    if (t == 2) {
                        this.mob.playSound(SoundEvents.WARDEN_ROAR, 3.0F, 1.2F);
                    }
                    if (t == 15) {
                        List<EntityType<? extends Mob>> types = List.of(ModEntities.STALKER.get(), ModEntities.STALKER.get(),
                                ModEntities.SCAVENGER.get(), ModEntities.SCAVENGER.get());
                        for (Mob minion : this.mob.summon(level, types)) {
                            this.mob.minions.add(minion.getUUID());
                        }
                    }
                }
                case ACTION_TWIRL -> this.twirl(t, 0);
                case ACTION_DASH -> this.dash(level, target, t, 0);
                case ACTION_SPIN -> this.spin(level, t, 0, 100);
                case ACTION_MULTI -> {
                    if (t < 20) {
                        this.twirl(t, 0);
                    } else if (t < 45) {
                        this.dash(level, target, t, 20);
                    } else {
                        this.spin(level, t, 45, 60);
                    }
                }
                default -> {
                }
            }
            if (this.time >= this.duration(action)) {
                this.mob.setAction(ACTION_NONE);
                boolean lowHealth = this.mob.getHealth() < this.mob.getMaxHealth() * 0.5F;
                this.pause = (lowHealth ? 15 : 30) + this.mob.random.nextInt(20);
            }
        }

        private void twirl(int t, int start) {
            if (t - start == 4) {
                this.mob.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 0.6F);
            }
            if (t - start == 8) {
                this.mob.hitAround(3.5, 9.0F, 1.0);
                if (this.mob.level() instanceof ServerLevel level) {
                    level.sendParticles(ParticleTypes.SWEEP_ATTACK, this.mob.getX(), this.mob.getY() + 1.5, this.mob.getZ(), 6, 1.5, 0.3, 1.5, 0);
                }
            }
        }

        private void dash(ServerLevel level, @Nullable LivingEntity target, int t, int start) {
            int local = t - start;
            if (local < 10) {
                this.mob.getNavigation().stop();
                if (target != null) {
                    this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                    this.dashDir = target.position().subtract(this.mob.position()).multiply(1, 0, 1).normalize();
                }
                if (local == 2) {
                    this.mob.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 1.5F, 0.6F);
                }
            } else if (local < 20) {
                this.mob.setDeltaMovement(this.dashDir.x * 1.3, this.mob.getDeltaMovement().y, this.dashDir.z * 1.3);
                level.sendParticles(ParticleTypes.SCULK_SOUL, this.mob.getX(), this.mob.getY() + 1.5, this.mob.getZ(), 2, 0.4, 1.0, 0.4, 0.01);
                for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate(1.0),
                        e -> e != this.mob && (!isSiftNative(e) || e instanceof EchoGolem))) {
                    if (this.dashHit.add(e)) {
                        e.hurt(this.mob.damageSources().mobAttack(this.mob), 10.0F);
                        this.mob.knockAway(e, 1.2, 0.4);
                    }
                }
            } else if (local == 22) {
                this.mob.spawnFeathers(level, 3, 1.5, 20);
            }
        }

        private void spin(ServerLevel level, int t, int start, int featherDelay) {
            int local = t - start;
            this.mob.getNavigation().stop();
            if (local == 10) {
                this.mob.spawnFeathers(level, 8, 2.5, featherDelay);
            }
            if (local < 40 && local % 8 == 0) {
                this.mob.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.2F, 0.8F + local * 0.01F);
                this.mob.hitAround(3.0, 6.0F, 0.6);
            }
        }
    }
}
