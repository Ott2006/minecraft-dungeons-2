package io.github.ott2006.dungeons2.datagen;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.registry.ModBlocks;
import io.github.ott2006.dungeons2.registry.ModItems;
import io.github.ott2006.dungeons2.registry.ModTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    private static String path(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    private static void cut(RecipeOutput out, ItemLike result, ItemLike material, int count) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(material), RecipeCategory.BUILDING_BLOCKS, result, count)
                .unlockedBy(getHasName(material), has(material))
                .save(out, Dungeons2.id(path(result) + "_from_" + path(material) + "_stonecutting"));
    }

    private static void stoneSet(RecipeOutput out, ItemLike base, ItemLike stairs, ItemLike slab, ItemLike wall) {
        stairBuilder(stairs, Ingredient.of(base)).unlockedBy(getHasName(base), has(base)).save(out);
        slab(out, RecipeCategory.BUILDING_BLOCKS, slab, base);
        wall(out, RecipeCategory.BUILDING_BLOCKS, wall, base);
        cut(out, stairs, base, 1);
        cut(out, slab, base, 2);
        cut(out, wall, base, 1);
    }

    @Override
    protected void buildRecipes(RecipeOutput out) {
        // ---------------------------------------------------------------- siftstone
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModBlocks.COBBLED_SIFTSTONE.get()), RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.SIFTSTONE.get(), 0.1F, 200)
                .unlockedBy(getHasName(ModBlocks.COBBLED_SIFTSTONE.get()), has(ModBlocks.COBBLED_SIFTSTONE.get()))
                .save(out, Dungeons2.id("siftstone_from_smelting"));
        polished(out, RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_SIFTSTONE.get(), ModBlocks.SIFTSTONE.get());
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SIFTSTONE_BRICKS.get(), 4)
                .define('#', ModBlocks.POLISHED_SIFTSTONE.get()).pattern("##").pattern("##")
                .unlockedBy(getHasName(ModBlocks.POLISHED_SIFTSTONE.get()), has(ModBlocks.POLISHED_SIFTSTONE.get())).save(out);
        chiseled(out, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_SIFTSTONE.get(), ModBlocks.SIFTSTONE_BRICK_SLAB.get());
        cut(out, ModBlocks.POLISHED_SIFTSTONE.get(), ModBlocks.SIFTSTONE.get(), 1);
        cut(out, ModBlocks.SIFTSTONE_BRICKS.get(), ModBlocks.SIFTSTONE.get(), 1);
        cut(out, ModBlocks.SIFTSTONE_BRICKS.get(), ModBlocks.POLISHED_SIFTSTONE.get(), 1);
        cut(out, ModBlocks.CHISELED_SIFTSTONE.get(), ModBlocks.SIFTSTONE.get(), 1);
        stoneSet(out, ModBlocks.COBBLED_SIFTSTONE.get(), ModBlocks.COBBLED_SIFTSTONE_STAIRS.get(), ModBlocks.COBBLED_SIFTSTONE_SLAB.get(),
                ModBlocks.COBBLED_SIFTSTONE_WALL.get());
        stoneSet(out, ModBlocks.POLISHED_SIFTSTONE.get(), ModBlocks.POLISHED_SIFTSTONE_STAIRS.get(), ModBlocks.POLISHED_SIFTSTONE_SLAB.get(),
                ModBlocks.POLISHED_SIFTSTONE_WALL.get());
        stoneSet(out, ModBlocks.SIFTSTONE_BRICKS.get(), ModBlocks.SIFTSTONE_BRICK_STAIRS.get(), ModBlocks.SIFTSTONE_BRICK_SLAB.get(),
                ModBlocks.SIFTSTONE_BRICK_WALL.get());
        twoByTwoPacker(out, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CARAPACE_SANDSTONE.get(), ModBlocks.CARAPACE_SAND.get());
        smeltOre(out, ModBlocks.SIFTSTONE_COAL_ORE.get(), Items.COAL, 0.1F);
        smeltOre(out, ModBlocks.SIFTSTONE_IRON_ORE.get(), Items.IRON_INGOT, 0.7F);
        smeltOre(out, ModBlocks.SIFTSTONE_COPPER_ORE.get(), Items.COPPER_INGOT, 0.7F);
        smeltOre(out, ModBlocks.SIFTSTONE_GOLD_ORE.get(), Items.GOLD_INGOT, 1.0F);
        smeltOre(out, ModBlocks.SIFTSTONE_DIAMOND_ORE.get(), Items.DIAMOND, 1.0F);

        // ---------------------------------------------------------------- white willow
        planksFromLogs(out, ModBlocks.WHITE_WILLOW_PLANKS.get(), ModTags.Items.WHITE_WILLOW_LOGS, 4);
        woodFromLogs(out, ModBlocks.WHITE_WILLOW_WOOD.get(), ModBlocks.WHITE_WILLOW_LOG.get());
        woodFromLogs(out, ModBlocks.STRIPPED_WHITE_WILLOW_WOOD.get(), ModBlocks.STRIPPED_WHITE_WILLOW_LOG.get());
        Ingredient planks = Ingredient.of(ModBlocks.WHITE_WILLOW_PLANKS.get());
        String hasPlanks = getHasName(ModBlocks.WHITE_WILLOW_PLANKS.get());
        stairBuilder(ModBlocks.WHITE_WILLOW_STAIRS.get(), planks).unlockedBy(hasPlanks, has(ModBlocks.WHITE_WILLOW_PLANKS.get())).save(out);
        slab(out, RecipeCategory.BUILDING_BLOCKS, ModBlocks.WHITE_WILLOW_SLAB.get(), ModBlocks.WHITE_WILLOW_PLANKS.get());
        fenceBuilder(ModBlocks.WHITE_WILLOW_FENCE.get(), planks).unlockedBy(hasPlanks, has(ModBlocks.WHITE_WILLOW_PLANKS.get())).save(out);
        fenceGateBuilder(ModBlocks.WHITE_WILLOW_FENCE_GATE.get(), planks).unlockedBy(hasPlanks, has(ModBlocks.WHITE_WILLOW_PLANKS.get())).save(out);
        doorBuilder(ModBlocks.WHITE_WILLOW_DOOR.get(), planks).unlockedBy(hasPlanks, has(ModBlocks.WHITE_WILLOW_PLANKS.get())).save(out);
        trapdoorBuilder(ModBlocks.WHITE_WILLOW_TRAPDOOR.get(), planks).unlockedBy(hasPlanks, has(ModBlocks.WHITE_WILLOW_PLANKS.get())).save(out);
        buttonBuilder(ModBlocks.WHITE_WILLOW_BUTTON.get(), planks).unlockedBy(hasPlanks, has(ModBlocks.WHITE_WILLOW_PLANKS.get())).save(out);
        pressurePlate(out, ModBlocks.WHITE_WILLOW_PRESSURE_PLATE.get(), ModBlocks.WHITE_WILLOW_PLANKS.get());

        // ---------------------------------------------------------------- soul & echo
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SOUL_BLOCK.get())
                .define('#', ModItems.SOUL_FRAGMENT.get()).pattern("###").pattern("###").pattern("###")
                .unlockedBy(getHasName(ModItems.SOUL_FRAGMENT.get()), has(ModItems.SOUL_FRAGMENT.get())).save(out);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOUL_FRAGMENT.get(), 9)
                .requires(ModBlocks.SOUL_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.SOUL_BLOCK.get()), has(ModBlocks.SOUL_BLOCK.get()))
                .save(out, Dungeons2.id("soul_fragment_from_soul_block"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.RESONANT_DEEPSLATE.get(), 8)
                .define('#', Items.DEEPSLATE_BRICKS).define('E', Items.ECHO_SHARD)
                .pattern("###").pattern("#E#").pattern("###")
                .unlockedBy(getHasName(Items.ECHO_SHARD), has(Items.ECHO_SHARD)).save(out);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SINGERS_HORN.get())
                .define('E', Items.ECHO_SHARD).define('H', Items.GOAT_HORN)
                .pattern(" E ").pattern("EHE").pattern(" E ")
                .unlockedBy(getHasName(Items.ECHO_SHARD), has(Items.ECHO_SHARD)).save(out);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MONARCH_LURE.get())
                .define('C', ModItems.SCULKER_CLAW.get()).define('S', Items.SCULK_CATALYST).define('B', Items.BONE_BLOCK)
                .pattern(" C ").pattern("CSC").pattern(" B ")
                .unlockedBy(getHasName(ModItems.SCULKER_CLAW.get()), has(ModItems.SCULKER_CLAW.get())).save(out);

        // ---------------------------------------------------------------- weapons
        Item chitin = ModItems.SIFTER_CHITIN.get();
        Item claw = ModItems.SCULKER_CLAW.get();
        Item soul = ModItems.SOUL_FRAGMENT.get();
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BATTLESTAFF.get())
                .define('C', chitin).define('L', ModBlocks.WHITE_WILLOW_LOG.get())
                .pattern("  C").pattern(" L ").pattern("C  ")
                .unlockedBy(getHasName(chitin), has(chitin)).save(out);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.RIFTSLASHER.get())
                .define('C', chitin).define('P', Items.ENDER_PEARL).define('S', Items.STICK)
                .pattern(" C").pattern("PC").pattern("S ")
                .unlockedBy(getHasName(chitin), has(chitin)).save(out);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SCULKERS_BANE.get())
                .define('K', claw).define('D', Items.DIAMOND_SWORD)
                .pattern(" K ").pattern("KDK").pattern(" K ")
                .unlockedBy(getHasName(claw), has(claw)).save(out);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SCULKER_CLAWS.get())
                .define('K', claw).define('F', ModItems.MONARCH_FEATHER.get())
                .pattern("K K").pattern("KFK")
                .unlockedBy(getHasName(ModItems.MONARCH_FEATHER.get()), has(ModItems.MONARCH_FEATHER.get())).save(out);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.CACOPHONOUS_CLEAVER.get())
                .define('I', Items.IRON_INGOT).define('N', Items.NOTE_BLOCK).define('F', soul).define('S', Items.STICK)
                .pattern("IN").pattern("IF").pattern(" S")
                .unlockedBy(getHasName(soul), has(soul)).save(out);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SOUL_REAPER.get())
                .define('F', soul).define('S', Items.STICK).define('I', Items.IRON_INGOT)
                .pattern("FFI").pattern("  S").pattern(" S ")
                .unlockedBy(getHasName(soul), has(soul)).save(out);

        // ---------------------------------------------------------------- artifacts
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.ECHO_OCARINA.get())
                .define('E', Items.ECHO_SHARD).define('C', Items.CLAY_BALL).define('N', Items.NOTE_BLOCK)
                .pattern(" E ").pattern("CNC")
                .unlockedBy(getHasName(Items.ECHO_SHARD), has(Items.ECHO_SHARD)).save(out);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WARDING_CHIMES.get())
                .define('S', Items.STICK).define('A', Items.AMETHYST_SHARD).define('C', chitin)
                .pattern(" S ").pattern("ACA").pattern("A A")
                .unlockedBy(getHasName(chitin), has(chitin)).save(out);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.HUMBLING_HORN.get())
                .requires(Items.GOAT_HORN).requires(chitin).requires(chitin).requires(Items.IRON_INGOT)
                .unlockedBy(getHasName(chitin), has(chitin)).save(out);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.CORRUPTED_SEEDS.get())
                .requires(Items.WHEAT_SEEDS).requires(soul).requires(chitin).requires(Items.FERMENTED_SPIDER_EYE)
                .unlockedBy(getHasName(soul), has(soul)).save(out);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SOUL_HARVESTER.get())
                .define('F', soul).define('C', Items.SCULK_CATALYST)
                .pattern(" F ").pattern("FCF").pattern(" F ")
                .unlockedBy(getHasName(soul), has(soul)).save(out);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.CORRUPTED_BEACON.get())
                .define('F', soul).define('G', Items.GLASS).define('S', ModBlocks.SOUL_BLOCK.get()).define('O', Items.OBSIDIAN)
                .pattern("FGF").pattern("FSF").pattern("OOO")
                .unlockedBy(getHasName(ModBlocks.SOUL_BLOCK.get()), has(ModBlocks.SOUL_BLOCK.get())).save(out);

        // ---------------------------------------------------------------- armor
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SIFTER_HELMET.get())
                .define('C', chitin).pattern("CCC").pattern("C C").unlockedBy(getHasName(chitin), has(chitin)).save(out);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SIFTER_CHESTPLATE.get())
                .define('C', chitin).pattern("C C").pattern("CCC").pattern("CCC").unlockedBy(getHasName(chitin), has(chitin)).save(out);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SIFTER_LEGGINGS.get())
                .define('C', chitin).pattern("CCC").pattern("C C").pattern("C C").unlockedBy(getHasName(chitin), has(chitin)).save(out);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SIFTER_BOOTS.get())
                .define('C', chitin).pattern("C C").pattern("C C").unlockedBy(getHasName(chitin), has(chitin)).save(out);
        mad(out, ModItems.MAD_SIFTER_HELMET.get(), ModItems.SIFTER_HELMET.get());
        mad(out, ModItems.MAD_SIFTER_CHESTPLATE.get(), ModItems.SIFTER_CHESTPLATE.get());
        mad(out, ModItems.MAD_SIFTER_LEGGINGS.get(), ModItems.SIFTER_LEGGINGS.get());
        mad(out, ModItems.MAD_SIFTER_BOOTS.get(), ModItems.SIFTER_BOOTS.get());
    }

    private static void smeltOre(RecipeOutput out, ItemLike ore, ItemLike result, float xp) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ore), RecipeCategory.MISC, result, xp, 200)
                .unlockedBy(getHasName(ore), has(ore)).save(out, Dungeons2.id(path(result) + "_from_smelting_" + path(ore)));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ore), RecipeCategory.MISC, result, xp, 100)
                .unlockedBy(getHasName(ore), has(ore)).save(out, Dungeons2.id(path(result) + "_from_blasting_" + path(ore)));
    }

    private static void mad(RecipeOutput out, ItemLike result, ItemLike base) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, result)
                .requires(base).requires(ModItems.ENRAGED_HEART.get()).requires(ModItems.ICHOR_BUCKET.get())
                .unlockedBy(getHasName(ModItems.ENRAGED_HEART.get()), has(ModItems.ENRAGED_HEART.get())).save(out);
    }
}
