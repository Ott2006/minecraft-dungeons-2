package io.github.ott2006.dungeons2.registry;

import io.github.ott2006.dungeons2.Dungeons2;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, Dungeons2.MOD_ID);

    /** Sifter Armor: made from the chitin of sifters. Protects against ichor when worn as a full set. */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SIFTER = register("sifter",
            new int[]{2, 5, 6, 2, 6}, 15, SoundEvents.ARMOR_EQUIP_TURTLE, 0.5F, 0.0F,
            () -> Ingredient.of(ModItems.SIFTER_CHITIN.get()));

    /** Mad Sifter Armor: an enraged upgrade of the sifter armor. */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MAD_SIFTER = register("mad_sifter",
            new int[]{3, 6, 8, 3, 10}, 12, SoundEvents.ARMOR_EQUIP_NETHERITE, 2.5F, 0.05F,
            () -> Ingredient.of(ModItems.ENRAGED_HEART.get()));

    private ModArmorMaterials() {
    }

    /**
     * @param defense boots, leggings, chestplate, helmet, body
     */
    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(String name, int[] defense, int enchantability,
                                                                         Holder<SoundEvent> equipSound, float toughness,
                                                                         float knockbackResistance, Supplier<Ingredient> repair) {
        Map<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, defense[0]);
        map.put(ArmorItem.Type.LEGGINGS, defense[1]);
        map.put(ArmorItem.Type.CHESTPLATE, defense[2]);
        map.put(ArmorItem.Type.HELMET, defense[3]);
        map.put(ArmorItem.Type.BODY, defense[4]);
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(map, enchantability, equipSound, repair,
                List.of(new ArmorMaterial.Layer(Dungeons2.id(name))), toughness, knockbackResistance));
    }
}
