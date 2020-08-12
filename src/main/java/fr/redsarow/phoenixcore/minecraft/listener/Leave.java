package fr.redsarow.phoenixcore.minecraft.listener;

import fr.redsarow.phoenixcore.PhoenixCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;

/**
 * @author redsarow
 * @since 1.0
 */
public class Leave implements Listener {

    PhoenixCore pl;

    public Leave(PhoenixCore phoenixCore) {
        this.pl = phoenixCore;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Team team = pl.DEFAULT_PLUGIN_SCOREBOARD.getTeam(player.getWorld().getName());
        if (team != null) {
            team.removeEntry(player.getName());
        }
    }
}
