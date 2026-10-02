package io.github.ott2006.dungeons2.entity.projectile;

import io.github.ott2006.dungeons2.entity.SiftMonster;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Base class of the projectiles fired by creatures of the Sift. They never hit other natives of the Sift, can home in
 * on a target for a while and expire after a lifetime.
 */
public abstract class SiftProjectile extends ThrowableItemProjectile {
    protected float damage = 4.0F;
    protected int lifetime = 120;
    protected int homingTicks;
    protected double homingStrength = 0.25;
    @Nullable
    protected Entity homingTarget;

    protected SiftProjectile(EntityType<? extends SiftProjectile> type, Level level) {
        super(type, level);
    }

    protected SiftProjectile(EntityType<? extends SiftProjectile> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    public SiftProjectile setDamage(float damage) {
        this.damage = damage;
        return this;
    }

    public SiftProjectile setHoming(@Nullable Entity target, int ticks, double strength) {
        this.homingTarget = target;
        this.homingTicks = ticks;
        this.homingStrength = strength;
        return this;
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            if (this.tickCount > this.lifetime) {
                this.expire();
                return;
            }
            if (this.homingTicks > 0 && this.homingTarget != null && this.homingTarget.isAlive()) {
                this.homingTicks--;
                Vec3 motion = this.getDeltaMovement();
                double speed = Math.max(0.2, motion.length());
                Vec3 wanted = this.homingTarget.getBoundingBox().getCenter().subtract(this.position()).normalize().scale(speed);
                Vec3 steered = motion.add(wanted.subtract(motion).scale(this.homingStrength));
                this.setDeltaMovement(steered.normalize().scale(speed));
                this.hasImpulse = true;
            }
        }
        super.tick();
    }

    /** Called when the projectile runs out of time. */
    protected void expire() {
        this.discard();
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!super.canHitEntity(target)) {
            return false;
        }
        Entity owner = this.getOwner();
        if (owner != null && SiftMonster.isSiftNative(owner) && SiftMonster.isSiftNative(target)
                && !(target instanceof io.github.ott2006.dungeons2.entity.EchoGolem)) {
            return false;
        }
        return !(target instanceof SiftProjectile);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity owner = this.getOwner();
        result.getEntity().hurt(this.damageSources().mobProjectile(this, owner instanceof LivingEntity living ? living : null), this.damage);
        this.onHitEffect(result.getEntity());
    }

    /** Extra effect applied to an entity that was hit. */
    protected void onHitEffect(Entity target) {
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.onImpact(result.getLocation());
            this.discard();
        }
    }

    /** Called on the server when the projectile hits anything. */
    protected void onImpact(Vec3 location) {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", this.damage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) {
            this.damage = tag.getFloat("Damage");
        }
    }
}
