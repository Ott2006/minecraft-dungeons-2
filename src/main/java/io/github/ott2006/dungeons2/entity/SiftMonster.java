package io.github.ott2006.dungeons2.entity;

import io.github.ott2006.dungeons2.registry.ModEffects;
import io.github.ott2006.dungeons2.registry.ModTags;
import java.util.List;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

/**
 * Base class of all hostile creatures of the Sift (sifters and sculkers).
 * <ul>
 *     <li>Natives of the Sift are allied with each other and immune to ichor.</li>
 *     <li>A synchronised "action" id drives the attack animations on the client.</li>
 *     <li>Enraged monsters (see {@link ModEffects#ENRAGED}) are rendered with a red glow.</li>
 * </ul>
 */
public abstract class SiftMonster extends Monster {
    private static final EntityDataAccessor<Integer> DATA_ACTION = SynchedEntityData.defineId(SiftMonster.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ENRAGED = SynchedEntityData.defineId(SiftMonster.class, EntityDataSerializers.BOOLEAN);

    public static final int ACTION_NONE = 0;

    private int actionStartTick;

    protected SiftMonster(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.LAVA, 8.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 8.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ACTION, ACTION_NONE);
        builder.define(DATA_ENRAGED, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_ACTION.equals(key)) {
            this.actionStartTick = this.tickCount;
        }
    }

    /** Adds the default targets: heroes, villagers, golems and echo golems. */
    protected void addDefaultTargets(int priority) {
        this.targetSelector.addGoal(priority, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(priority + 1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(priority + 2, new NearestAttackableTargetGoal<>(this, EchoGolem.class, true));
        this.targetSelector.addGoal(priority + 3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(priority + 3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public int getAction() {
        return this.entityData.get(DATA_ACTION);
    }

    public void setAction(int action) {
        this.entityData.set(DATA_ACTION, action);
        this.actionStartTick = this.tickCount;
    }

    /** Ticks (plus partial tick) since the current action started. */
    public float getActionTime(float partialTick) {
        return this.tickCount - this.actionStartTick + partialTick;
    }

    public boolean isEnraged() {
        return this.entityData.get(DATA_ENRAGED);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            boolean enraged = this.hasEffect(ModEffects.ENRAGED);
            if (enraged != this.isEnraged()) {
                this.entityData.set(DATA_ENRAGED, enraged);
            }
        }
    }

    public static boolean isSiftNative(Entity entity) {
        return entity.getType().is(ModTags.Entities.SIFT_NATIVES);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (entity instanceof Monster && isSiftNative(entity)) {
            return this.getTeam() == null && entity.getTeam() == null || super.isAlliedTo(entity);
        }
        return super.isAlliedTo(entity);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (isSiftNative(target) && !(target instanceof EchoGolem)) {
            return false;
        }
        return super.canAttack(target);
    }

    /** Spawns a few helpers around this monster. Returns the spawned mobs. */
    protected List<Mob> summon(ServerLevel level, List<EntityType<? extends Mob>> types) {
        List<Mob> spawned = new java.util.ArrayList<>();
        RandomSource random = this.getRandom();
        for (EntityType<? extends Mob> type : types) {
            for (int attempt = 0; attempt < 8; attempt++) {
                double angle = random.nextDouble() * Math.PI * 2;
                double dist = 2.0 + random.nextDouble() * 3.0;
                BlockPos pos = BlockPos.containing(this.getX() + Math.cos(angle) * dist, this.getY() + 1, this.getZ() + Math.sin(angle) * dist);
                for (int dy = 0; dy < 4 && !level.getBlockState(pos.below()).isSolid(); dy++) {
                    pos = pos.below();
                }
                if (!level.noCollision(type.getSpawnAABB(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5))) {
                    continue;
                }
                Mob mob = type.create(level);
                if (mob == null) {
                    break;
                }
                mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, random.nextFloat() * 360F, 0);
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
                if (this.getTarget() != null) {
                    mob.setTarget(this.getTarget());
                }
                level.addFreshEntity(mob);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SCULK_SOUL, mob.getX(), mob.getY() + 0.5, mob.getZ(), 10, 0.3, 0.5, 0.3, 0.02);
                spawned.add(mob);
                break;
            }
        }
        return spawned;
    }

    /** Spawn rule used by all Sift monsters: any light level, solid ground. */
    public static boolean checkSiftMonsterSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor level, MobSpawnType spawnType,
                                                     BlockPos pos, RandomSource random) {
        return Monster.checkAnyLightMonsterSpawnRules(type, level, spawnType, pos, random)
                && (spawnType != MobSpawnType.NATURAL || level.getBlockState(pos.below()).is(ModTags.Blocks.SIFT_SPAWNABLE_ON));
    }

    /** Pushes {@code target} away from this entity. */
    protected void knockAway(LivingEntity target, double strength, double up) {
        Vec3 dir = target.position().subtract(this.position()).multiply(1, 0, 1);
        if (dir.lengthSqr() < 1.0E-4) {
            dir = new Vec3(1, 0, 0);
        }
        dir = dir.normalize().scale(strength);
        target.push(dir.x, up, dir.z);
        target.hurtMarked = true;
    }

    /** Pulls {@code target} towards this entity. */
    protected void pullTowards(LivingEntity target, double strength) {
        Vec3 dir = this.position().subtract(target.position());
        double len = dir.length();
        if (len < 1.0E-3) {
            return;
        }
        Vec3 v = dir.scale(strength / len);
        target.setDeltaMovement(v.x, Math.min(0.5, 0.2 + v.y * 0.3), v.z);
        target.hurtMarked = true;
    }
}
