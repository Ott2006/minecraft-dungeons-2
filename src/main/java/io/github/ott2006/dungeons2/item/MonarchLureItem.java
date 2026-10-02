package io.github.ott2006.dungeons2.item;

import io.github.ott2006.dungeons2.entity.Monarch;
import io.github.ott2006.dungeons2.registry.ModEntities;
import io.github.ott2006.dungeons2.world.ModDimensions;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** Lures the Monarch, the huge long-clawed sculker boss of the Carapace. Only works in the Sift. */
public class MonarchLureItem extends Item {
    public MonarchLureItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (!ModDimensions.isSift(level)) {
            if (player != null && !level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.dungeons2.monarch_lure.wrong_dimension")
                        .withStyle(ChatFormatting.GOLD), true);
            }
            return InteractionResult.FAIL;
        }
        if (level instanceof ServerLevel server) {
            BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
            Monarch monarch = ModEntities.MONARCH.get().spawn(server, pos.above(), MobSpawnType.EVENT);
            if (monarch == null) {
                return InteractionResult.FAIL;
            }
            server.sendParticles(ParticleTypes.SCULK_SOUL, monarch.getX(), monarch.getY() + 2, monarch.getZ(), 60, 1.0, 2.0, 1.0, 0.05);
            server.playSound(null, pos, SoundEvents.WARDEN_EMERGE, SoundSource.HOSTILE, 2.0F, 1.2F);
            if (player == null || !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.dungeons2.monarch_lure.desc").withStyle(ChatFormatting.GRAY));
    }
}
