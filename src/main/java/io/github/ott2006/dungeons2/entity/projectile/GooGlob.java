package io.github.ott2006.dungeons2.entity.projectile;

import io.github.ott2006.dungeons2.entity.SiftMonster;
import io.github.ott2006.dungeons2.registry.ModEntities;
import io.github.ott2006.dungeons2.registry.ModItems;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * The large glob a pollinator regurgitates. It bursts on impact, leaving sticky yellow goo on the ground.
 */
public class GooGlob extends SiftProjectile {
    private static final Vector3f GOO_COLOR = new Vector3f(0.95F, 0.85F, 0.2F);

    public GooGlob(EntityType<? extends GooGlob> type, Level level) {
        super(type, level);
        this.damage = 3.0F;
    }

    public GooGlob(Level level, LivingEntity shooter) {
        super(ModEntities.GOO_GLOB.get(), shooter, level);
        this.damage = 3.0F;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.GOO_GLOB.get();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
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
        level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ModItems.GOO_GLOB.get())), at.x, at.y, at.z,
                16, 0.4, 0.3, 0.4, 0.15);
        level.playSound(null, at.x, at.y, at.z, SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1.0F, 0.6F);
        level.playSound(null, at.x, at.y, at.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 0.4F, 1.6F);
        Entity owner = this.getOwner();
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0))) {
            if (!SiftMonster.isSiftNative(entity) || entity instanceof io.github.ott2006.dungeons2.entity.EchoGolem) {
                entity.hurt(this.damageSources().mobProjectile(this, owner instanceof LivingEntity l ? l : null), 3.0F);
            }
        }
        AreaEffectCloud cloud = new AreaEffectCloud(level, at.x, at.y, at.z);
        if (owner instanceof LivingEntity living) {
            cloud.setOwner(living);
        }
        cloud.setParticle(new DustParticleOptions(GOO_COLOR, 1.2F));
        cloud.setRadius(2.2F);
        cloud.setRadiusOnUse(-0.1F);
        cloud.setWaitTime(5);
        cloud.setDuration(100);
        cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
        cloud.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
        level.addFreshEntity(cloud);
    }
}
