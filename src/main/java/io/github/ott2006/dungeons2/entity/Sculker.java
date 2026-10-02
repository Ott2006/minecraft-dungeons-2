package io.github.ott2006.dungeons2.entity;

import io.github.ott2006.dungeons2.entity.ai.ActionGoal;
import io.github.ott2006.dungeons2.entity.ai.KeepDistanceGoal;
import io.github.ott2006.dungeons2.entity.projectile.SoulWisp;
import io.github.ott2006.dungeons2.registry.ModBlocks;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Sculkers: sculk-related, hostile creatures of the Carapace. They come in four kinds:
 * <ul>
 *     <li><b>Hunter</b> (orange, melee): grabs heroes and pulls them in.</li>
 *     <li><b>Scavenger</b> (yellow, ranged): passive until approached; throws tracking souls that leave soul fire and
 *     dashes backwards, leaving golden afterimages.</li>
 *     <li><b>Stalker</b> (blue, melee): slashes twice and spins, or lunges from afar leaving a spectral afterimage.</li>
 *     <li><b>Trapper</b> (pink, melee): conjures magic barriers around heroes.</li>
 * </ul>
 */
public class Sculker extends SiftMonster {
    public enum Kind {
        HUNTER("hunter", 24.0, 5.0, 0.31, 32.0),
        SCAVENGER("scavenger", 18.0, 3.0, 0.28, 10.0),
        STALKER("stalker", 26.0, 5.0, 0.33, 32.0),
        TRAPPER("trapper", 24.0, 4.0, 0.28, 32.0);

        public final String name;
        final double health;
        final double damage;
        final double speed;
        final double followRange;

        Kind(String name, double health, double damage, double speed, double followRange) {
            this.name = name;
            this.health = health;
            this.damage = damage;
            this.speed = speed;
            this.followRange = followRange;
        }
    }

    public static final int ACTION_GRAB = 1;
    public static final int ACTION_THROW = 2;
    public static final int ACTION_DODGE = 3;
    public static final int ACTION_SLASH = 4;
    public static final int ACTION_LUNGE = 5;
    public static final int ACTION_CAST = 6;

    public static final int GRAB_TIME = 16;
    public static final int THROW_TIME = 30;
    public static final int DODGE_TIME = 12;
    public static final int SLASH_TIME = 30;
    public static final int LUNGE_TIME = 18;
    public static final int CAST_TIME = 24;

    private static final Vector3f GOLD = new Vector3f(1.0F, 0.8F, 0.25F);
    private static final Vector3f SPECTRAL = new Vector3f(0.45F, 0.75F, 1.0F);

    private final Kind kind;

    public Sculker(EntityType<? extends Sculker> type, Level level, Kind kind) {
        super(type, level);
        this.kind = kind;
        this.xpReward = 7;
        // goals depend on the kind, which is only known now
        if (!level.isClientSide) {
            this.registerKindGoals();
        }
    }

    public Kind getKind() {
        return this.kind;
    }

