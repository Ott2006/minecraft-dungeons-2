package io.github.ott2006.dungeons2.datagen;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.item.SiftWeaponItem;
import io.github.ott2006.dungeons2.registry.ModBlocks;
import io.github.ott2006.dungeons2.registry.ModItems;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper files) {
        super(output, Dungeons2.MOD_ID, files);
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    private static String name(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    @Override
    protected void registerModels() {
        Set<Block> flat = Set.of(
                ModBlocks.ORANGE_SHORT_SCULK_GRASS.get(), ModBlocks.LIGHT_ORANGE_SHORT_SCULK_GRASS.get(),
                ModBlocks.GREEN_SHORT_SCULK_GRASS.get(), ModBlocks.WHITE_WILLOW_SAPLING.get(), ModBlocks.WEEPING_WHITE_WILLOW.get());
        for (DeferredBlock<?> holder : ModBlocks.BLOCKS_WITH_ITEMS) {
            Block block = holder.get();
            String name = name(block);
            if (flat.contains(block)) {
                this.flatBlock(name, this.modLoc("block/" + name));
            } else if (block == ModBlocks.TALL_SCULK_GRASS.get() || block == ModBlocks.GREEN_TALL_SCULK_GRASS.get()) {
                this.flatBlock(name, this.modLoc("block/" + name + "_top"));
            } else if (block == ModBlocks.WHITE_WILLOW_DOOR.get()) {
                this.basicItem(block.asItem());
            } else if (block == ModBlocks.WHITE_WILLOW_TRAPDOOR.get()) {
                this.withExistingParent(name, this.modLoc("block/" + name + "_bottom"));
            } else if (block == ModBlocks.WHITE_WILLOW_FENCE.get()) {
                this.fenceInventory(name, this.modLoc("block/white_willow_planks"));
            } else if (block == ModBlocks.WHITE_WILLOW_BUTTON.get()) {
                this.buttonInventory(name, this.modLoc("block/white_willow_planks"));
            } else if (block == ModBlocks.COBBLED_SIFTSTONE_WALL.get()) {
                this.wallInventory(name, this.modLoc("block/cobbled_siftstone"));
            } else if (block == ModBlocks.POLISHED_SIFTSTONE_WALL.get()) {
                this.wallInventory(name, this.modLoc("block/polished_siftstone"));
            } else if (block == ModBlocks.SIFTSTONE_BRICK_WALL.get()) {
                this.wallInventory(name, this.modLoc("block/siftstone_bricks"));
            } else {
                this.withExistingParent(name, this.modLoc("block/" + name));
            }
        }

        for (DeferredItem<? extends Item> holder : ModItems.TAB_ITEMS) {
            Item item = holder.get();
            if (item instanceof SiftWeaponItem) {
                this.handheld(item);
            } else {
                this.basicItem(item);
            }
        }
        this.basicItem(ModItems.SIFT_DART.get());
        this.basicItem(ModItems.GOO_GLOB.get());
        this.basicItem(ModItems.SOUL_WISP.get());
        for (DeferredItem<?> egg : ModItems.SPAWN_EGGS) {
            this.withExistingParent(name(egg.get()), this.mcLoc("item/template_spawn_egg"));
        }
    }

    private void flatBlock(String name, ResourceLocation texture) {
        this.withExistingParent(name, this.mcLoc("item/generated")).texture("layer0", texture);
    }

    private void handheld(Item item) {
        String name = name(item);
        this.withExistingParent(name, this.mcLoc("item/handheld")).texture("layer0", this.modLoc("item/" + name));
    }
}
