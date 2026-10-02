package io.github.ott2006.dungeons2.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

/** Decides who counts as an enemy for area-of-effect abilities. */
public final class CombatUtil {
    private CombatUtil() {
    }

    public static boolean isEnemyOf(LivingEntity user, LivingEntity other) {
        if (other == user || !other.isAlive() || other.isAlliedTo(user) || user.isAlliedTo(other)) {
            return false;
        }
        if (other instanceof OwnableEntity ownable && ownable.getOwner() == user) {
            return false;
        }
        if (other instanceof Player) {
            return user instanceof Mob;
        }
        if (user instanceof Player) {
            return other instanceof Enemy || (other instanceof Mob mob && mob.getTarget() == user);
        }
        return true;
    }
}
