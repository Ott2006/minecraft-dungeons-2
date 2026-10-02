package io.github.ott2006.dungeons2.effect;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/** A mob effect without tick logic; its behaviour lives in attribute modifiers or event handlers. */
public class SimpleEffect extends MobEffect {
    public SimpleEffect(MobEffectCategory category, int color, ParticleOptions particle) {
        super(category, color, particle);
    }
}
