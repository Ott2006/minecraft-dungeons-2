package io.github.ott2006.dungeons2.entity;

import io.github.ott2006.dungeons2.entity.ai.ActionGoal;
import io.github.ott2006.dungeons2.registry.ModEntities;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Harmonizer: a massive mini-boss variant of the sprout. It roars, sending out a circular shockwave and summoning
 * sifters to help it fight, keeps its allies buffed with beams, and grabs heroes with its tentacles.
 */
public class Harmonizer extends Sprout {
    public static final int ACTION_ROAR = 3;
    public static final int ACTION_GRAB = 4;
    public static final int ROAR_TIME = 30;
    public static final int GRAB_TIME = 20;

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_10).setDarkenScreen(false);
    private int buffIndex;

    public Harmonizer(EntityType<? extends Harmonizer> type, Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.FLYING_SPEED, 0.3)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RoarGoal(this));
        this.goalSelector.addGoal(2, new GrabGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addDefaultTargets(1);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        // beams connect the harmonizer with its allies and keep them buffed
        if (this.tickCount % 40 == 0) {
            List<SiftMonster> allies = this.level().getEntitiesOfClass(SiftMonster.class, this.getBoundingBox().inflate(16.0),
                    e -> e != this && e.isAlive() && e instanceof Sifter);
            if (allies.isEmpty()) {
                this.setBeamTarget(null);
            } else {
                SiftMonster ally = allies.get(this.buffIndex++ % allies.size());
                this.setBeamTarget(ally);
                if (!ally.hasEffect(MobEffects.DAMAGE_BOOST)) {
                    this.buff(ally, 200);
                }
            }
        }
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
        return SoundEvents.GLOW_SQUID_AMBIENT;
    }

    @Override
    public float getVoicePitch() {
        return 0.5F;
    }

    @Override
    protected float getSoundVolume() {
        return 2.0F;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    private void roar(ServerLevel level) {
        this.playSound(SoundEvents.WARDEN_ROAR, 2.0F, 1.3F);
        level.sendParticles(ParticleTypes.SONIC_BOOM, this.getX(), this.getY() + 1, this.getZ(), 1, 0, 0, 0, 0);
        for (int i = 0; i < 24; i++) {
            double a = i / 24.0 * Math.PI * 2;
            level.sendParticles(ParticleTypes.SCULK_SOUL, this.getX() + Math.cos(a) * 3, this.getY() + 0.5, this.getZ() + Math.sin(a) * 3,
                    1, 0, 0, 0, 0.05);
        }
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(6.0),
                e -> e != this && !isSiftNative(e) || e instanceof EchoGolem)) {
            entity.hurt(this.damageSources().mobAttack(this), 6.0F);
            this.knockAway(entity, 1.4, 0.4);
        }
        long minions = level.getEntitiesOfClass(SiftMonster.class, this.getBoundingBox().inflate(24.0), e -> e != this).size();
        if (minions < 6) {
            List<EntityType<? extends Mob>> types = new ArrayList<>();
            int count = 5 + this.random.nextInt(3);
            for (int i = 0; i < count; i++) {
                int r = this.random.nextInt(10);
                types.add(r < 3 ? ModEntities.SENTINEL.get() : r < 6 ? ModEntities.NESTER.get()
                        : r < 8 ? ModEntities.SEEDLING.get() : r < 9 ? ModEntities.POLLINATOR.get() : ModEntities.STALKER.get());
            }
            this.summon(level, types);
        }
    }

    static class RoarGoal extends ActionGoal<Harmonizer> {
        RoarGoal(Harmonizer mob) {
            super(mob, ACTION_ROAR, ROAR_TIME, 320);
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return distSqr < 20 * 20;
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.3));
            if (time == 15 && this.mob.level() instanceof ServerLevel level) {
                this.mob.roar(level);
            }
        }
    }

    static class GrabGoal extends ActionGoal<Harmonizer> {
        GrabGoal(Harmonizer mob) {
            super(mob, ACTION_GRAB, GRAB_TIME, 140);
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return distSqr > 4 * 4 && distSqr < 11 * 11 && this.mob.getSensing().hasLineOfSight(target);
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            if (time == 10 && target != null && this.mob.distanceToSqr(target) < 12 * 12) {
                this.mob.pullTowards(target, 1.3);
                target.hurt(this.mob.damageSources().mobAttack(this.mob), 4.0F);
                this.mob.playSound(SoundEvents.WARDEN_TENDRIL_CLICKS, 2.0F, 0.8F);
            }
        }
    }
}
