package io.github.ott2006.dungeons2.world;

import io.github.ott2006.dungeons2.entity.EchoGolem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * The song of the Singers. It pacifies wardens and makes echo golems dance.
 */
public final class SiftSong {
    /** A short, gentle melody (semitones relative to F#3, as used by note blocks). */
    private static final int[] MELODY = {12, 16, 19, 24, 19, 16, 14, 17, 21, 19};

    private SiftSong() {
    }

    public static float notePitch(int step) {
        int note = MELODY[Math.floorMod(step, MELODY.length)];
        return (float) Math.pow(2.0, (note - 12) / 12.0);
    }

    /** Plays one note of the melody at the singer and spawns a note particle. */
    public static void playNote(ServerLevel level, Entity singer, int step, float volume) {
        Vec3 head = singer.getEyePosition();
        level.playSound(null, head.x, head.y, head.z, SoundEvents.NOTE_BLOCK_CHIME, SoundSource.NEUTRAL, volume, notePitch(step));
        if (step % 2 == 0) {
            level.playSound(null, head.x, head.y, head.z, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.NEUTRAL, volume * 0.6F, notePitch(step));
        }
        int note = MELODY[Math.floorMod(step, MELODY.length)];
        level.sendParticles(ParticleTypes.NOTE, head.x, head.y + 0.6, head.z, 0, note / 24.0, 0.0, 0.0, 1.0);
    }

    /** Applies the calming effect of the song to everything in range. */
    public static void soothe(ServerLevel level, Entity singer, double radius) {
        AABB box = singer.getBoundingBox().inflate(radius);
        for (Warden warden : level.getEntitiesOfClass(Warden.class, box)) {
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, warden.getBoundingBox().inflate(48))) {
                warden.clearAnger(target);
            }
            warden.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            warden.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
            level.sendParticles(ParticleTypes.NOTE, warden.getX(), warden.getEyeY() + 0.8, warden.getZ(), 0, 0.3, 0.0, 0.0, 1.0);
        }
        for (EchoGolem golem : level.getEntitiesOfClass(EchoGolem.class, box)) {
            golem.startDancing(100);
        }
    }
}
