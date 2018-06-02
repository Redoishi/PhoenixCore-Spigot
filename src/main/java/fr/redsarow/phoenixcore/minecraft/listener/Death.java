package fr.redsarow.phoenixcore.minecraft.listener;

import fr.redsarow.phoenixcore.PhoenixCore;
import fr.redsarow.phoenixcore.minecraft.WorldGroup;
import fr.redsarow.phoenixcore.minecraft.save.SaveDeathCount;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Objective;

import java.util.HashMap;
import java.util.Map;

/**
 * @author redsarow
 * @since 1.0
 */
public class Death implements Listener {

    private final static Map<Player, WorldGroup> deathInGroup = new HashMap<>();

    private SaveDeathCount playerDeathCount;
    private Objective objectiveDeath;
    PhoenixCore pl;

    public Death(PhoenixCore phoenixCore, SaveDeathCount playerDeathCount, Objective objectiveDeath) {
        this.pl = phoenixCore;
        this.playerDeathCount = playerDeathCount;
        this.objectiveDeath = objectiveDeath;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        World world = player.getWorld();
        WorldGroup worldGroup = WorldGroup.findWorldGroupByWorldName(world.getName());
        if(worldGroup.isDeadCount()){
            int increment = playerDeathCount.increment(player);
            objectiveDeath.getScore(player.getDisplayName()).setScore(increment);
        }

        pl.discordBot.getSendMessage().sendDeath(world.getName()+"\n"+event.getDeathMessage());

        if(player.getBedSpawnLocation() == null){
            deathInGroup.put(player, worldGroup);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        if(deathInGroup.containsKey(player)){
            event.setRespawnLocation(
                    pl.getServer().getWorld(
                            deathInGroup.get(player).getWorlds().iterator().next()
                    ).getSpawnLocation()
            );
            deathInGroup.remove(player);
        }
    }
}
