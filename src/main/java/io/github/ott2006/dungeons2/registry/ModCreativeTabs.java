package io.github.ott2006.dungeons2.registry;

import io.github.ott2006.dungeons2.Dungeons2;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Dungeons2.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SIFT = CREATIVE_TABS.register("the_sift",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.dungeons2.the_sift"))
                    .icon(() -> new ItemStack(ModItems.SINGERS_HORN.get()))
                    .displayItems((params, output) -> {
                        ModBlocks.BLOCKS_WITH_ITEMS.forEach(b -> output.accept(b.get()));
                        ModItems.TAB_ITEMS.forEach(i -> output.accept(i.get()));
                        ModItems.SPAWN_EGGS.forEach(i -> output.accept(i.get()));
                    })
                    .build());

    private ModCreativeTabs() {
    }
}
