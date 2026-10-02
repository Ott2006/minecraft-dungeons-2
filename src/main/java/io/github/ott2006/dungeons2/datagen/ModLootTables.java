package io.github.ott2006.dungeons2.datagen;

import io.github.ott2006.dungeons2.registry.ModBlocks;
import io.github.ott2006.dungeons2.registry.ModEntities;
import io.github.ott2006.dungeons2.registry.ModItems;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModLootTables {
    private ModLootTables() {
    }

    public static class BlockLoot extends BlockLootSubProvider {
        public BlockLoot(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate() {
            for (var holder : ModBlocks.BLOCKS_WITH_ITEMS) {
                Block block = holder.get();
                if (block == ModBlocks.COBBLED_SIFTSTONE_SLAB.get() || block == ModBlocks.POLISHED_SIFTSTONE_SLAB.get()
                        || block == ModBlocks.SIFTSTONE_BRICK_SLAB.get() || block == ModBlocks.WHITE_WILLOW_SLAB.get()) {
                    this.add(block, this.createSlabItemTable(block));
                } else if (block == ModBlocks.WHITE_WILLOW_DOOR.get()) {
                    this.add(block, this.createDoorTable(block));
                } else if (block == ModBlocks.WHITE_WILLOW_LEAVES.get()) {
                    this.add(block, this.createLeavesDrops(block, ModBlocks.WHITE_WILLOW_SAPLING.get(), 0.05F, 0.0625F, 0.083333336F, 0.1F));
                } else if (block == ModBlocks.SIFTSTONE_COAL_ORE.get()) {
                    this.add(block, this.createOreDrop(block, Items.COAL));
                } else if (block == ModBlocks.SIFTSTONE_IRON_ORE.get()) {
                    this.add(block, this.createOreDrop(block, Items.RAW_IRON));
                } else if (block == ModBlocks.SIFTSTONE_COPPER_ORE.get()) {
                    this.add(block, this.createCopperOreDrops(block));
                } else if (block == ModBlocks.SIFTSTONE_GOLD_ORE.get()) {
                    this.add(block, this.createOreDrop(block, Items.RAW_GOLD));
                } else if (block == ModBlocks.SIFTSTONE_DIAMOND_ORE.get()) {
                    this.add(block, this.createOreDrop(block, Items.DIAMOND));
                } else if (block == ModBlocks.SIFTSTONE.get()) {
                    this.add(block, this.createSingleItemTableWithSilkTouch(block, ModBlocks.COBBLED_SIFTSTONE.get()));
                } else if (block == ModBlocks.ORANGE_SCULK_GRASS_BLOCK.get() || block == ModBlocks.LIGHT_ORANGE_SCULK_GRASS_BLOCK.get()) {
                    this.add(block, this.createSingleItemTableWithSilkTouch(block, ModBlocks.ORANGE_HEALTHY_SCULK.get()));
                } else if (block == ModBlocks.GREEN_SCULK_GRASS_BLOCK.get()) {
                    this.add(block, this.createSingleItemTableWithSilkTouch(block, ModBlocks.GREEN_HEALTHY_SCULK.get()));
                } else if (block == ModBlocks.ORANGE_SHORT_SCULK_GRASS.get() || block == ModBlocks.LIGHT_ORANGE_SHORT_SCULK_GRASS.get()
                        || block == ModBlocks.GREEN_SHORT_SCULK_GRASS.get() || block == ModBlocks.WEEPING_WHITE_WILLOW.get()) {
                    this.add(block, createShearsOnlyDrop(block));
                } else if (block == ModBlocks.TALL_SCULK_GRASS.get() || block == ModBlocks.GREEN_TALL_SCULK_GRASS.get()) {
                    this.add(block, this.createDoublePlantShearsDrop(block));
                } else {
                    this.dropSelf(block);
                }
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).map(b -> (Block) b)::iterator;
        }
    }

    public static class EntityLoot extends EntityLootSubProvider {
        public EntityLoot(HolderLookup.Provider registries) {
            super(FeatureFlags.REGISTRY.allFlags(), registries);
        }

        private LootPool.Builder drop(ItemLike item, float min, float max) {
            return LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(item)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F))));
        }

        private LootPool.Builder playerDrop(ItemLike item, float min, float max) {
            return this.drop(item, min, max).when(LootItemKilledByPlayerCondition.killedByPlayer());
        }

        private LootPool.Builder rare(ItemLike item, float chance) {
            return LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(item))
                    .when(LootItemKilledByPlayerCondition.killedByPlayer())
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, chance, chance / 2));
        }

        @Override
        public void generate() {
            this.add(ModEntities.BLUB.get(), LootTable.lootTable().withPool(this.drop(ModItems.SOUL_FRAGMENT.get(), 0, 1)));
            this.add(ModEntities.SINGER.get(), LootTable.lootTable());
            this.add(ModEntities.ECHO_GOLEM.get(), LootTable.lootTable()
                    .withPool(this.drop(ModBlocks.SOUL_BLOCK.get(), 1, 1))
                    .withPool(this.drop(Items.ECHO_SHARD, 0, 1)));

            this.add(ModEntities.SEEDLING.get(), LootTable.lootTable()
                    .withPool(this.drop(ModItems.SIFTER_CHITIN.get(), 0, 1))
                    .withPool(this.playerDrop(ModItems.SOUL_FRAGMENT.get(), 0, 1)));
            this.add(ModEntities.SENTINEL.get(), LootTable.lootTable()
                    .withPool(this.drop(ModItems.SIFTER_CHITIN.get(), 0, 2))
                    .withPool(this.playerDrop(ModItems.SOUL_FRAGMENT.get(), 0, 1))
                    .withPool(this.rare(ModItems.ENRAGED_HEART.get(), 0.02F)));
            this.add(ModEntities.POLLINATOR.get(), LootTable.lootTable()
                    .withPool(this.drop(ModItems.SIFTER_CHITIN.get(), 0, 2))
                    .withPool(this.drop(Items.SLIME_BALL, 0, 1))
                    .withPool(this.playerDrop(ModItems.SOUL_FRAGMENT.get(), 0, 1)));
            this.add(ModEntities.NESTER.get(), LootTable.lootTable()
                    .withPool(this.drop(ModItems.SIFTER_CHITIN.get(), 1, 2))
                    .withPool(this.playerDrop(ModItems.SOUL_FRAGMENT.get(), 0, 1))
                    .withPool(this.rare(ModItems.ENRAGED_HEART.get(), 0.02F)));
            this.add(ModEntities.SPROUT.get(), LootTable.lootTable()
                    .withPool(this.drop(ModItems.SIFTER_CHITIN.get(), 0, 1))
                    .withPool(this.drop(Items.GLOW_INK_SAC, 0, 1))
                    .withPool(this.playerDrop(ModItems.SOUL_FRAGMENT.get(), 1, 1)));
            this.add(ModEntities.HARMONIZER.get(), LootTable.lootTable()
                    .withPool(this.drop(ModItems.ENRAGED_HEART.get(), 1, 1))
                    .withPool(this.drop(ModItems.SIFTER_CHITIN.get(), 3, 6))
                    .withPool(this.drop(ModItems.SOUL_FRAGMENT.get(), 2, 4)));
            this.add(ModEntities.DARTBACK.get(), LootTable.lootTable()
                    .withPool(this.drop(ModBlocks.SOUL_BLOCK.get(), 2, 4))
                    .withPool(this.drop(ModItems.ENRAGED_HEART.get(), 1, 1))
                    .withPool(this.drop(ModItems.SIFTER_CHITIN.get(), 4, 8)));

            for (EntityType<?> sculker : new EntityType<?>[]{ModEntities.HUNTER.get(), ModEntities.SCAVENGER.get(),
                    ModEntities.STALKER.get(), ModEntities.TRAPPER.get()}) {
                this.add(sculker, LootTable.lootTable()
                        .withPool(this.drop(ModItems.SCULKER_CLAW.get(), 0, 2))
                        .withPool(this.playerDrop(ModItems.SOUL_FRAGMENT.get(), 0, 1)));
            }
            this.add(ModEntities.MONARCH.get(), LootTable.lootTable()
                    .withPool(this.drop(ModItems.MONARCH_FEATHER.get(), 3, 5))
                    .withPool(this.drop(ModItems.SCULKER_CLAW.get(), 2, 4))
                    .withPool(this.drop(ModBlocks.SOUL_BLOCK.get(), 1, 2)));
        }

        @Override
        protected boolean canHaveLootTable(EntityType<?> type) {
            return type == ModEntities.ECHO_GOLEM.get() || super.canHaveLootTable(type);
        }

        @Override
        protected Stream<EntityType<?>> getKnownEntityTypes() {
            return ModEntities.ENTITY_TYPES.getEntries().stream().map(DeferredHolder::get);
        }
    }
}
