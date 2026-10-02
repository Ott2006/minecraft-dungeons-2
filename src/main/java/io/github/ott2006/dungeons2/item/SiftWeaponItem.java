package io.github.ott2006.dungeons2.item;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.registry.ModItems;
import io.github.ott2006.dungeons2.registry.ModTags;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

/**
 * The melee weapons of the Sift. Each kind has a unique trait, mirroring its Minecraft Dungeons II counterpart.
 */
public class SiftWeaponItem extends SwordItem {
    public static final Tier SIFT_TIER = new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1250, 8.0F, 0.0F, 16,
            () -> Ingredient.of(ModItems.SIFTER_CHITIN.get()));

    private static final String COMBO_COUNT = "dungeons2:combo_count";
    private static final String COMBO_TIME = "dungeons2:combo_time";

    public enum Kind {
        /** Large two-ended staff: long reach and heavy knockback. */
        BATTLESTAFF(7, 1.3, 1.0, 1.0),
        /** Curved longsword: every third hit tears a small rift that hurts everything around the target. */
        RIFTSLASHER(8, 1.4, 0.0, 0.0),
        /** Forged in ichor: inflicts additional soul damage and reaches far. */
        SPECTRAL_SPEAR(7, 1.1, 1.5, 0.0),
        /** Soul attacks against sculk creatures deal much more damage. */
        SCULKERS_BANE(8, 1.6, 0.0, 0.0),
        /** Fast dual claws: the third hit of a combo strikes hard. */
        SCULKER_CLAWS(5, 2.4, 0.0, 0.0),
        /** Restores health for every soul it gathers (every kill). */
        CACOPHONOUS_CLEAVER(9, 0.9, 0.0, 0.5),
        /** A scythe that gathers more souls: kills grant 60% more experience. */
        SOUL_REAPER(7, 1.0, 0.5, 0.0);

        final double damage;
        final double speed;
        final double reach;
        final double knockback;

        Kind(double damage, double speed, double reach, double knockback) {
            this.damage = damage;
            this.speed = speed;
            this.reach = reach;
            this.knockback = knockback;
        }
    }

    private final Kind kind;

    public SiftWeaponItem(Kind kind, Rarity rarity) {
        super(SIFT_TIER, new Item.Properties().rarity(rarity).attributes(createAttributes(kind)));
        this.kind = kind;
    }

    public Kind getKind() {
        return this.kind;
    }

    private static ItemAttributeModifiers createAttributes(Kind kind) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, kind.damage - 1.0,
                        AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, kind.speed - 4.0,
                        AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        if (kind.reach > 0) {
            builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(Dungeons2.id("weapon_reach"), kind.reach,
                    AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }
        if (kind.knockback > 0) {
            builder.add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(Dungeons2.id("weapon_knockback"), kind.knockback,
                    AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }
        return builder.build();
    }

    @Override
    public float getAttackDamageBonus(Entity target, float damage, DamageSource source) {
        Entity attacker = source.getEntity();
        return switch (this.kind) {
            case SCULKERS_BANE -> target.getType().is(ModTags.Entities.SCULKERS) ? damage * 0.5F : 0.0F;
            case SPECTRAL_SPEAR -> 3.0F;
            case SCULKER_CLAWS -> attacker != null && comboCount(attacker) == 2 ? 6.0F : 0.0F;
            default -> 0.0F;
        };
    }

    private static int comboCount(Entity attacker) {
        CompoundTag data = attacker.getPersistentData();
        if (attacker.level().getGameTime() - data.getLong(COMBO_TIME) > 40) {
            return 0;
        }
        return data.getInt(COMBO_COUNT);
    }

    /** Increments and returns the combo counter (1, 2, 3, ...). */
    private static int bumpCombo(Entity attacker) {
        int count = comboCount(attacker) + 1;
        CompoundTag data = attacker.getPersistentData();
        data.putInt(COMBO_COUNT, count);
        data.putLong(COMBO_TIME, attacker.level().getGameTime());
        return count;
    }

    private static void resetCombo(Entity attacker) {
        attacker.getPersistentData().putInt(COMBO_COUNT, 0);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.level() instanceof ServerLevel level) {
            switch (this.kind) {
                case RIFTSLASHER -> {
                    if (bumpCombo(attacker) >= 3) {
                        resetCombo(attacker);
                        riftBurst(level, target, attacker);
                    }
                }
                case SCULKER_CLAWS -> {
                    if (bumpCombo(attacker) >= 3) {
                        resetCombo(attacker);
                        level.sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), target.getY(0.5), target.getZ(), 2, 0.3, 0.2, 0.3, 0.0);
                        level.playSound(null, target.blockPosition(), SoundEvents.WARDEN_ATTACK_IMPACT, SoundSource.PLAYERS, 0.8F, 1.3F);
                    }
                }
                case SPECTRAL_SPEAR -> level.sendParticles(ParticleTypes.SCULK_SOUL, target.getX(), target.getY(0.6), target.getZ(),
                        6, 0.25, 0.3, 0.25, 0.02);
                case SCULKERS_BANE -> {
                    if (target.getType().is(ModTags.Entities.SCULKERS)) {
                        level.sendParticles(ParticleTypes.SCULK_SOUL, target.getX(), target.getY(0.6), target.getZ(), 6, 0.3, 0.3, 0.3, 0.02);
                    }
                }
                default -> {
                }
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    private static void riftBurst(ServerLevel level, LivingEntity target, LivingEntity attacker) {
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, target.getX(), target.getY(0.5), target.getZ(), 40, 0.6, 0.6, 0.6, 0.2);
        level.sendParticles(ParticleTypes.SONIC_BOOM, target.getX(), target.getY(0.5), target.getZ(), 1, 0, 0, 0, 0);
        level.playSound(null, target.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8F, 0.6F);
        DamageSource source = attacker instanceof net.minecraft.world.entity.player.Player player
                ? level.damageSources().playerAttack(player) : level.damageSources().mobAttack(attacker);
        for (LivingEntity other : level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(2.5))) {
            if (other != attacker && CombatUtil.isEnemyOf(attacker, other)) {
                other.hurt(source, 4.0F);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
    }
}
