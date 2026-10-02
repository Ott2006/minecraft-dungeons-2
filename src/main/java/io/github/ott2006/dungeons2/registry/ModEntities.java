package io.github.ott2006.dungeons2.registry;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.entity.Blub;
import io.github.ott2006.dungeons2.entity.Dartback;
import io.github.ott2006.dungeons2.entity.EchoGolem;
import io.github.ott2006.dungeons2.entity.Harmonizer;
import io.github.ott2006.dungeons2.entity.Monarch;
import io.github.ott2006.dungeons2.entity.Nester;
import io.github.ott2006.dungeons2.entity.Pollinator;
import io.github.ott2006.dungeons2.entity.Sculker;
import io.github.ott2006.dungeons2.entity.Seedling;
import io.github.ott2006.dungeons2.entity.Sentinel;
import io.github.ott2006.dungeons2.entity.Singer;
import io.github.ott2006.dungeons2.entity.Sprout;
import io.github.ott2006.dungeons2.entity.projectile.GooGlob;
import io.github.ott2006.dungeons2.entity.projectile.MonarchFeather;
import io.github.ott2006.dungeons2.entity.projectile.SiftDart;
import io.github.ott2006.dungeons2.entity.projectile.SoulWisp;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Dungeons2.MOD_ID);

    // ------------------------------------------------------------------ friendly
    public static final DeferredHolder<EntityType<?>, EntityType<Blub>> BLUB = register("blub",
            EntityType.Builder.of(Blub::new, MobCategory.CREATURE).sized(0.5F, 0.55F).eyeHeight(0.4F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<Singer>> SINGER = register("singer",
            EntityType.Builder.of(Singer::new, MobCategory.CREATURE).sized(1.1F, 2.9F).eyeHeight(2.65F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<EchoGolem>> ECHO_GOLEM = register("echo_golem",
            EntityType.Builder.of(EchoGolem::new, MobCategory.MISC).sized(0.9F, 1.7F).eyeHeight(1.45F).clientTrackingRange(10));

    // ------------------------------------------------------------------ sifters
    public static final DeferredHolder<EntityType<?>, EntityType<Seedling>> SEEDLING = register("seedling",
            EntityType.Builder.of(Seedling::new, MobCategory.MONSTER).sized(0.6F, 0.6F).eyeHeight(0.4F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<Sentinel>> SENTINEL = register("sentinel",
            EntityType.Builder.of(Sentinel::new, MobCategory.MONSTER).sized(0.7F, 1.9F).eyeHeight(1.65F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<Pollinator>> POLLINATOR = register("pollinator",
            EntityType.Builder.of(Pollinator::new, MobCategory.MONSTER).sized(1.0F, 0.9F).eyeHeight(0.75F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<Nester>> NESTER = register("nester",
            EntityType.Builder.of(Nester::new, MobCategory.MONSTER).sized(0.9F, 1.6F).eyeHeight(1.3F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<Sprout>> SPROUT = register("sprout",
            EntityType.Builder.of(Sprout::new, MobCategory.MONSTER).sized(0.8F, 1.3F).eyeHeight(0.9F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<Harmonizer>> HARMONIZER = register("harmonizer",
            EntityType.Builder.of(Harmonizer::new, MobCategory.MONSTER).sized(2.0F, 3.2F).eyeHeight(2.2F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<Dartback>> DARTBACK = register("dartback",
            EntityType.Builder.of(Dartback::new, MobCategory.MONSTER).sized(2.4F, 2.0F).eyeHeight(1.5F).clientTrackingRange(10));

    // ------------------------------------------------------------------ sculkers
    public static final DeferredHolder<EntityType<?>, EntityType<Sculker>> HUNTER = register("hunter",
            EntityType.Builder.<Sculker>of((t, l) -> new Sculker(t, l, Sculker.Kind.HUNTER), MobCategory.MONSTER)
                    .sized(0.7F, 2.0F).eyeHeight(1.75F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<Sculker>> SCAVENGER = register("scavenger",
            EntityType.Builder.<Sculker>of((t, l) -> new Sculker(t, l, Sculker.Kind.SCAVENGER), MobCategory.MONSTER)
                    .sized(0.7F, 1.9F).eyeHeight(1.65F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<Sculker>> STALKER = register("stalker",
            EntityType.Builder.<Sculker>of((t, l) -> new Sculker(t, l, Sculker.Kind.STALKER), MobCategory.MONSTER)
                    .sized(0.7F, 2.0F).eyeHeight(1.75F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<Sculker>> TRAPPER = register("trapper",
            EntityType.Builder.<Sculker>of((t, l) -> new Sculker(t, l, Sculker.Kind.TRAPPER), MobCategory.MONSTER)
                    .sized(0.7F, 1.9F).eyeHeight(1.65F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<Monarch>> MONARCH = register("monarch",
            EntityType.Builder.of(Monarch::new, MobCategory.MONSTER).sized(1.6F, 4.4F).eyeHeight(4.0F).clientTrackingRange(10));

    // ------------------------------------------------------------------ projectiles
    public static final DeferredHolder<EntityType<?>, EntityType<SiftDart>> SIFT_DART = register("sift_dart",
            EntityType.Builder.<SiftDart>of(SiftDart::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(6).updateInterval(2));
    public static final DeferredHolder<EntityType<?>, EntityType<GooGlob>> GOO_GLOB = register("goo_glob",
            EntityType.Builder.<GooGlob>of(GooGlob::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(5));
    public static final DeferredHolder<EntityType<?>, EntityType<SoulWisp>> SOUL_WISP = register("soul_wisp",
            EntityType.Builder.<SoulWisp>of(SoulWisp::new, MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(6).updateInterval(2));
    public static final DeferredHolder<EntityType<?>, EntityType<MonarchFeather>> MONARCH_FEATHER = register("monarch_feather",
            EntityType.Builder.<MonarchFeather>of(MonarchFeather::new, MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(6).updateInterval(2));

    private ModEntities() {
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(name, () -> builder.build(name));
    }
}
