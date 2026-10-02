package io.github.ott2006.dungeons2.item;

import io.github.ott2006.dungeons2.registry.ModArmorMaterials;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * Sifter and Mad Sifter armor.
 * <ul>
 *     <li>Full set (either kind, may be mixed): immunity to ichor.</li>
 *     <li>Full Mad Sifter set: enraged below half health (handled in {@code GameEvents}).</li>
 * </ul>
 */
public class SiftArmorItem extends ArmorItem {
    private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public SiftArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    public static boolean hasFullSet(LivingEntity entity) {
        for (EquipmentSlot slot : SLOTS) {
            if (!(entity.getItemBySlot(slot).getItem() instanceof SiftArmorItem)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasFullMadSet(LivingEntity entity) {
        for (EquipmentSlot slot : SLOTS) {
            if (!(entity.getItemBySlot(slot).getItem() instanceof SiftArmorItem armor) || !armor.getMaterial().is(ModArmorMaterials.MAD_SIFTER.getKey())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.dungeons2.sifter_armor.desc").withStyle(ChatFormatting.GRAY));
        if (this.getMaterial().is(ModArmorMaterials.MAD_SIFTER.getKey())) {
            tooltip.add(Component.translatable("item.dungeons2.mad_sifter_armor.desc").withStyle(ChatFormatting.RED));
        }
    }
}
