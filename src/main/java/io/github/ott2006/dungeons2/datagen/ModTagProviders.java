package io.github.ott2006.dungeons2.datagen;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.registry.ModBlocks;
import io.github.ott2006.dungeons2.registry.ModDamageTypes;
import io.github.ott2006.dungeons2.registry.ModEntities;
import io.github.ott2006.dungeons2.registry.ModItems;
import io.github.ott2006.dungeons2.registry.ModTags;
import io.github.ott2006.dungeons2.world.gen.ModBiomes;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModTagProviders {
    private ModTagProviders() {
    }

    public static class Blocks extends BlockTagsProvider {
        public Blocks(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper files) {
            super(output, lookup, Dungeons2.MOD_ID, files);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            Block[] stone = {
                    ModBlocks.SIFTSTONE.get(), ModBlocks.COBBLED_SIFTSTONE.get(), ModBlocks.COBBLED_SIFTSTONE_STAIRS.get(),
                    ModBlocks.COBBLED_SIFTSTONE_SLAB.get(), ModBlocks.COBBLED_SIFTSTONE_WALL.get(), ModBlocks.POLISHED_SIFTSTONE.get(),
                    ModBlocks.POLISHED_SIFTSTONE_STAIRS.get(), ModBlocks.POLISHED_SIFTSTONE_SLAB.get(), ModBlocks.POLISHED_SIFTSTONE_WALL.get(),
                    ModBlocks.SIFTSTONE_BRICKS.get(), ModBlocks.SIFTSTONE_BRICK_STAIRS.get(), ModBlocks.SIFTSTONE_BRICK_SLAB.get(),
                    ModBlocks.SIFTSTONE_BRICK_WALL.get(), ModBlocks.CHISELED_SIFTSTONE.get(), ModBlocks.CARAPACE_SANDSTONE.get(),
                    ModBlocks.RESONANT_DEEPSLATE.get(), ModBlocks.SOUL_BLOCK.get()};
            Block[] soil = {ModBlocks.GREEN_HEALTHY_SCULK.get(), ModBlocks.ORANGE_HEALTHY_SCULK.get(),
                    ModBlocks.ORANGE_SCULK_GRASS_BLOCK.get(), ModBlocks.LIGHT_ORANGE_SCULK_GRASS_BLOCK.get(), ModBlocks.GREEN_SCULK_GRASS_BLOCK.get()};
            Block[] grassBlocks = {ModBlocks.ORANGE_SCULK_GRASS_BLOCK.get(), ModBlocks.LIGHT_ORANGE_SCULK_GRASS_BLOCK.get(),
                    ModBlocks.GREEN_SCULK_GRASS_BLOCK.get()};

            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(stone).add(ModBlocks.SIFTSTONE_COAL_ORE.get(), ModBlocks.SIFTSTONE_IRON_ORE.get(),
                    ModBlocks.SIFTSTONE_COPPER_ORE.get(), ModBlocks.SIFTSTONE_GOLD_ORE.get(), ModBlocks.SIFTSTONE_DIAMOND_ORE.get());
            this.tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.SIFTSTONE_IRON_ORE.get(), ModBlocks.SIFTSTONE_COPPER_ORE.get());
            this.tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.SIFTSTONE_GOLD_ORE.get(), ModBlocks.SIFTSTONE_DIAMOND_ORE.get());
            this.tag(BlockTags.COAL_ORES).add(ModBlocks.SIFTSTONE_COAL_ORE.get());
            this.tag(BlockTags.IRON_ORES).add(ModBlocks.SIFTSTONE_IRON_ORE.get());
            this.tag(BlockTags.COPPER_ORES).add(ModBlocks.SIFTSTONE_COPPER_ORE.get());
            this.tag(BlockTags.GOLD_ORES).add(ModBlocks.SIFTSTONE_GOLD_ORE.get());
            this.tag(BlockTags.DIAMOND_ORES).add(ModBlocks.SIFTSTONE_DIAMOND_ORE.get());
            this.tag(net.neoforged.neoforge.common.Tags.Blocks.ORES_COAL).add(ModBlocks.SIFTSTONE_COAL_ORE.get());
            this.tag(net.neoforged.neoforge.common.Tags.Blocks.ORES_IRON).add(ModBlocks.SIFTSTONE_IRON_ORE.get());
            this.tag(net.neoforged.neoforge.common.Tags.Blocks.ORES_COPPER).add(ModBlocks.SIFTSTONE_COPPER_ORE.get());
            this.tag(net.neoforged.neoforge.common.Tags.Blocks.ORES_GOLD).add(ModBlocks.SIFTSTONE_GOLD_ORE.get());
            this.tag(net.neoforged.neoforge.common.Tags.Blocks.ORES_DIAMOND).add(ModBlocks.SIFTSTONE_DIAMOND_ORE.get());
            this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(soil).add(ModBlocks.CARAPACE_SAND.get());
            this.tag(BlockTags.MINEABLE_WITH_HOE).add(soil).add(ModBlocks.WHITE_WILLOW_LEAVES.get());
            this.tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.RESONANT_DEEPSLATE.get());
            this.tag(BlockTags.DIRT).add(soil);
            this.tag(BlockTags.SAND).add(ModBlocks.CARAPACE_SAND.get());
            this.tag(BlockTags.DEAD_BUSH_MAY_PLACE_ON).add(ModBlocks.CARAPACE_SAND.get());
            this.tag(BlockTags.BASE_STONE_OVERWORLD).add(ModBlocks.SIFTSTONE.get());
            this.tag(BlockTags.STAIRS).add(ModBlocks.COBBLED_SIFTSTONE_STAIRS.get(), ModBlocks.POLISHED_SIFTSTONE_STAIRS.get(),
                    ModBlocks.SIFTSTONE_BRICK_STAIRS.get());
            this.tag(BlockTags.SLABS).add(ModBlocks.COBBLED_SIFTSTONE_SLAB.get(), ModBlocks.POLISHED_SIFTSTONE_SLAB.get(),
                    ModBlocks.SIFTSTONE_BRICK_SLAB.get());
            this.tag(BlockTags.WALLS).add(ModBlocks.COBBLED_SIFTSTONE_WALL.get(), ModBlocks.POLISHED_SIFTSTONE_WALL.get(),
                    ModBlocks.SIFTSTONE_BRICK_WALL.get());

            // white willow
            this.tag(ModTags.Blocks.WHITE_WILLOW_LOGS).add(ModBlocks.WHITE_WILLOW_LOG.get(), ModBlocks.WHITE_WILLOW_WOOD.get(),
                    ModBlocks.STRIPPED_WHITE_WILLOW_LOG.get(), ModBlocks.STRIPPED_WHITE_WILLOW_WOOD.get());
            this.tag(BlockTags.LOGS_THAT_BURN).addTag(ModTags.Blocks.WHITE_WILLOW_LOGS);
            this.tag(BlockTags.PLANKS).add(ModBlocks.WHITE_WILLOW_PLANKS.get());
            this.tag(BlockTags.WOODEN_STAIRS).add(ModBlocks.WHITE_WILLOW_STAIRS.get());
            this.tag(BlockTags.WOODEN_SLABS).add(ModBlocks.WHITE_WILLOW_SLAB.get());
            this.tag(BlockTags.WOODEN_FENCES).add(ModBlocks.WHITE_WILLOW_FENCE.get());
            this.tag(BlockTags.FENCE_GATES).add(ModBlocks.WHITE_WILLOW_FENCE_GATE.get());
            this.tag(BlockTags.WOODEN_DOORS).add(ModBlocks.WHITE_WILLOW_DOOR.get());
            this.tag(BlockTags.WOODEN_TRAPDOORS).add(ModBlocks.WHITE_WILLOW_TRAPDOOR.get());
            this.tag(BlockTags.WOODEN_BUTTONS).add(ModBlocks.WHITE_WILLOW_BUTTON.get());
            this.tag(BlockTags.WOODEN_PRESSURE_PLATES).add(ModBlocks.WHITE_WILLOW_PRESSURE_PLATE.get());
            this.tag(BlockTags.LEAVES).add(ModBlocks.WHITE_WILLOW_LEAVES.get());
            this.tag(BlockTags.SAPLINGS).add(ModBlocks.WHITE_WILLOW_SAPLING.get());
            this.tag(BlockTags.REPLACEABLE_BY_TREES).add(ModBlocks.WEEPING_WHITE_WILLOW.get(), ModBlocks.ORANGE_SHORT_SCULK_GRASS.get(),
                    ModBlocks.LIGHT_ORANGE_SHORT_SCULK_GRASS.get(), ModBlocks.GREEN_SHORT_SCULK_GRASS.get(),
                    ModBlocks.TALL_SCULK_GRASS.get(), ModBlocks.GREEN_TALL_SCULK_GRASS.get());
            this.tag(BlockTags.MINEABLE_WITH_AXE).addTag(ModTags.Blocks.WHITE_WILLOW_LOGS).add(ModBlocks.WHITE_WILLOW_PLANKS.get(),
                    ModBlocks.WHITE_WILLOW_STAIRS.get(), ModBlocks.WHITE_WILLOW_SLAB.get(), ModBlocks.WHITE_WILLOW_FENCE.get(),
                    ModBlocks.WHITE_WILLOW_FENCE_GATE.get(), ModBlocks.WHITE_WILLOW_DOOR.get(), ModBlocks.WHITE_WILLOW_TRAPDOOR.get(),
                    ModBlocks.WHITE_WILLOW_BUTTON.get(), ModBlocks.WHITE_WILLOW_PRESSURE_PLATE.get());

            this.tag(BlockTags.DRAGON_IMMUNE).add(ModBlocks.SIFT_PORTAL.get(), ModBlocks.SPECTRAL_BARRIER.get());
            this.tag(BlockTags.WITHER_IMMUNE).add(ModBlocks.SIFT_PORTAL.get(), ModBlocks.SPECTRAL_BARRIER.get());

            // mod tags
            this.tag(ModTags.Blocks.SIFT_PORTAL_FRAMES).add(net.minecraft.world.level.block.Blocks.REINFORCED_DEEPSLATE,
                    ModBlocks.RESONANT_DEEPSLATE.get());
            this.tag(ModTags.Blocks.SIFT_SOIL).add(soil);
            this.tag(ModTags.Blocks.SIFT_SPAWNABLE_ON).add(soil).add(ModBlocks.SIFTSTONE.get(), ModBlocks.COBBLED_SIFTSTONE.get(),
                    ModBlocks.CARAPACE_SAND.get(), ModBlocks.CARAPACE_SANDSTONE.get(), net.minecraft.world.level.block.Blocks.BONE_BLOCK);
            this.tag(ModTags.Blocks.SIFT_CARVER_REPLACEABLES).addTag(BlockTags.OVERWORLD_CARVER_REPLACEABLES).add(soil)
                    .add(ModBlocks.SIFTSTONE.get(), ModBlocks.COBBLED_SIFTSTONE.get(), ModBlocks.CARAPACE_SAND.get(),
                            ModBlocks.CARAPACE_SANDSTONE.get());
            this.tag(BlockTags.ANIMALS_SPAWNABLE_ON).add(grassBlocks);
        }
    }

    public static class Items extends ItemTagsProvider {
        public Items(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
                     CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, ExistingFileHelper files) {
            super(output, lookup, blockTags, Dungeons2.MOD_ID, files);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.copy(ModTags.Blocks.WHITE_WILLOW_LOGS, ModTags.Items.WHITE_WILLOW_LOGS);
            this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
            this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
            this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
            this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
            this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
            this.copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
            this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
            this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
            this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
            this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
            this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
            this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
            this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
            this.copy(BlockTags.SLABS, ItemTags.SLABS);
            this.copy(BlockTags.WALLS, ItemTags.WALLS);
            this.copy(BlockTags.DIRT, ItemTags.DIRT);
            this.copy(BlockTags.COAL_ORES, ItemTags.COAL_ORES);
            this.copy(BlockTags.IRON_ORES, ItemTags.IRON_ORES);
            this.copy(BlockTags.COPPER_ORES, ItemTags.COPPER_ORES);
            this.copy(BlockTags.GOLD_ORES, ItemTags.GOLD_ORES);
            this.copy(BlockTags.DIAMOND_ORES, ItemTags.DIAMOND_ORES);
            this.copy(BlockTags.SAND, ItemTags.SAND);
            this.tag(ItemTags.STONE_CRAFTING_MATERIALS).add(ModBlocks.COBBLED_SIFTSTONE.get().asItem());
            this.tag(ItemTags.STONE_TOOL_MATERIALS).add(ModBlocks.COBBLED_SIFTSTONE.get().asItem());

            this.tag(ModTags.Items.BLUB_FOOD).add(net.minecraft.world.item.Items.GLOW_BERRIES, net.minecraft.world.item.Items.SWEET_BERRIES,
                    ModBlocks.GREEN_SHORT_SCULK_GRASS.get().asItem(), ModBlocks.ORANGE_SHORT_SCULK_GRASS.get().asItem());
            this.tag(ItemTags.SWORDS).add(ModItems.BATTLESTAFF.get(), ModItems.RIFTSLASHER.get(), ModItems.SPECTRAL_SPEAR.get(),
                    ModItems.SCULKERS_BANE.get(), ModItems.SCULKER_CLAWS.get(), ModItems.CACOPHONOUS_CLEAVER.get(), ModItems.SOUL_REAPER.get());
            this.tag(ItemTags.HEAD_ARMOR).add(ModItems.SIFTER_HELMET.get(), ModItems.MAD_SIFTER_HELMET.get());
            this.tag(ItemTags.CHEST_ARMOR).add(ModItems.SIFTER_CHESTPLATE.get(), ModItems.MAD_SIFTER_CHESTPLATE.get());
            this.tag(ItemTags.LEG_ARMOR).add(ModItems.SIFTER_LEGGINGS.get(), ModItems.MAD_SIFTER_LEGGINGS.get());
            this.tag(ItemTags.FOOT_ARMOR).add(ModItems.SIFTER_BOOTS.get(), ModItems.MAD_SIFTER_BOOTS.get());
        }
    }

    public static class EntityTypes extends EntityTypeTagsProvider {
        public EntityTypes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper files) {
            super(output, lookup, Dungeons2.MOD_ID, files);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.tag(ModTags.Entities.SCULKERS).add(ModEntities.HUNTER.get(), ModEntities.SCAVENGER.get(), ModEntities.STALKER.get(),
                    ModEntities.TRAPPER.get(), ModEntities.MONARCH.get(), EntityType.WARDEN);
            this.tag(ModTags.Entities.SIFT_NATIVES).add(ModEntities.BLUB.get(), ModEntities.SINGER.get(), ModEntities.ECHO_GOLEM.get(),
                    ModEntities.SEEDLING.get(), ModEntities.SENTINEL.get(), ModEntities.POLLINATOR.get(), ModEntities.NESTER.get(),
                    ModEntities.SPROUT.get(), ModEntities.HARMONIZER.get(), ModEntities.DARTBACK.get(), ModEntities.HUNTER.get(),
                    ModEntities.SCAVENGER.get(), ModEntities.STALKER.get(), ModEntities.TRAPPER.get(), ModEntities.MONARCH.get());
            this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(ModEntities.SPROUT.get(), ModEntities.HARMONIZER.get(), ModEntities.MONARCH.get());
            this.tag(EntityTypeTags.IMPACT_PROJECTILES).add(ModEntities.SIFT_DART.get(), ModEntities.GOO_GLOB.get(),
                    ModEntities.SOUL_WISP.get(), ModEntities.MONARCH_FEATHER.get());
        }
    }

    public static class Biomes extends BiomeTagsProvider {
        public Biomes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper files) {
            super(output, lookup, Dungeons2.MOD_ID, files);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.tag(ModTags.Biomes.IS_SIFT).add(ModBiomes.SINGERS_MEADOW, ModBiomes.LULLABY_HILLS, ModBiomes.THE_CARAPACE,
                    ModBiomes.ICHOR_RAVINES);
        }
    }

    public static class DamageTypes extends DamageTypeTagsProvider {
        public DamageTypes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper files) {
            super(output, lookup, Dungeons2.MOD_ID, files);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.tag(DamageTypeTags.BYPASSES_ARMOR).add(ModDamageTypes.SOUL_BURN);
            this.tag(DamageTypeTags.IS_FIRE).add(ModDamageTypes.SOUL_BURN);
            this.tag(DamageTypeTags.NO_KNOCKBACK).add(ModDamageTypes.SOUL_BURN);
        }
    }
}
