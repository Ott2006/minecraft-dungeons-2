package io.github.ott2006.dungeons2.entity;

import io.github.ott2006.dungeons2.registry.ModBlocks;
import io.github.ott2006.dungeons2.registry.ModEntities;
import io.github.ott2006.dungeons2.registry.ModTags;
import io.github.ott2006.dungeons2.world.SiftSong;
import java.util.EnumSet;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;

/**
 * Singer: a tall, gentle creature of the Sift with pale green shaggy fur, a tiny face on an elongated neck and pale
 * antlers. Singers communicate only through song. Their song pacifies wardens and is able to open the Deep Dark
 * portal. Singers created the echo golems: give a singer an echo shard while a soul block is nearby and it will sing a
 * new echo golem into existence.
 */
public class Singer extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> DATA_SINGING = SynchedEntityData.defineId(Singer.class, EntityDataSerializers.BOOLEAN);
    public static final int SONG_LENGTH = 80;

    private int songTime;
    private int nextSong = 400;

    public Singer(EntityType<? extends Singer> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.LAVA, 4.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SINGING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SingGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public boolean isSinging() {
        return this.entityData.get(DATA_SINGING);
    }

    public void startSinging() {
        if (!this.isSinging()) {
            this.songTime = 0;
            this.entityData.set(DATA_SINGING, true);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level() instanceof ServerLevel level) {
            if (this.isSinging()) {
                if (this.songTime % 8 == 0) {
                    SiftSong.playNote(level, this, this.songTime / 8, 1.5F);
                }
                if (this.songTime % 20 == 0) {
                    SiftSong.soothe(level, this, 24.0);
                    for (Player player : level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10.0))) {
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
                    }
                }
                if (++this.songTime >= SONG_LENGTH) {
                    this.entityData.set(DATA_SINGING, false);
                    this.nextSong = 600 + this.random.nextInt(900);
                }
            } else if (--this.nextSong <= 0) {
                this.startSinging();
            }
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.ECHO_SHARD)) {
            if (this.level() instanceof ServerLevel level) {
                Optional<BlockPos> soulBlock = BlockPos.findClosestMatch(this.blockPosition(), 5, 3,
                        p -> level.getBlockState(p).is(ModBlocks.SOUL_BLOCK.get()));
                if (soulBlock.isEmpty()) {
                    player.displayClientMessage(Component.translatable("message.dungeons2.singer.needs_soul_block")
                            .withStyle(ChatFormatting.AQUA), true);
                    return InteractionResult.CONSUME;
                }
                BlockPos pos = soulBlock.get();
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                EchoGolem golem = ModEntities.ECHO_GOLEM.get().create(level);
                if (golem != null) {
                    golem.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, this.random.nextFloat() * 360F, 0);
                    golem.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
                    golem.awaken(player);
                    level.addFreshEntity(golem);
                    level.sendParticles(ParticleTypes.SCULK_SOUL, golem.getX(), golem.getY() + 1, golem.getZ(), 40, 0.5, 0.8, 0.5, 0.05);
                }
                stack.consume(1, player);
                this.startSinging();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (stack.isEmpty() && !this.isSinging()) {
            if (!this.level().isClientSide) {
                this.startSinging();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isSinging() ? null : SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return 0.55F;
    }

    public static boolean checkSingerSpawnRules(EntityType<Singer> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                                BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(ModTags.Blocks.SIFT_SPAWNABLE_ON);
    }

    /** While singing, the singer stands still and looks towards the sky. */
    static class SingGoal extends Goal {
        private final Singer singer;

        SingGoal(Singer singer) {
            this.singer = singer;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.singer.isSinging();
        }

        @Override
        public void start() {
            this.singer.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.singer.getLookControl().setLookAt(this.singer.getX(), this.singer.getEyeY() + 3.0,
                    this.singer.getZ() + (this.singer.getYRot() > 0 ? 1 : -1));
        }
    }
}
