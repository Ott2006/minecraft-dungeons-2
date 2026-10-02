package io.github.ott2006.dungeons2.world;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * The tides of the Sift shift every few minutes between <b>Flow</b>, <b>Thrive</b> and <b>Endure</b>, affecting every
 * hero in the dimension:
 * <ul>
 *     <li>Flow: faster movement and mining</li>
 *     <li>Thrive: slow regeneration</li>
 *     <li>Endure: damage resistance</li>
 * </ul>
 */
public final class SiftTides {
    public static final int TIDE_LENGTH = 6000;
    private static final Map<ServerLevel, Integer> LAST_TIDE = new WeakHashMap<>();

    public enum Tide {
        FLOW(ChatFormatting.AQUA),
        THRIVE(ChatFormatting.GREEN),
        ENDURE(ChatFormatting.GOLD);

        final ChatFormatting color;

        Tide(ChatFormatting color) {
            this.color = color;
        }

        public Component displayName() {
            return Component.translatable("tide.dungeons2." + this.name().toLowerCase(java.util.Locale.ROOT)).withStyle(this.color);
        }
    }

    private SiftTides() {
    }

    public static Tide current(ServerLevel level) {
        long cycle = level.getGameTime() / TIDE_LENGTH;
        return Tide.values()[(int) Math.floorMod(cycle, Tide.values().length)];
    }

    public static void tick(ServerLevel level) {
        if (!ModDimensions.isSift(level) || level.getGameTime() % 20 != 0) {
            return;
        }
        Tide tide = current(level);
        Integer last = LAST_TIDE.put(level, tide.ordinal());
        boolean changed = last == null || last != tide.ordinal();
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) {
                continue;
            }
            if (changed && last != null) {
                player.displayClientMessage(Component.translatable("message.dungeons2.tide_shift", tide.displayName()), true);
                level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.AMBIENT, 1.0F, 0.6F);
            }
            switch (tide) {
                case FLOW -> {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0, true, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 60, 0, true, false, true));
                }
                case THRIVE -> {
                    if (level.getGameTime() % 100 == 0) {
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false, true));
                    }
                }
                case ENDURE -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0, true, false, true));
            }
        }
    }
}
