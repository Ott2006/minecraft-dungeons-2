package io.github.ott2006.dungeons2.registry;

import io.github.ott2006.dungeons2.Dungeons2;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Dungeons2.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Dungeons2.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> ICHOR_TYPE = FLUID_TYPES.register("ichor", () -> new FluidType(
            FluidType.Properties.create()
                    .descriptionId("block.dungeons2.ichor")
                    .canSwim(true)
                    .canDrown(true)
                    .canExtinguish(true)
                    .canHydrate(false)
                    .canConvertToSource(false)
                    .supportsBoating(false)
                    .lightLevel(7)
                    .density(1800)
                    .viscosity(2500)
                    .temperature(400)
                    .motionScale(0.0045)
                    .fallDistanceModifier(0.0F)
                    .pathType(PathType.LAVA)
                    .adjacentPathType(null)
                    .rarity(Rarity.UNCOMMON)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> ICHOR = FLUIDS.register("ichor",
            () -> new BaseFlowingFluid.Source(ModFluids.ichorProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_ICHOR = FLUIDS.register("flowing_ichor",
            () -> new BaseFlowingFluid.Flowing(ModFluids.ichorProperties()));

    private static BaseFlowingFluid.Properties ichorProperties;

    private ModFluids() {
    }

    private static BaseFlowingFluid.Properties ichorProperties() {
        if (ichorProperties == null) {
            ichorProperties = new BaseFlowingFluid.Properties(ICHOR_TYPE, ICHOR, FLOWING_ICHOR)
                    .bucket(ModItems.ICHOR_BUCKET)
                    .block(ModBlocks.ICHOR)
                    .slopeFindDistance(3)
                    .levelDecreasePerBlock(2)
                    .tickRate(20)
                    .explosionResistance(100.0F);
        }
        return ichorProperties;
    }
}
