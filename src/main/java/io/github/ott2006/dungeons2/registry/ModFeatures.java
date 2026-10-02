package io.github.ott2006.dungeons2.registry;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.world.gen.feature.BoneArchFeature;
import io.github.ott2006.dungeons2.world.gen.feature.DormantEchoGolemFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Dungeons2.MOD_ID);

    public static final DeferredHolder<Feature<?>, BoneArchFeature> BONE_ARCH = FEATURES.register("bone_arch",
            () -> new BoneArchFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, DormantEchoGolemFeature> DORMANT_ECHO_GOLEM = FEATURES.register("dormant_echo_golem",
            () -> new DormantEchoGolemFeature(NoneFeatureConfiguration.CODEC));

    private ModFeatures() {
    }
}
