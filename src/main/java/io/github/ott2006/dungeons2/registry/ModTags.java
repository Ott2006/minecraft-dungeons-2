package io.github.ott2006.dungeons2.registry;

import io.github.ott2006.dungeons2.Dungeons2;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    private ModTags() {
    }

    public static final class Blocks {
        /** Blocks that can frame a Deep Dark portal to the Sift (reinforced deepslate and resonant deepslate). */
        public static final TagKey<Block> SIFT_PORTAL_FRAMES = TagKey.create(Registries.BLOCK, Dungeons2.id("sift_portal_frames"));
        public static final TagKey<Block> WHITE_WILLOW_LOGS = TagKey.create(Registries.BLOCK, Dungeons2.id("white_willow_logs"));
        /** Soil the plants of the Sift grow on. */
        public static final TagKey<Block> SIFT_SOIL = TagKey.create(Registries.BLOCK, Dungeons2.id("sift_soil"));
        /** Blocks the caves and canyons of the Sift carve through. */
        public static final TagKey<Block> SIFT_CARVER_REPLACEABLES = TagKey.create(Registries.BLOCK, Dungeons2.id("sift_carver_replaceables"));
        /** Blocks Sift mobs can spawn on. */
        public static final TagKey<Block> SIFT_SPAWNABLE_ON = TagKey.create(Registries.BLOCK, Dungeons2.id("sift_spawnable_on"));

        private Blocks() {
        }
    }

    public static final class Items {
        public static final TagKey<Item> BLUB_FOOD = TagKey.create(Registries.ITEM, Dungeons2.id("blub_food"));
        public static final TagKey<Item> WHITE_WILLOW_LOGS = TagKey.create(Registries.ITEM, Dungeons2.id("white_willow_logs"));

        private Items() {
        }
    }

    public static final class Entities {
        /** Creatures native to the Sift. They are immune to ichor and do not fight each other. */
        public static final TagKey<EntityType<?>> SIFT_NATIVES = TagKey.create(Registries.ENTITY_TYPE, Dungeons2.id("sift_natives"));
        /** Sculk creatures. Sculker's Bane deals extra damage to them. */
        public static final TagKey<EntityType<?>> SCULKERS = TagKey.create(Registries.ENTITY_TYPE, Dungeons2.id("sculkers"));

        private Entities() {
        }
    }

    public static final class Biomes {
        public static final TagKey<Biome> IS_SIFT = TagKey.create(Registries.BIOME, Dungeons2.id("is_sift"));

        private Biomes() {
        }
    }
}
