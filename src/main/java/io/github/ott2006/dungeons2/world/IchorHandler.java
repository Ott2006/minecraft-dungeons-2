package io.github.ott2006.dungeons2.world;

import io.github.ott2006.dungeons2.item.SiftArmorItem;
import io.github.ott2006.dungeons2.registry.ModEffects;
import io.github.ott2006.dungeons2.registry.ModItems;
import io.github.ott2006.dungeons2.registry.ModTags;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Everything ichor does to the things that touch it.
 * <ul>
 *     <li>Creatures from the Overworld are set alight with soul fire ({@link ModEffects#SOUL_BURN}).</li>
 *     <li>Natives of the Sift (tag {@code dungeons2:sift_natives}) and heroes wearing a full sifter armor set are immune.</li>
 *     <li>Items left floating in ichor are re-forged. The Spectral Spear was forged in ichor, after all.</li>
 * </ul>
 */
public final class IchorHandler {
    public static final String FORGE_TICKS_TAG = "dungeons2:ichor_forging";
    public static final int FORGE_TIME = 100;

    /** Item -> result of ichor forging. Each input item is converted one by one. */
    private static final Map<Supplier<Item>, Supplier<Item>> FORGING = Map.of(
            () -> Items.TRIDENT, () -> ModItems.SPECTRAL_SPEAR.get(),
            () -> Items.SOUL_SAND, () -> ModItems.SOUL_FRAGMENT.get(),
            () -> Items.SOUL_SOIL, () -> ModItems.SOUL_FRAGMENT.get(),
            () -> Items.BONE_BLOCK, () -> ModItems.SIFTER_CHITIN.get()
    );

    private IchorHandler() {
    }

    public static boolean isImmune(Entity entity) {
        if (entity.getType().is(ModTags.Entities.SIFT_NATIVES)) {
            return true;
        }
        return entity instanceof LivingEntity living && SiftArmorItem.hasFullSet(living);
    }

    public static void onEntityInIchor(Entity entity, BlockPos pos) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        if (entity instanceof ItemEntity item) {
            tickForging(level, item);
            return;
        }
        if (entity.tickCount % 5 != 0) {
            return;
        }
        if (entity instanceof LivingEntity living && !isImmune(living) && !living.isSpectator()) {
            MobEffectInstance current = living.getEffect(ModEffects.SOUL_BURN);
            if (current == null || current.getDuration() < 40) {
                living.addEffect(new MobEffectInstance(ModEffects.SOUL_BURN, 100, 0, false, true, true));
            }
        }
    }

    private static Item forgingResult(Item input) {
        for (Map.Entry<Supplier<Item>, Supplier<Item>> e : FORGING.entrySet()) {
            if (e.getKey().get() == input) {
                return e.getValue().get();
            }
        }
        return null;
    }

    private static void tickForging(ServerLevel level, ItemEntity entity) {
        ItemStack stack = entity.getItem();
        Item result = forgingResult(stack.getItem());
        if (result == null) {
            return;
        }
        CompoundTag data = entity.getPersistentData();
        int ticks = data.getInt(FORGE_TICKS_TAG) + 1;
        data.putInt(FORGE_TICKS_TAG, ticks);
        if (ticks % 10 == 0) {
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, entity.getX(), entity.getY() + 0.2, entity.getZ(), 3, 0.15, 0.1, 0.15, 0.01);
        }
        if (ticks >= FORGE_TIME) {
            data.remove(FORGE_TICKS_TAG);
            ItemStack out = new ItemStack(result, stack.getCount());
            entity.setItem(out);
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.3, 0));
            level.sendParticles(ParticleTypes.SCULK_SOUL, entity.getX(), entity.getY() + 0.3, entity.getZ(), 12, 0.2, 0.2, 0.2, 0.05);
            level.playSound(null, entity.blockPosition(), SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 0.8F, 0.7F);
            level.playSound(null, entity.blockPosition(), SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}
