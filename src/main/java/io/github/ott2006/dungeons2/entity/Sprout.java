package io.github.ott2006.dungeons2.entity;

import io.github.ott2006.dungeons2.entity.ai.ActionGoal;
import io.github.ott2006.dungeons2.registry.ModEffects;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Sprout: a squid-like, floating support sifter. It channels a flashing beam into a nearby ally (preferring
 * pollinators) to strengthen it; damage interrupts the beam. When a sprout dies, nearby sifters become enraged.
 */
public class Sprout extends SiftMonster implements Sifter {
    public static final int ACTION_BEAM = 1;
    public static final int BEAM_TIME = 40;
    private static final EntityDataAccessor<Integer> DATA_BEAM_TARGET = SynchedEntityData.defineId(Sprout.class, EntityDataSerializers.INT);

    protected boolean beamInterrupted;

    public Sprout(EntityType<? extends Sprout> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.FLYING_SPEED, 0.45)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BEAM_TARGET, -1);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new BuffAllyGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(5, new StayNearAlliesGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addDefaultTargets(1);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isControlledByLocalInstance()) {
            if (this.isInWater() || this.isInLava()) {
                this.moveRelative(0.02F, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.6F));
            } else {
                this.moveRelative(this.getSpeed(), travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.91F));
            }
        }
        this.calculateEntityAnimation(false);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    public int getBeamTargetId() {
        return this.entityData.get(DATA_BEAM_TARGET);
    }

    public void setBeamTarget(@Nullable Entity entity) {
        this.entityData.set(DATA_BEAM_TARGET, entity == null ? -1 : entity.getId());
    }

    @Nullable
    public Entity getBeamTarget() {
        int id = this.getBeamTargetId();
        return id < 0 ? null : this.level().getEntity(id);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            Entity beamTarget = this.getBeamTarget();
            if (beamTarget != null) {
                Vec3 from = this.position().add(0, this.getBbHeight() * 0.3, 0);
                Vec3 to = beamTarget.getBoundingBox().getCenter();
                Vec3 delta = to.subtract(from);
                int steps = Mth.ceil(delta.length() * 2);
                float hue = (this.tickCount % 12) / 12.0F;
                int rgb = Mth.hsvToRgb(hue, 0.7F, 1.0F);
                Vector3f color = new Vector3f(((rgb >> 16) & 255) / 255F, ((rgb >> 8) & 255) / 255F, (rgb & 255) / 255F);
                for (int i = 0; i < steps; i += 2) {
                    Vec3 p = from.add(delta.scale((i + this.random.nextFloat()) / steps));
                    this.level().addParticle(new DustParticleOptions(color, 0.9F), p.x, p.y, p.z, 0, 0, 0);
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && this.getAction() == ACTION_BEAM) {
            this.beamInterrupted = true;
        }
        return hurt;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (this.level() instanceof ServerLevel level) {
            for (SiftMonster ally : level.getEntitiesOfClass(SiftMonster.class, this.getBoundingBox().inflate(10.0),
                    e -> e != this && e instanceof Sifter && e.isAlive())) {
                ally.addEffect(new MobEffectInstance(ModEffects.ENRAGED, 600, 0));
                ally.heal(4.0F);
                level.sendParticles(ParticleTypes.ANGRY_VILLAGER, ally.getX(), ally.getY() + ally.getBbHeight(), ally.getZ(), 3, 0.3, 0.2, 0.3, 0);
            }
            this.playSound(SoundEvents.SCULK_SHRIEKER_SHRIEK, 1.0F, 1.6F);
        }
    }

    @Nullable
    protected SiftMonster findAllyToBuff(double range) {
        List<SiftMonster> allies = this.level().getEntitiesOfClass(SiftMonster.class, this.getBoundingBox().inflate(range),
                e -> e != this && e.isAlive() && e instanceof Sifter && !(e instanceof Sprout) && !e.hasEffect(MobEffects.DAMAGE_BOOST));
        return allies.stream()
                .min(Comparator.<SiftMonster>comparingInt(e -> e instanceof Pollinator ? 0 : 1).thenComparingDouble(this::distanceToSqr))
                .orElse(null);
    }

    protected void buff(LivingEntity ally, int duration) {
        ally.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 0));
        ally.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 0));
        ally.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 0));
        if (this.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.WAX_ON, ally.getX(), ally.getY(0.5), ally.getZ(), 12, 0.4, 0.5, 0.4, 0.05);
        }
        this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, 1.5F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.GLOW_SQUID_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GLOW_SQUID_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GLOW_SQUID_DEATH;
    }

    static class BuffAllyGoal extends ActionGoal<Sprout> {
        @Nullable
        private SiftMonster ally;

        BuffAllyGoal(Sprout mob) {
            super(mob, ACTION_BEAM, BEAM_TIME, 120, 20, EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        protected boolean canStart(LivingEntity target, double distSqr) {
            this.ally = this.mob.findAllyToBuff(12.0);
            return this.ally != null;
        }

        @Override
        public void start() {
            super.start();
            this.mob.beamInterrupted = false;
            this.mob.setBeamTarget(this.ally);
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.ally != null && this.ally.isAlive() && !this.mob.beamInterrupted
                    && this.mob.distanceToSqr(this.ally) < 16 * 16;
        }

        @Override
        protected void onTick(int time, LivingEntity target) {
            if (this.ally == null) {
                return;
            }
            this.mob.getNavigation().stop();
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.5));
            this.mob.getLookControl().setLookAt(this.ally, 30.0F, 30.0F);
            if (time == BEAM_TIME - 1) {
                this.mob.buff(this.ally, 240);
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.mob.setBeamTarget(null);
            this.ally = null;
        }
    }

    static class StayNearAlliesGoal extends Goal {
        private final Sprout mob;
        @Nullable
        private SiftMonster ally;

        StayNearAlliesGoal(Sprout mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.mob.getRandom().nextInt(20) != 0) {
                return false;
            }
            this.ally = this.mob.level().getEntitiesOfClass(SiftMonster.class, this.mob.getBoundingBox().inflate(24.0),
                            e -> e != this.mob && e instanceof Sifter && !(e instanceof Sprout))
                    .stream().min(Comparator.comparingDouble(this.mob::distanceToSqr)).orElse(null);
            return this.ally != null && this.mob.distanceToSqr(this.ally) > 6 * 6;
        }

        @Override
        public boolean canContinueToUse() {
            return this.ally != null && this.ally.isAlive() && this.mob.distanceToSqr(this.ally) > 4 * 4
                    && !this.mob.getNavigation().isDone();
        }

        @Override
        public void start() {
            if (this.ally != null) {
                this.mob.getNavigation().moveTo(this.ally.getX(), this.ally.getY() + 2.0, this.ally.getZ(), 1.0);
            }
        }
    }
}
