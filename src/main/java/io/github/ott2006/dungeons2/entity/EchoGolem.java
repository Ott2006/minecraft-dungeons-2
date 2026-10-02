package io.github.ott2006.dungeons2.entity;

import io.github.ott2006.dungeons2.registry.ModBlocks;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Echo Golem (the "Caretaker Golem"): created by the Singers to assist everyone and powered by soul blocks. Golems
 * found in Lullaby Hills are dormant; awaken them with a soul block and they will follow and protect you, keep you
 * healthy, and dance whenever a Singer sings.
 */
public class EchoGolem extends TamableAnimal {
    private static final EntityDataAccessor<Boolean> DATA_DORMANT = SynchedEntityData.defineId(EchoGolem.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DANCING = SynchedEntityData.defineId(EchoGolem.class, EntityDataSerializers.BOOLEAN);

    private int danceTicks;

    public EchoGolem(EntityType<? extends EchoGolem> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DORMANT, false);
        builder.define(DATA_DANCING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new DormantGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0, 8.0F, 3.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    public boolean isDormant() {
        return this.entityData.get(DATA_DORMANT);
    }

    public void setDormant(boolean dormant) {
        this.entityData.set(DATA_DORMANT, dormant);
    }

    public boolean isDancing() {
        return this.entityData.get(DATA_DANCING);
    }

    public void startDancing(int ticks) {
        if (!this.isDormant()) {
            this.danceTicks = Math.max(this.danceTicks, ticks);
            this.entityData.set(DATA_DANCING, true);
        }
    }

    /** Wakes the golem and binds it to {@code player}. */
    public void awaken(Player player) {
        this.setDormant(false);
        this.tame(player);
        this.setPersistenceRequired();
        this.startDancing(60);
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isDormant();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level() instanceof ServerLevel level) {
            if (this.danceTicks > 0 && --this.danceTicks == 0) {
                this.entityData.set(DATA_DANCING, false);
            }
            if (!this.isDormant() && this.tickCount % 160 == 0) {
                this.caretakerPulse(level);
            }
        } else if (!this.isDormant() && this.random.nextInt(8) == 0) {
            this.level().addParticle(ParticleTypes.SCULK_SOUL, this.getRandomX(0.4), this.getY() + 1.0, this.getRandomZ(0.4), 0, 0.02, 0);
        }
    }

    /** The caretaker keeps its friends healthy. */
    private void caretakerPulse(ServerLevel level) {
        boolean healed = false;
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(8.0),
                e -> e instanceof Player || (e instanceof OwnableEntity o && o.getOwner() != null && o.getOwner() == this.getOwner()))) {
            if (entity.getHealth() < entity.getMaxHealth()) {
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0), this);
                level.sendParticles(ParticleTypes.HEART, entity.getX(), entity.getY() + entity.getBbHeight() + 0.3, entity.getZ(), 1, 0.2, 0.1, 0.2, 0);
                healed = true;
            }
        }
        if (healed) {
            this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, 1.2F);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isDormant()) {
            if (stack.is(ModBlocks.SOUL_BLOCK.get().asItem())) {
                if (this.level() instanceof ServerLevel level) {
                    stack.consume(1, player);
                    this.awaken(player);
                    this.playSound(SoundEvents.BEACON_ACTIVATE, 1.0F, 1.4F);
                    level.sendParticles(ParticleTypes.SCULK_SOUL, this.getX(), this.getY() + 1, this.getZ(), 30, 0.4, 0.8, 0.4, 0.05);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            return InteractionResult.PASS;
        }
        if (this.isTame() && this.isOwnedBy(player)) {
            if (stack.is(Items.ECHO_SHARD) && this.getHealth() < this.getMaxHealth()) {
                if (!this.level().isClientSide) {
                    this.heal(15.0F);
                    stack.consume(1, player);
                    this.playSound(SoundEvents.AMETHYST_BLOCK_RESONATE, 1.0F, 1.2F);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (stack.isEmpty() && player.isShiftKeyDown()) {
                if (!this.level().isClientSide) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.navigation.stop();
                    this.setTarget(null);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canMate(Animal other) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return null;
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        return !(target instanceof EchoGolem) && !(target instanceof Player) && super.wantsToAttack(target, owner);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Dormant", this.isDormant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setDormant(tag.getBoolean("Dormant"));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isDormant() ? null : SoundEvents.AMETHYST_BLOCK_CHIME;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SCULK_BLOCK_STEP, 0.6F, 0.9F);
    }

    @Override
    public float getVoicePitch() {
        return 0.8F + this.random.nextFloat() * 0.4F;
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.9 * this.getEyeHeight(), this.getBbWidth() * 0.4F);
    }

    /** Dormant golems don't do anything. */
    static class DormantGoal extends Goal {
        private final EchoGolem golem;

        DormantGoal(EchoGolem golem) {
            this.golem = golem;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP, Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            return this.golem.isDormant();
        }

        @Override
        public void start() {
            this.golem.getNavigation().stop();
            this.golem.setTarget(null);
        }
    }
}
