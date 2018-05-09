package fr.redsarow.phoenixcore.minecraft.listener;

import fr.redsarow.phoenixcore.minecraft.Group;
import fr.redsarow.phoenixcore.minecraft.PhoenixCore;
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
        player.sendMessage("Connecter sur " + ChatColor.AQUA + worldName);
        Group group = Group.findWorld(worldName);
        if(group!= null){
            Team team = group.getTeamForWorld(worldName);
            team.addEntry(player.getName());
        }
    }
}
