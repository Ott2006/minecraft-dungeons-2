package io.github.ott2006.dungeons2.registry;

import io.github.ott2006.dungeons2.Dungeons2;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public final class ModDamageTypes {
    public static final ResourceKey<DamageType> SOUL_BURN = ResourceKey.create(Registries.DAMAGE_TYPE, Dungeons2.id("soul_burn"));

    private ModDamageTypes() {
    }

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(SOUL_BURN, new DamageType("dungeons2.soulBurn", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
                0.0F, DamageEffects.BURNING));
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> key) {
        return level.damageSources().source(key);
    }
}
