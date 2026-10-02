package io.github.ott2006.dungeons2.item;

import io.github.ott2006.dungeons2.registry.ModEffects;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Artifacts are active abilities with a cooldown, just like in Minecraft Dungeons II.
 */
public class ArtifactItem extends Item {
    public enum Kind {
        /** Protects against ranged attacks. */
        ECHO_OCARINA(400),
        /** Their music turns your skin as thick as carapace. */
        WARDING_CHIMES(400),
        /** Knock enemies over and leave ears ringing. */
        HUMBLING_HORN(160),
        /** Instantly sprout into deadly vines, poisoning and binding. */
        CORRUPTED_SEEDS(160),
        /** Soul-powered hex that hurts everything around you and heals you for every enemy hit. */
        SOUL_HARVESTER(200),
        /** Soul-powered beam that pierces through every enemy in its path. */
        CORRUPTED_BEACON(240);

        final int cooldown;

        Kind(int cooldown) {
            this.cooldown = cooldown;
        }
    }

    private final Kind kind;

    public ArtifactItem(Kind kind) {
        super(new Item.Properties().stacksTo(1).rarity(kind == Kind.CORRUPTED_BEACON || kind == Kind.SOUL_HARVESTER ? Rarity.RARE : Rarity.UNCOMMON));
        this.kind = kind;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel server && !this.activate(server, player)) {
            return InteractionResultHolder.fail(stack);
        }
        player.getCooldowns().addCooldown(this, this.kind.cooldown);
        player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private boolean activate(ServerLevel level, Player player) {
        switch (this.kind) {
            case ECHO_OCARINA -> {
                for (LivingEntity ally : allies(level, player, 6.0)) {
                    ally.addEffect(new MobEffectInstance(ModEffects.ECHO_WARD, 200, 0));
                    level.sendParticles(ParticleTypes.SCULK_CHARGE_POP, ally.getX(), ally.getY(0.5), ally.getZ(), 15, 0.5, 0.6, 0.5, 0.02);
                }
                play(level, player, SoundEvents.NOTE_BLOCK_FLUTE.value(), 1.2F);
                play(level, player, SoundEvents.SCULK_CLICKING, 1.0F);
            }
            case WARDING_CHIMES -> {
                for (LivingEntity ally : allies(level, player, 8.0)) {
                    ally.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1));
                    level.sendParticles(ParticleTypes.WAX_ON, ally.getX(), ally.getY(0.5), ally.getZ(), 12, 0.4, 0.6, 0.4, 0.02);
                }
                play(level, player, SoundEvents.NOTE_BLOCK_CHIME.value(), 1.0F);
                play(level, player, SoundEvents.AMETHYST_BLOCK_CHIME, 0.8F);
            }
            case HUMBLING_HORN -> {
                for (LivingEntity enemy : enemies(level, player, player.getBoundingBox().inflate(6.0))) {
                    Vec3 push = enemy.position().subtract(player.position()).normalize().scale(1.6);
                    enemy.push(push.x, 0.45, push.z);
                    enemy.hurtMarked = true;
                    enemy.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
                    enemy.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
                }
                level.sendParticles(ParticleTypes.SONIC_BOOM, player.getX(), player.getY(0.5), player.getZ(), 1, 0, 0, 0, 0);
                level.playSound(null, player.blockPosition(), SoundEvents.RAID_HORN.value(), SoundSource.PLAYERS, 1.5F, 1.4F);
            }
            case CORRUPTED_SEEDS -> {
                Vec3 eye = player.getEyePosition();
                Vec3 end = eye.add(player.getLookAngle().scale(14.0));
                BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                Vec3 at = hit.getLocation();
                for (LivingEntity enemy : enemies(level, player, new AABB(at, at).inflate(3.5))) {
                    enemy.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
                    enemy.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 70, 4));
                }
                level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, at.x, at.y + 0.5, at.z, 50, 2.0, 0.6, 2.0, 0.0);
                level.sendParticles(new DustParticleOptions(new Vector3f(0.35F, 0.75F, 0.2F), 1.5F), at.x, at.y + 0.3, at.z, 40, 2.0, 0.3, 2.0, 0.0);
                level.playSound(null, at.x, at.y, at.z, SoundEvents.ROOTED_DIRT_PLACE, SoundSource.PLAYERS, 1.5F, 0.6F);
            }
            case SOUL_HARVESTER -> {
                int hits = 0;
                for (LivingEntity enemy : enemies(level, player, player.getBoundingBox().inflate(6.0))) {
                    enemy.hurt(level.damageSources().indirectMagic(player, player), 7.0F);
                    level.sendParticles(ParticleTypes.SCULK_SOUL, enemy.getX(), enemy.getY(0.6), enemy.getZ(), 8, 0.3, 0.4, 0.3, 0.05);
                    hits++;
                }
                player.heal(hits);
                level.sendParticles(ParticleTypes.SCULK_SOUL, player.getX(), player.getY(0.5), player.getZ(), 40, 3.0, 0.5, 3.0, 0.02);
                play(level, player, SoundEvents.SOUL_ESCAPE.value(), 2.0F);
            }
            case CORRUPTED_BEACON -> {
                Vec3 eye = player.getEyePosition();
                Vec3 dir = player.getLookAngle();
                Vec3 end = eye.add(dir.scale(20.0));
                BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                Vec3 stop = hit.getLocation();
                double length = stop.distanceTo(eye);
                for (double d = 1.0; d < length; d += 0.5) {
                    Vec3 p = eye.add(dir.scale(d));
                    level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0.0);
                    if (((int) (d * 2)) % 3 == 0) {
                        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z, 1, 0.1, 0.1, 0.1, 0.0);
                    }
                }
                AABB box = new AABB(eye, stop).inflate(1.0);
                for (LivingEntity enemy : enemies(level, player, box)) {
                    Vec3 rel = enemy.getBoundingBox().getCenter().subtract(eye);
                    double along = rel.dot(dir);
                    if (along > 0 && along < length + 1 && rel.subtract(dir.scale(along)).length() < 1.4) {
                        enemy.hurt(level.damageSources().indirectMagic(player, player), 10.0F);
                    }
                }
                play(level, player, SoundEvents.BEACON_POWER_SELECT, 1.0F);
                play(level, player, SoundEvents.WARDEN_SONIC_CHARGE, 0.6F);
            }
        }
        return true;
    }

    private static void play(ServerLevel level, Player player, SoundEvent sound, float pitch) {
        level.playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, 1.0F, pitch);
    }

    private static List<LivingEntity> allies(ServerLevel level, Player player, double radius) {
        return level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius),
                e -> e == player || e instanceof Player || (e instanceof OwnableEntity o && o.getOwner() == player));
    }

    private static List<LivingEntity> enemies(ServerLevel level, Player player, AABB box) {
        return level.getEntitiesOfClass(LivingEntity.class, box, e -> CombatUtil.isEnemyOf(player, e));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.dungeons2.artifact.cooldown", this.kind.cooldown / 20).withStyle(ChatFormatting.DARK_AQUA));
    }
}
