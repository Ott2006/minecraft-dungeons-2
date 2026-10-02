package io.github.ott2006.dungeons2.datagen;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.block.SiftPortalBlock;
import io.github.ott2006.dungeons2.registry.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper files) {
        super(output, Dungeons2.MOD_ID, files);
    }

    private ResourceLocation tex(String name) {
        return this.modLoc("block/" + name);
    }

    private void cube(Block block) {
        this.simpleBlock(block);
    }

    @Override
    protected void registerStatesAndModels() {
        // siftstone
        this.cube(ModBlocks.SIFTSTONE.get());
        this.cube(ModBlocks.COBBLED_SIFTSTONE.get());
        this.cube(ModBlocks.POLISHED_SIFTSTONE.get());
        this.cube(ModBlocks.SIFTSTONE_BRICKS.get());
        this.cube(ModBlocks.CHISELED_SIFTSTONE.get());
        this.stairsBlock(ModBlocks.COBBLED_SIFTSTONE_STAIRS.get(), this.tex("cobbled_siftstone"));
        this.slabBlock(ModBlocks.COBBLED_SIFTSTONE_SLAB.get(), this.tex("cobbled_siftstone"), this.tex("cobbled_siftstone"));
        this.wallBlock(ModBlocks.COBBLED_SIFTSTONE_WALL.get(), this.tex("cobbled_siftstone"));
        this.stairsBlock(ModBlocks.POLISHED_SIFTSTONE_STAIRS.get(), this.tex("polished_siftstone"));
        this.slabBlock(ModBlocks.POLISHED_SIFTSTONE_SLAB.get(), this.tex("polished_siftstone"), this.tex("polished_siftstone"));
        this.wallBlock(ModBlocks.POLISHED_SIFTSTONE_WALL.get(), this.tex("polished_siftstone"));
        this.stairsBlock(ModBlocks.SIFTSTONE_BRICK_STAIRS.get(), this.tex("siftstone_bricks"));
        this.slabBlock(ModBlocks.SIFTSTONE_BRICK_SLAB.get(), this.tex("siftstone_bricks"), this.tex("siftstone_bricks"));
        this.wallBlock(ModBlocks.SIFTSTONE_BRICK_WALL.get(), this.tex("siftstone_bricks"));

        this.cube(ModBlocks.SIFTSTONE_COAL_ORE.get());
        this.cube(ModBlocks.SIFTSTONE_IRON_ORE.get());
        this.cube(ModBlocks.SIFTSTONE_COPPER_ORE.get());
        this.cube(ModBlocks.SIFTSTONE_GOLD_ORE.get());
        this.cube(ModBlocks.SIFTSTONE_DIAMOND_ORE.get());

        // carapace
        this.cube(ModBlocks.CARAPACE_SAND.get());
        this.simpleBlock(ModBlocks.CARAPACE_SANDSTONE.get(), this.models().cubeBottomTop("carapace_sandstone",
                this.tex("carapace_sandstone"), this.tex("carapace_sandstone_bottom"), this.tex("carapace_sandstone_top")));

        // healthy sculk & sculk grass
        this.cube(ModBlocks.GREEN_HEALTHY_SCULK.get());
        this.cube(ModBlocks.ORANGE_HEALTHY_SCULK.get());
        this.grass(ModBlocks.ORANGE_SCULK_GRASS_BLOCK.get(), "orange", "orange_healthy_sculk");
        this.grass(ModBlocks.LIGHT_ORANGE_SCULK_GRASS_BLOCK.get(), "light_orange", "orange_healthy_sculk");
        this.grass(ModBlocks.GREEN_SCULK_GRASS_BLOCK.get(), "green", "green_healthy_sculk");
        this.cross(ModBlocks.ORANGE_SHORT_SCULK_GRASS.get(), "orange_short_sculk_grass");
        this.cross(ModBlocks.LIGHT_ORANGE_SHORT_SCULK_GRASS.get(), "light_orange_short_sculk_grass");
        this.cross(ModBlocks.GREEN_SHORT_SCULK_GRASS.get(), "green_short_sculk_grass");
        this.doublePlant(ModBlocks.TALL_SCULK_GRASS.get(), "tall_sculk_grass");
        this.doublePlant(ModBlocks.GREEN_TALL_SCULK_GRASS.get(), "green_tall_sculk_grass");

        // white willow
        this.logBlock(ModBlocks.WHITE_WILLOW_LOG.get());
        this.logBlock(ModBlocks.STRIPPED_WHITE_WILLOW_LOG.get());
        this.axisBlock(ModBlocks.WHITE_WILLOW_WOOD.get(), this.tex("white_willow_log"), this.tex("white_willow_log"));
        this.axisBlock(ModBlocks.STRIPPED_WHITE_WILLOW_WOOD.get(), this.tex("stripped_white_willow_log"), this.tex("stripped_white_willow_log"));
        ResourceLocation planks = this.tex("white_willow_planks");
        this.cube(ModBlocks.WHITE_WILLOW_PLANKS.get());
        this.stairsBlock(ModBlocks.WHITE_WILLOW_STAIRS.get(), planks);
        this.slabBlock(ModBlocks.WHITE_WILLOW_SLAB.get(), planks, planks);
        this.fenceBlock(ModBlocks.WHITE_WILLOW_FENCE.get(), planks);
        this.fenceGateBlock(ModBlocks.WHITE_WILLOW_FENCE_GATE.get(), planks);
        this.doorBlockWithRenderType(ModBlocks.WHITE_WILLOW_DOOR.get(), this.tex("white_willow_door_bottom"),
                this.tex("white_willow_door_top"), "cutout");
        this.trapdoorBlockWithRenderType(ModBlocks.WHITE_WILLOW_TRAPDOOR.get(), this.tex("white_willow_trapdoor"), true, "cutout");
        this.buttonBlock(ModBlocks.WHITE_WILLOW_BUTTON.get(), planks);
        this.pressurePlateBlock(ModBlocks.WHITE_WILLOW_PRESSURE_PLATE.get(), planks);
        this.simpleBlock(ModBlocks.WHITE_WILLOW_LEAVES.get(), this.models().cubeAll("white_willow_leaves", this.tex("white_willow_leaves"))
                .renderType("cutout_mipped"));
        this.cross(ModBlocks.WHITE_WILLOW_SAPLING.get(), "white_willow_sapling");
        this.cross(ModBlocks.WEEPING_WHITE_WILLOW.get(), "weeping_white_willow");

        // soul & echo
        this.cube(ModBlocks.SOUL_BLOCK.get());
        this.cube(ModBlocks.RESONANT_DEEPSLATE.get());
        this.simpleBlock(ModBlocks.SPECTRAL_BARRIER.get(), this.models().cubeAll("spectral_barrier", this.tex("spectral_barrier"))
                .renderType("translucent"));

        // portal
        ModelFile portalNs = this.models().withExistingParent("sift_portal_ns", this.mcLoc("block/nether_portal_ns"))
                .texture("portal", this.tex("sift_portal")).texture("particle", this.tex("sift_portal")).renderType("translucent");
        ModelFile portalEw = this.models().withExistingParent("sift_portal_ew", this.mcLoc("block/nether_portal_ew"))
                .texture("portal", this.tex("sift_portal")).texture("particle", this.tex("sift_portal")).renderType("translucent");
        this.getVariantBuilder(ModBlocks.SIFT_PORTAL.get())
                .partialState().with(SiftPortalBlock.AXIS, Direction.Axis.X).modelForState().modelFile(portalNs).addModel()
                .partialState().with(SiftPortalBlock.AXIS, Direction.Axis.Z).modelForState().modelFile(portalEw).addModel();

        // ichor: liquids only need a particle texture
        this.simpleBlock(ModBlocks.ICHOR.get(), this.models().getBuilder("ichor").texture("particle", this.tex("ichor_still")));
    }

    private void grass(Block block, String color, String soil) {
        String name = color + "_sculk_grass_block";
        this.simpleBlock(block, this.models().cubeBottomTop(name, this.tex(name + "_side"), this.tex(soil), this.tex(name + "_top")));
    }

    private void cross(Block block, String texture) {
        this.simpleBlock(block, this.models().cross(texture, this.tex(texture)).renderType("cutout"));
    }

    private void doublePlant(Block block, String name) {
        ModelFile bottom = this.models().cross(name + "_bottom", this.tex(name + "_bottom")).renderType("cutout");
        ModelFile top = this.models().cross(name + "_top", this.tex(name + "_top")).renderType("cutout");
        this.getVariantBuilder(block)
                .partialState().with(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER).addModels(new ConfiguredModel(bottom))
                .partialState().with(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER).addModels(new ConfiguredModel(top));
    }
}
