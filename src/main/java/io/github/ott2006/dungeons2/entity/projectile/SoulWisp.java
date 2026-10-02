package io.github.ott2006.dungeons2.entity.projectile;

import io.github.ott2006.dungeons2.registry.ModEffects;
import io.github.ott2006.dungeons2.registry.ModEntities;
import io.github.ott2006.dungeons2.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * A flying soul thrown by scavengers. It tracks its target and detonates on impact, leaving a pool of soul fire.
 */
public class SoulWisp extends SiftProjectile {
    public SoulWisp(EntityType<? extends SoulWisp> type, Level level) {
        super(type, level);
        this.lifetime = 100;
    }

    public SoulWisp(Level level, LivingEntity shooter) {
        super(ModEntities.SOUL_WISP.get(), shooter, level);
        this.lifetime = 100;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SOUL_WISP.get();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 0.1, this.getZ(), 0, 0.01, 0);
        }
    }

    @Override
    protected void expire() {
        this.onImpact(this.position());
        super.expire();
    }

    @Override
    protected void onImpact(Vec3 at) {
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        level.sendParticles(ParticleTypes.SCULK_SOUL, at.x, at.y, at.z, 12, 0.3, 0.3, 0.3, 0.05);
        level.playSound(null, at.x, at.y, at.z, SoundEvents.SOUL_ESCAPE.value(), SoundSource.HOSTILE, 1.0F, 1.2F);
        AreaEffectCloud pool = new AreaEffectCloud(level, at.x, at.y, at.z);
        if (this.getOwner() instanceof LivingEntity living) {
            pool.setOwner(living);
        }
        pool.setParticle(ParticleTypes.SOUL_FIRE_FLAME);
        pool.setRadius(1.6F);
        pool.setWaitTime(0);
        pool.setDuration(80);
        pool.setRadiusPerTick(-0.005F);
        pool.addEffect(new MobEffectInstance(ModEffects.SOUL_BURN, 60, 0));
        level.addFreshEntity(pool);
    }
}
