package fr.redsarow.phoenixcore.minecraft.listener;

import fr.redsarow.phoenixcore.PhoenixCore;
import fr.redsarow.phoenixcore.minecraft.WorldGroup;
import fr.redsarow.phoenixcore.minecraft.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

/**
 * @author redsarow
 * @since 1.0
 */
public class Join implements Listener {

    PhoenixCore pl;

    public Join(PhoenixCore PhoenixCore) {
        this.pl = PhoenixCore;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();
        WorldGroup group = WorldGroup.findWorldGroupByWorldName(worldName);
        if (group == null) {
            player.sendMessage(Color.ERROR+"Une erreur est survenue contacter Redsarow");
            return;
        }
        player.sendMessage("Connecter sur " + Color.INFO + worldName + ChatColor.RESET
                + " du groupe " + Color.INFO + group.getName());
        Team team = group.getTeamForWorld(worldName);
        if(team != null){
            team.addEntry(player.getName());
        }
    }
}