    public static AttributeSupplier.Builder createAttributes(Kind kind) {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, kind.health)
                .add(Attributes.MOVEMENT_SPEED, kind.speed)
                .add(Attributes.ATTACK_DAMAGE, kind.damage)
                .add(Attributes.FOLLOW_RANGE, kind.followRange)
                .add(Attributes.ARMOR, 3.0);
    }

    @Override
    protected void registerGoals() {
        // registered in registerKindGoals(), because the kind is not yet known here
    }

    private void registerKindGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        switch (this.kind) {
            case HUNTER -> {
                this.goalSelector.addGoal(2, new GrabGoal(this));
                this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, true));
            }
            case SCAVENGER -> {
                this.goalSelector.addGoal(1, new DodgeGoal(this));
                this.goalSelector.addGoal(2, new ThrowGoal(this));
                this.goalSelector.addGoal(4, new KeepDistanceGoal(this, 1.0, 5.0, 11.0, true));
            }
            case STALKER -> {
                this.goalSelector.addGoal(2, new SlashGoal(this));
                this.goalSelector.addGoal(2, new LungeGoal(this));
                this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, true));
            }
            case TRAPPER -> {
                this.goalSelector.addGoal(2, new TrapGoal(this));
                this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1, true));
            }
        }
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addDefaultTargets(1);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            int action = this.getAction();
            if (action == ACTION_THROW && this.getActionTime(0) < 22) {
                Vec3 look = this.getLookAngle();
                this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() + look.z * 0.5, this.getY() + 1.3,
                        this.getZ() - look.x * 0.5, 0, 0.02, 0);
                this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() - look.z * 0.5, this.getY() + 1.3,
                        this.getZ() + look.x * 0.5, 0, 0.02, 0);
            } else if (action == ACTION_DODGE) {
                this.afterimage(GOLD);
            } else if (action == ACTION_LUNGE && this.getActionTime(0) > 8) {
                this.afterimage(SPECTRAL);
            } else if (action == ACTION_CAST) {
                this.level().addParticle(ParticleTypes.WAX_OFF, this.getRandomX(0.8), this.getRandomY(), this.getRandomZ(0.8), 0, 0.05, 0);
            }
        }
    }

    /** Leaves a trail of coloured dust in the shape of the body: the afterimage of a dash. */
    private void afterimage(Vector3f color) {
        for (int i = 0; i < 6; i++) {
            this.level().addParticle(new DustParticleOptions(color, 1.0F), this.getRandomX(0.6), this.getRandomY(), this.getRandomZ(0.6), 0, 0, 0);
        }
    }

    private void hitAround(double radius, float damage) {
        for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius),
                e -> e != this && (!isSiftNative(e) || e instanceof EchoGolem))) {
            e.hurt(this.damageSources().mobAttack(this), damage);
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
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SCULK_BLOCK_STEP, 0.4F, 1.2F);
    }

    @Override
    protected float getSoundVolume() {
        return 0.5F;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 1.6F;
    }

    // ------------------------------------------------------------------ hunter
    static class GrabGoal extends ActionGoal<Sculker> {
        GrabGoal(Sculker mob) {
            super(mob, ACTION_GRAB, GRAB_TIME, 110);
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return distSqr > 4 * 4 && distSqr < 10 * 10 && this.mob.getSensing().hasLineOfSight(target);
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            if (time == 8 && target != null && this.mob.distanceToSqr(target) < 11 * 11) {
                this.mob.playSound(SoundEvents.WARDEN_TENDRIL_CLICKS, 1.5F, 1.2F);
                this.mob.pullTowards(target, 1.2);
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 1), this.mob);
                if (this.mob.level() instanceof ServerLevel level) {
                    Vec3 from = this.mob.getEyePosition();
                    Vec3 to = target.getEyePosition();
                    for (int i = 0; i <= 10; i++) {
                        Vec3 p = from.lerp(to, i / 10.0);
                        level.sendParticles(ParticleTypes.SCULK_CHARGE_POP, p.x, p.y, p.z, 1, 0, 0, 0, 0);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------ scavenger
    static class ThrowGoal extends ActionGoal<Sculker> {
        ThrowGoal(Sculker mob) {
            super(mob, ACTION_THROW, THROW_TIME, 70);
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return distSqr < 16 * 16 && this.mob.getSensing().hasLineOfSight(target);
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            if (time == 2) {
                this.mob.playSound(SoundEvents.BLAZE_SHOOT, 0.6F, 1.6F);
            }
            if (target != null && (time == 20 || time == 26)) {
                SoulWisp wisp = new SoulWisp(this.mob.level(), this.mob);
                Vec3 look = this.mob.getLookAngle();
                double side = time == 20 ? 0.5 : -0.5;
                wisp.setPos(this.mob.getX() + look.z * side, this.mob.getY() + 1.4, this.mob.getZ() - look.x * side);
                Vec3 dir = target.getEyePosition().subtract(wisp.position()).normalize().add(0, 0.3, 0);
                wisp.shoot(dir.x, dir.y, dir.z, 0.45F, 6.0F);
                wisp.setHoming(target, 60, 0.12);
                wisp.setDamage(4.0F);
                this.mob.level().addFreshEntity(wisp);
                this.mob.playSound(SoundEvents.SOUL_ESCAPE.value(), 1.0F, 1.4F);
            }
        }
    }

    static class DodgeGoal extends ActionGoal<Sculker> {
        DodgeGoal(Sculker mob) {
            super(mob, ACTION_DODGE, DODGE_TIME, 100);
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return distSqr < 3.5 * 3.5 && this.mob.onGround();
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            if (time == 0 && target != null) {
                Vec3 away = this.mob.position().subtract(target.position()).multiply(1, 0, 1).normalize();
                this.mob.setDeltaMovement(away.x * 1.1, 0.4, away.z * 1.1);
                this.mob.hasImpulse = true;
                this.mob.playSound(SoundEvents.BREEZE_JUMP, 1.0F, 1.3F);
            }
        }
    }

    // ------------------------------------------------------------------ stalker
    static class SlashGoal extends ActionGoal<Sculker> {
        private final Set<Entity> dashHit = new HashSet<>();

        SlashGoal(Sculker mob) {
            super(mob, ACTION_SLASH, SLASH_TIME, 50);
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return distSqr < 2.8 * 2.8;
        }

        @Override
        public void start() {
            super.start();
            this.dashHit.clear();
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            if ((time == 5 || time == 11) && target != null && this.mob.distanceToSqr(target) < 3.5 * 3.5) {
                this.mob.swing(time == 5 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
                this.mob.doHurtTarget(target);
            }
            if (time == 16) {
                this.mob.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.8F);
                this.mob.hitAround(1.8, 3.0F);
            }
            if (time == 20 && target != null) {
                Vec3 dir = target.position().subtract(this.mob.position()).multiply(1, 0, 1).normalize();
                this.mob.setDeltaMovement(dir.x * 0.9, 0.1, dir.z * 0.9);
                this.mob.hasImpulse = true;
            }
        }
    }

    static class LungeGoal extends ActionGoal<Sculker> {
        private boolean hit;

        LungeGoal(Sculker mob) {
            super(mob, ACTION_LUNGE, LUNGE_TIME, 90);
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return distSqr > 5 * 5 && distSqr < 12 * 12 && this.mob.onGround() && this.mob.getSensing().hasLineOfSight(target);
        }

        @Override
        public void start() {
            super.start();
            this.hit = false;
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            if (time == 8 && target != null) {
                Vec3 dir = target.position().subtract(this.mob.position()).multiply(1, 0, 1).normalize();
                this.mob.setDeltaMovement(dir.x * 1.6, 0.25, dir.z * 1.6);
                this.mob.hasImpulse = true;
                this.mob.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 0.8F, 1.6F);
            }
            if (time > 8 && !this.hit && target != null && this.mob.getBoundingBox().inflate(0.6).intersects(target.getBoundingBox())) {
                this.hit = true;
                this.mob.doHurtTarget(target);
            }
        }
    }

    // ------------------------------------------------------------------ trapper
    static class TrapGoal extends ActionGoal<Sculker> {
        TrapGoal(Sculker mob) {
            super(mob, ACTION_CAST, CAST_TIME, 220);
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            return distSqr > 3 * 3 && distSqr < 14 * 14 && target.onGround() && this.mob.getSensing().hasLineOfSight(target);
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            if (time == 4) {
                this.mob.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0F, 1.4F);
            }
            if (time == CAST_TIME - 2 && target != null && this.mob.level() instanceof ServerLevel level) {
                BlockPos center = target.blockPosition();
                BlockState barrier = ModBlocks.SPECTRAL_BARRIER.get().defaultBlockState();
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        if (Math.abs(dx) != 2 && Math.abs(dz) != 2) {
                            continue;
                        }
                        for (int dy = 0; dy <= 2; dy++) {
                            BlockPos pos = center.offset(dx, dy, dz);
                            if (level.getBlockState(pos).isAir()) {
                                level.setBlockAndUpdate(pos, barrier);
                            }
                        }
                    }
                }
                level.playSound(null, center, SoundEvents.AMETHYST_BLOCK_RESONATE, net.minecraft.sounds.SoundSource.HOSTILE, 1.5F, 0.8F);
                level.sendParticles(ParticleTypes.WAX_OFF, center.getX() + 0.5, center.getY() + 1, center.getZ() + 0.5, 30, 2, 1, 2, 0.1);
            }
        }
    }
}
