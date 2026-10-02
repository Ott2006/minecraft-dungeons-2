package io.github.ott2006.dungeons2;

import com.mojang.logging.LogUtils;
import io.github.ott2006.dungeons2.event.CommonModEvents;
import io.github.ott2006.dungeons2.event.GameEvents;
import io.github.ott2006.dungeons2.registry.ModArmorMaterials;
import io.github.ott2006.dungeons2.registry.ModBlocks;
import io.github.ott2006.dungeons2.registry.ModCreativeTabs;
import io.github.ott2006.dungeons2.registry.ModEffects;
import io.github.ott2006.dungeons2.registry.ModEntities;
import io.github.ott2006.dungeons2.registry.ModFeatures;
import io.github.ott2006.dungeons2.registry.ModFluids;
import io.github.ott2006.dungeons2.registry.ModItems;
import io.github.ott2006.dungeons2.registry.ModPoiTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

/**
 * Dungeons II: The Sift.
 * <p>
 * Brings the Sift dimension and its inhabitants from Minecraft Dungeons II into Minecraft.
 */
@Mod(Dungeons2.MOD_ID)
public class Dungeons2 {
    public static final String MOD_ID = "dungeons2";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Dungeons2(IEventBus modBus) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModFluids.FLUID_TYPES.register(modBus);
        ModFluids.FLUIDS.register(modBus);
        ModArmorMaterials.ARMOR_MATERIALS.register(modBus);
        ModEffects.MOB_EFFECTS.register(modBus);
        ModEntities.ENTITY_TYPES.register(modBus);
        ModPoiTypes.POI_TYPES.register(modBus);
        ModFeatures.FEATURES.register(modBus);
        ModCreativeTabs.CREATIVE_TABS.register(modBus);

        CommonModEvents.register(modBus);
        GameEvents.register();
        io.github.ott2006.dungeons2.datagen.DataGenerators.register(modBus);
        if (FMLEnvironment.dist.isClient()) {
            io.github.ott2006.dungeons2.client.ClientSetup.init(modBus);
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
