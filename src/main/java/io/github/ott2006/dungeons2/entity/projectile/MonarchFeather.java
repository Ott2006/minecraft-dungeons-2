package io.github.ott2006.dungeons2.entity.projectile;

import io.github.ott2006.dungeons2.registry.ModEntities;
import io.github.ott2006.dungeons2.registry.ModItems;
import javax.annotation.Nullable;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * A razor-sharp feather of the Monarch. It hovers in the air for a few seconds, then fires at the nearest hero.
 */
public class MonarchFeather extends SiftProjectile {
    private static final Vector3f COLOR = new Vector3f(0.95F, 0.5F, 0.1F);
    private int delay = 100;
    private boolean launched;

    public MonarchFeather(EntityType<? extends MonarchFeather> type, Level level) {
        super(type, level);
        this.damage = 5.0F;
    }

    public MonarchFeather(Level level, LivingEntity owner, Vec3 pos, int delay) {
        super(ModEntities.MONARCH_FEATHER.get(), owner, level);
        this.setPos(pos);
        this.damage = 5.0F;
        this.delay = delay;
        this.lifetime = delay + 80;
        this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.MONARCH_FEATHER.get();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    public void tick() {
        if (!this.launched) {
            this.setDeltaMovement(new Vec3(0, Math.sin(this.tickCount * 0.3) * 0.02, 0));
            if (!this.level().isClientSide && this.tickCount >= this.delay) {
                this.launch();
            }
        }
        if (this.level().isClientSide && this.tickCount % 2 == 0) {
            this.level().addParticle(new DustParticleOptions(COLOR, 0.8F), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
        super.tick();
    }

    private void launch() {
        this.launched = true;
        LivingEntity target = this.findTarget();
        Vec3 dir;
        if (target != null) {
            dir = target.getBoundingBox().getCenter().subtract(this.position()).normalize();
        } else {
            dir = new Vec3(this.random.nextDouble() - 0.5, -0.3, this.random.nextDouble() - 0.5).normalize();
        }
        this.setDeltaMovement(dir.scale(1.3));
        this.hasImpulse = true;
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PHANTOM_FLAP, SoundSource.HOSTILE, 0.6F, 1.6F);
    }

    @Nullable
    private LivingEntity findTarget() {
        if (this.getOwner() instanceof Mob mob && mob.getTarget() != null && mob.getTarget().distanceToSqr(this) < 32 * 32) {
            return mob.getTarget();
        }
        Player player = this.level().getNearestPlayer(this, 32.0);
        return player != null && !player.isCreative() && !player.isSpectator() ? player : null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Delay", this.delay);
        tag.putBoolean("Launched", this.launched);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.delay = tag.getInt("Delay");
        this.launched = tag.getBoolean("Launched");
    }
}
