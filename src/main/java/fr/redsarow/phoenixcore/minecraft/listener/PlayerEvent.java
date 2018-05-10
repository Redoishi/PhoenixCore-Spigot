package fr.redsarow.phoenixcore.minecraft.listener;

import fr.redsarow.phoenixcore.minecraft.PhoenixCore;
import fr.redsarow.phoenixcore.minecraft.WorldGroup;
import fr.redsarow.phoenixcore.minecraft.util.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scoreboard.Team;

/**
 * @author redsarow
 * @since 1.0
 */
public class PlayerEvent implements Listener {

    PhoenixCore pl;

    public PlayerEvent(PhoenixCore phoenixCore) {
        this.pl = phoenixCore;
    }

    @EventHandler
    public void worldChange(PlayerChangedWorldEvent event){
        Player player = event.getPlayer();

        String worldName = player.getWorld().getName();
        WorldGroup group = WorldGroup.findWorldGroupByWorldName(worldName);
        if (group == null) {
            player.sendMessage(Color.ERROR+"Une erreur est survenue contacter Redsarow");
            return;
        }
        Team team = group.getTeamForWorld(worldName);
        if(team != null){
            team.addEntry(player.getName());
        }else{
            Team lastTeam = PhoenixCore.TEAM_SCOREBOARD.getTeam(player.getWorld().getName());
            if(lastTeam!=null){
                lastTeam.removeEntry(player.getName());
            }
        }
    }
}
