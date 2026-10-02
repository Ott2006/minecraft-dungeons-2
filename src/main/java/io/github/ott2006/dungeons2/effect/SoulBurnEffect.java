package io.github.ott2006.dungeons2.effect;

import io.github.ott2006.dungeons2.registry.ModDamageTypes;
import io.github.ott2006.dungeons2.world.IchorHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/** Soul fire: the curse of ichor. Deals damage every second and ignores armor. */
public class SoulBurnEffect extends MobEffect {
    public SoulBurnEffect(MobEffectCategory category, int color) {
        super(category, color, ParticleTypes.SOUL_FIRE_FLAME);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (IchorHandler.isImmune(entity)) {
            return false;
        }
        if (entity.level() instanceof ServerLevel level) {
            entity.hurt(ModDamageTypes.source(level, ModDamageTypes.SOUL_BURN), 1.0F + amplifier);
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, entity.getX(), entity.getY() + entity.getBbHeight() * 0.5,
                    entity.getZ(), 4, entity.getBbWidth() * 0.4, entity.getBbHeight() * 0.3, entity.getBbWidth() * 0.4, 0.01);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int interval = Math.max(5, 20 >> amplifier);
        return duration % interval == 0;
    }
}
