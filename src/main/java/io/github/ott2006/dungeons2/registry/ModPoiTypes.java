package io.github.ott2006.dungeons2.registry;

import com.google.common.collect.ImmutableSet;
import io.github.ott2006.dungeons2.Dungeons2;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModPoiTypes {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, Dungeons2.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> SIFT_PORTAL = POI_TYPES.register("sift_portal",
            () -> new PoiType(ImmutableSet.copyOf(ModBlocks.SIFT_PORTAL.get().getStateDefinition().getPossibleStates()), 0, 1));

    private ModPoiTypes() {
    }
}
