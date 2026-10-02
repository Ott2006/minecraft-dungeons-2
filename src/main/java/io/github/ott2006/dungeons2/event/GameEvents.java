package io.github.ott2006.dungeons2.event;

import io.github.ott2006.dungeons2.item.SiftArmorItem;
import io.github.ott2006.dungeons2.item.SiftWeaponItem;
import io.github.ott2006.dungeons2.registry.ModEffects;
import io.github.ott2006.dungeons2.registry.ModFluids;
import io.github.ott2006.dungeons2.world.IchorHandler;
import io.github.ott2006.dungeons2.world.SiftTides;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Game bus events: Sift tides, artifact and armor effects and weapon traits that need events. */
public final class GameEvents {
    private GameEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(GameEvents::onLevelTick);
        NeoForge.EVENT_BUS.addListener(GameEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(GameEvents::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(GameEvents::onExperienceDrop);
        NeoForge.EVENT_BUS.addListener(GameEvents::onDeath);
        NeoForge.EVENT_BUS.addListener(GameEvents::onEntityTick);
    }

    private static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide && entity.isInFluidType(ModFluids.ICHOR_TYPE.get())) {
            IchorHandler.onEntityInIchor(entity, entity.blockPosition());
        }
    }

    private static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            SiftTides.tick(level);
        }
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || player.tickCount % 20 != 0) {
            return;
        }
        // Mad Sifter armor: enraged when badly hurt
        if (player.getHealth() < player.getMaxHealth() * 0.5F && SiftArmorItem.hasFullMadSet(player)) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0, true, true));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0, true, true));
        }
    }

    private static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        // Echo Ocarina: ranged attacks are absorbed by echoes
        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE) && entity.hasEffect(ModEffects.ECHO_WARD)) {
            event.setCanceled(true);
            if (entity.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.SCULK_CHARGE_POP, entity.getX(), entity.getY(0.5), entity.getZ(), 10, 0.4, 0.4, 0.4, 0.05);
                level.playSound(null, entity.blockPosition(), SoundEvents.SCULK_CLICKING, SoundSource.PLAYERS, 1.0F, 1.5F);
            }
        }
    }

    private static boolean holds(Player player, SiftWeaponItem.Kind kind) {
        Item item = player.getMainHandItem().getItem();
        return item instanceof SiftWeaponItem weapon && weapon.getKind() == kind;
    }

    private static void onExperienceDrop(LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        // Soul Reaper: gathers 60% more souls
        if (player != null && holds(player, SiftWeaponItem.Kind.SOUL_REAPER)) {
            event.setDroppedExperience(Math.round(event.getDroppedExperience() * 1.6F));
        }
    }

    private static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player) || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        LivingEntity victim = event.getEntity();
        if (holds(player, SiftWeaponItem.Kind.CACOPHONOUS_CLEAVER)) {
            // restores health for every soul gathered
            player.heal(Math.max(1.0F, player.getMaxHealth() * 0.07F) + 1.5F);
            level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), SoundSource.PLAYERS, 1.0F,
                    0.5F + level.random.nextFloat());
            level.sendParticles(ParticleTypes.NOTE, player.getX(), player.getEyeY() + 0.5, player.getZ(), 3, 0.4, 0.2, 0.4, 1.0);
        }
        if (holds(player, SiftWeaponItem.Kind.SOUL_REAPER) || holds(player, SiftWeaponItem.Kind.CACOPHONOUS_CLEAVER)) {
            level.sendParticles(ParticleTypes.SCULK_SOUL, victim.getX(), victim.getY(0.5), victim.getZ(), 8, 0.3, 0.4, 0.3, 0.05);
        }
    }
}
