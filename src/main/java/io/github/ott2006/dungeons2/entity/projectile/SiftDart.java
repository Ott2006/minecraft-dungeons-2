package io.github.ott2006.dungeons2.entity.projectile;

import io.github.ott2006.dungeons2.registry.ModEntities;
import io.github.ott2006.dungeons2.registry.ModItems;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Darts fired by sentinels and dartbacks. Sentinel darts quickly rotate towards the nearest hero, then lock on and fly
 * straight.
 */
public class SiftDart extends SiftProjectile {
    @Nullable
    private MobEffectInstance effect;

    public SiftDart(EntityType<? extends SiftDart> type, Level level) {
        super(type, level);
        this.damage = 3.0F;
        this.lifetime = 80;
    }

    public SiftDart(Level level, LivingEntity shooter) {
        super(ModEntities.SIFT_DART.get(), shooter, level);
        this.damage = 3.0F;
        this.lifetime = 80;
    }

    public SiftDart withEffect(MobEffectInstance effect) {
        this.effect = effect;
        return this;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SIFT_DART.get();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.tickCount % 2 == 0) {
            this.level().addParticle(ParticleTypes.WAX_OFF, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    @Override
    protected void onHitEffect(Entity target) {
        if (this.effect != null && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(this.effect), this.getEffectSource());
        }
    }
}
