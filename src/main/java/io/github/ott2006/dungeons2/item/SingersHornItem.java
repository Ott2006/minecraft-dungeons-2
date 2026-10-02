package io.github.ott2006.dungeons2.item;

import io.github.ott2006.dungeons2.world.SiftPortalShape;
import io.github.ott2006.dungeons2.world.SiftSong;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Carries the song of the Singers. Played at a frame of reinforced or resonant deepslate it opens a Deep Dark portal
 * to the Sift. Played anywhere else it calms wardens nearby.
 */
public class SingersHornItem extends Item {
    public SingersHornItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clicked = context.getClickedPos();
        Player player = context.getPlayer();
        if (!SiftPortalShape.isFrame(level.getBlockState(clicked))) {
            return InteractionResult.PASS;
        }
        if (player != null && player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        // try the clicked side first, then every other side of the clicked frame block
        BlockPos start = clicked.relative(context.getClickedFace());
        boolean lit = SiftPortalShape.tryLight(level, start);
        for (Direction dir : Direction.values()) {
            if (lit) {
                break;
            }
            if (dir != context.getClickedFace()) {
                start = clicked.relative(dir);
                lit = SiftPortalShape.tryLight(level, start);
            }
        }
        if (lit) {
            ServerLevel server = (ServerLevel) level;
            server.playSound(null, start, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 1.5F, 0.8F);
            server.playSound(null, start, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.5F, 1.2F);
            if (player != null) {
                for (int i = 0; i < 6; i++) {
                    SiftSong.playNote(server, player, i, 0.8F);
                }
            }
            server.sendParticles(ParticleTypes.SCULK_SOUL, start.getX() + 0.5, start.getY() + 0.5, start.getZ() + 0.5,
                    30, 1.0, 1.5, 1.0, 0.02);
            level.gameEvent(player, GameEvent.BLOCK_PLACE, start);
            if (player != null) {
                player.getCooldowns().addCooldown(this, 40);
            }
            return InteractionResult.CONSUME;
        }
        if (player != null) {
            player.displayClientMessage(Component.translatable("message.dungeons2.portal_incomplete")
                    .withStyle(ChatFormatting.DARK_AQUA), true);
            player.getCooldowns().addCooldown(this, 20);
        }
        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel server) {
            for (int i = 0; i < 4; i++) {
                SiftSong.playNote(server, player, player.getRandom().nextInt(10), 1.0F);
            }
            SiftSong.soothe(server, player, 24.0);
            level.gameEvent(player, GameEvent.INSTRUMENT_PLAY, player.position());
        }
        player.getCooldowns().addCooldown(this, 100);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.dungeons2.singers_horn.desc").withStyle(ChatFormatting.GRAY));
    }
}
