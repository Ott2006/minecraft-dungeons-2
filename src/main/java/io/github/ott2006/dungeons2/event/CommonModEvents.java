package io.github.ott2006.dungeons2.event;

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
import io.github.ott2006.dungeons2.entity.SiftMonster;
import io.github.ott2006.dungeons2.entity.Singer;
import io.github.ott2006.dungeons2.entity.Sprout;
import io.github.ott2006.dungeons2.registry.ModBlocks;
import io.github.ott2006.dungeons2.registry.ModEntities;
import io.github.ott2006.dungeons2.registry.ModFluids;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;

/** Mod bus events shared by client and server. */
public final class CommonModEvents {
    private CommonModEvents() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(CommonModEvents::onAttributes);
        modBus.addListener(CommonModEvents::onSpawnPlacements);
        modBus.addListener(CommonModEvents::onCommonSetup);
    }

    private static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BLUB.get(), Blub.createAttributes().build());
        event.put(ModEntities.SINGER.get(), Singer.createAttributes().build());
        event.put(ModEntities.ECHO_GOLEM.get(), EchoGolem.createAttributes().build());
        event.put(ModEntities.SEEDLING.get(), Seedling.createAttributes().build());
        event.put(ModEntities.SENTINEL.get(), Sentinel.createAttributes().build());
        event.put(ModEntities.POLLINATOR.get(), Pollinator.createAttributes().build());
        event.put(ModEntities.NESTER.get(), Nester.createAttributes().build());
        event.put(ModEntities.SPROUT.get(), Sprout.createAttributes().build());
        event.put(ModEntities.HARMONIZER.get(), Harmonizer.createAttributes().build());
        event.put(ModEntities.DARTBACK.get(), Dartback.createAttributes().build());
        event.put(ModEntities.HUNTER.get(), Sculker.createAttributes(Sculker.Kind.HUNTER).build());
        event.put(ModEntities.SCAVENGER.get(), Sculker.createAttributes(Sculker.Kind.SCAVENGER).build());
        event.put(ModEntities.STALKER.get(), Sculker.createAttributes(Sculker.Kind.STALKER).build());
        event.put(ModEntities.TRAPPER.get(), Sculker.createAttributes(Sculker.Kind.TRAPPER).build());
        event.put(ModEntities.MONARCH.get(), Monarch.createAttributes().build());
    }

    private static void onSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        RegisterSpawnPlacementsEvent.Operation op = RegisterSpawnPlacementsEvent.Operation.REPLACE;
        Heightmap.Types surface = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
        event.register(ModEntities.BLUB.get(), SpawnPlacementTypes.ON_GROUND, surface, Blub::checkBlubSpawnRules, op);
        event.register(ModEntities.SINGER.get(), SpawnPlacementTypes.ON_GROUND, surface, Singer::checkSingerSpawnRules, op);
        monster(event, ModEntities.SEEDLING.get());
        monster(event, ModEntities.SENTINEL.get());
        monster(event, ModEntities.POLLINATOR.get());
        monster(event, ModEntities.NESTER.get());
        monster(event, ModEntities.SPROUT.get());
        monster(event, ModEntities.HARMONIZER.get());
        monster(event, ModEntities.DARTBACK.get());
        monster(event, ModEntities.HUNTER.get());
        monster(event, ModEntities.SCAVENGER.get());
        monster(event, ModEntities.STALKER.get());
        monster(event, ModEntities.TRAPPER.get());
        monster(event, ModEntities.MONARCH.get());
    }

    private static <T extends Monster> void monster(RegisterSpawnPlacementsEvent event, EntityType<T> type) {
        event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                SiftMonster::checkSiftMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // ichor + water: crying obsidian from a source, siftstone from flowing ichor
            FluidInteractionRegistry.addInteraction(ModFluids.ICHOR_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    NeoForgeMod.WATER_TYPE.value(),
                    state -> state.isSource() ? Blocks.CRYING_OBSIDIAN.defaultBlockState() : ModBlocks.SIFTSTONE.get().defaultBlockState()));
            // lava flowing into ichor: magma
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    ModFluids.ICHOR_TYPE.get(),
                    state -> state.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.MAGMA_BLOCK.defaultBlockState()));
        });
    }
}
