package io.github.ott2006.dungeons2.entity;

import io.github.ott2006.dungeons2.registry.ModEntities;
import io.github.ott2006.dungeons2.registry.ModFluids;
import io.github.ott2006.dungeons2.registry.ModTags;
import io.github.ott2006.dungeons2.world.gen.ModBiomes;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.pathfinder.PathType;

/**
 * Blub: a soft and squishy, rabbit-like creature of the Sift that squeaks and hops around. Blubs smell tangy and
 * metallic and love bathing in ichor. The default "soul engorged" blub glows; there are also Carapace, Meadows and
 * Ravines variants.
 */
public class Blub extends Animal {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Blub.class, EntityDataSerializers.INT);

    public enum Variant {
        SOUL_ENGORGED("soul_engorged", true),
        CARAPACE("carapace", false),
        MEADOWS("meadows", false),
        RAVINES("ravines", true);

        public final String name;
        public final boolean glows;

        Variant(String name, boolean glows) {
            this.name = name;
            this.glows = glows;
        }

        public static Variant byId(int id) {
            Variant[] values = values();
            return values[Math.floorMod(id, values.length)];
        }
    }

    public Blub(EntityType<? extends Blub> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.LAVA, 0.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.JUMP_STRENGTH, 0.36);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.8));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, s -> s.is(ModTags.Items.BLUB_FOOD), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, new BathInIchorGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public Variant getVariant() {
        return Variant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant.ordinal());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // blubs hop around like rabbits
        if (!this.level().isClientSide && this.onGround() && this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4
                && this.tickCount % 9 == 0) {
            this.getJumpControl().jump();
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData groupData) {
        this.setVariant(pickVariant(level.getBiome(this.blockPosition()), level.getRandom()));
        return super.finalizeSpawn(level, difficulty, spawnType, groupData);
    }

    private static Variant pickVariant(Holder<Biome> biome, RandomSource random) {
        if (random.nextInt(4) == 0) {
            return Variant.SOUL_ENGORGED;
        }
        if (biome.is(ModBiomes.THE_CARAPACE)) {
            return Variant.CARAPACE;
        }
        if (biome.is(ModBiomes.SINGERS_MEADOW) || biome.is(ModBiomes.LULLABY_HILLS)) {
            return Variant.MEADOWS;
        }
        if (biome.is(ModBiomes.ICHOR_RAVINES)) {
            return Variant.RAVINES;
        }
        return Variant.SOUL_ENGORGED;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModTags.Items.BLUB_FOOD);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        Blub baby = ModEntities.BLUB.get().create(level);
        if (baby != null) {
            Variant variant = this.random.nextBoolean() || !(other instanceof Blub blub) ? this.getVariant() : blub.getVariant();
            baby.setVariant(variant);
        }
        return baby;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Variant", this.getVariant().name);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        String name = tag.getString("Variant");
        for (Variant v : Variant.values()) {
            if (v.name.equals(name)) {
                this.setVariant(v);
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RABBIT_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.SLIME_SQUISH_SMALL;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 1.6F;
    }

    public static boolean checkBlubSpawnRules(EntityType<Blub> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                              BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(ModTags.Blocks.SIFT_SPAWNABLE_ON);
    }

    /** Blubs like to wander into ichor pools to bathe. */
    static class BathInIchorGoal extends MoveToBlockGoal {
        private final Blub blub;

        BathInIchorGoal(Blub blub) {
            super(blub, 1.0, 10, 3);
            this.blub = blub;
        }

        @Override
        public boolean canUse() {
            return !this.blub.isBaby() && this.blub.getRandom().nextInt(300) == 0 && super.canUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            return level.getFluidState(pos).getType().isSame(ModFluids.ICHOR.get()) && level.getBlockState(pos.above()).isAir();
        }
    }
}
