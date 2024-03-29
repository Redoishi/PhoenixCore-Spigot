package fr.redsarow.phoenixcore.minecraft.listener;

import fr.redsarow.phoenixcore.PhoenixCore;
import fr.redsarow.phoenixcore.minecraft.WorldGroup;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.HashMap;
import java.util.Map;

/**
 * @author redsarow
 * @since 1.0
 */
public class Death implements Listener {

    private final static Map<Player, WorldGroup> deathInGroup = new HashMap<>();
    private final Objective objectiveDeath;
    PhoenixCore pl;

    public Death(PhoenixCore phoenixCore, Objective objectiveDeath) {
        this.pl = phoenixCore;
        this.objectiveDeath = objectiveDeath;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        World world = player.getWorld();
        WorldGroup worldGroup = WorldGroup.findWorldGroupByWorldName(world.getName());
        if (!worldGroup.isDeadCount()) {
//            int increment = playerDeathCount.increment(player);
            Score score = objectiveDeath.getScore(player.getDisplayName());
            score.setScore(score.getScore() - 1);
        }else {
            pl.discordBot.getSendMessage().sendDeath(world.getName() + "\n" + event.getDeathMessage());
        }

        if (player.getBedSpawnLocation() == null) {
            deathInGroup.put(player, worldGroup);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (deathInGroup.containsKey(player)) {
            event.setRespawnLocation(
                    pl.getServer().getWorld(
                            deathInGroup.get(player).getWorlds().iterator().next()
                    ).getSpawnLocation()
            );
            deathInGroup.remove(player);
        }
    }
}
