package io.github.ott2006.dungeons2.datagen;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.registry.ModEntities;
import io.github.ott2006.dungeons2.registry.ModItems;
import io.github.ott2006.dungeons2.world.ModDimensions;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** A small advancement tree that guides players through the Sift. */
public class ModAdvancements implements AdvancementProvider.AdvancementGenerator {
    private static Advancement.Builder base(ItemLike icon, String id, AdvancementType type) {
        return Advancement.Builder.advancement().display(icon,
                Component.translatable("advancements.dungeons2." + id + ".title"),
                Component.translatable("advancements.dungeons2." + id + ".description"),
                null, type, true, type != AdvancementType.TASK, false);
    }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper files) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(ModItems.SINGERS_HORN.get(),
                        Component.translatable("advancements.dungeons2.root.title"),
                        Component.translatable("advancements.dungeons2.root.description"),
                        Dungeons2.id("textures/block/siftstone.png"), AdvancementType.TASK, true, false, false)
                .addCriterion("horn", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SINGERS_HORN.get()))
                .save(saver, Dungeons2.id("root").toString());
        AdvancementHolder enter = base(ModItems.ICHOR_BUCKET.get(), "enter_sift", AdvancementType.GOAL).parent(root)
                .addCriterion("enter", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(ModDimensions.SIFT))
                .save(saver, Dungeons2.id("enter_sift").toString());
        base(ModItems.ECHO_GOLEM_SPAWN_EGG.get(), "echo_golem", AdvancementType.TASK).parent(enter)
                .addCriterion("golem", TameAnimalTrigger.TriggerInstance.tamedAnimal(
                        EntityPredicate.Builder.entity().of(ModEntities.ECHO_GOLEM.get())))
                .save(saver, Dungeons2.id("echo_golem").toString());
        base(ModItems.SPECTRAL_SPEAR.get(), "spectral_spear", AdvancementType.TASK).parent(enter)
                .addCriterion("spear", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SPECTRAL_SPEAR.get()))
                .save(saver, Dungeons2.id("spectral_spear").toString());
        AdvancementHolder miniBoss = base(ModItems.ENRAGED_HEART.get(), "mini_boss", AdvancementType.GOAL).parent(enter)
                .addCriterion("harmonizer", KilledTrigger.TriggerInstance.playerKilledEntity(
                        EntityPredicate.Builder.entity().of(ModEntities.HARMONIZER.get())))
                .addCriterion("dartback", KilledTrigger.TriggerInstance.playerKilledEntity(
                        EntityPredicate.Builder.entity().of(ModEntities.DARTBACK.get())))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(saver, Dungeons2.id("mini_boss").toString());
        base(ModItems.MONARCH_FEATHER.get(), "monarch", AdvancementType.CHALLENGE).parent(miniBoss)
                .addCriterion("monarch", KilledTrigger.TriggerInstance.playerKilledEntity(
                        EntityPredicate.Builder.entity().of(ModEntities.MONARCH.get())))
                .save(saver, Dungeons2.id("monarch").toString());
    }
}
