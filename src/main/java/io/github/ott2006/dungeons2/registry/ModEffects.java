package io.github.ott2006.dungeons2.registry;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.effect.SimpleEffect;
import io.github.ott2006.dungeons2.effect.SoulBurnEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Dungeons2.MOD_ID);

    /** Soul fire from ichor and soul projectiles: drains life over time. */
    public static final DeferredHolder<MobEffect, MobEffect> SOUL_BURN = MOB_EFFECTS.register("soul_burn",
            () -> new SoulBurnEffect(MobEffectCategory.HARMFUL, 0x3FD8F0));

    /** Granted by the Echo Ocarina: ranged attacks are absorbed by echoes. */
    public static final DeferredHolder<MobEffect, MobEffect> ECHO_WARD = MOB_EFFECTS.register("echo_ward",
            () -> new SimpleEffect(MobEffectCategory.BENEFICIAL, 0x2FD8E6, ParticleTypes.SCULK_CHARGE_POP));

    /** Sifters are enraged when a sprout dies near them: faster, stronger and tougher. */
    public static final DeferredHolder<MobEffect, MobEffect> ENRAGED = MOB_EFFECTS.register("enraged",
            () -> new SimpleEffect(MobEffectCategory.BENEFICIAL, 0xD8262A, ParticleTypes.ANGRY_VILLAGER)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, Dungeons2.id("effect.enraged.speed"), 0.25,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, Dungeons2.id("effect.enraged.damage"), 0.5,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.ARMOR, Dungeons2.id("effect.enraged.armor"), 4.0,
                            AttributeModifier.Operation.ADD_VALUE));

    private ModEffects() {
    }
}
