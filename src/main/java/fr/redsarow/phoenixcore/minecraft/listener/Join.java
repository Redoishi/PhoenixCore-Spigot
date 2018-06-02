package fr.redsarow.phoenixcore.minecraft.listener;

import fr.redsarow.phoenixcore.PhoenixCore;
import fr.redsarow.phoenixcore.minecraft.WorldGroup;
import fr.redsarow.phoenixcore.minecraft.save.SaveGrantedPlayer;
import fr.redsarow.phoenixcore.minecraft.util.Color;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

/**
 * @author redsarow
 * @since 1.0
 */
public class Join implements Listener {

    PhoenixCore pl;
    private SaveGrantedPlayer grantedPlayer;

    public Join(PhoenixCore phoenixCore, SaveGrantedPlayer grantedPlayer) {
        this.pl = phoenixCore;
        this.grantedPlayer = grantedPlayer;
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
        if(group.isScoreboard()){
            player.setScoreboard(pl.DEFAULT_PLUGIN_SCOREBOARD);
        }else{
            player.setScoreboard(pl.getServer().getScoreboardManager().getMainScoreboard());
        }

        UUID uniqueId = player.getUniqueId();
        if(!grantedPlayer.isGranted(uniqueId)){
            String playerName = player.getName();
            player.setGameMode(GameMode.SPECTATOR);
            PhoenixCore.waitGranted.put(playerName, uniqueId);

            pl.discordBot.getSendMessage().sendNotGrantedPlayer(
                    "Le joueur '"+playerName+"', uuid '"+ uniqueId
                            +"' vient de se connecter. "
                    ,playerName);

            TextComponent msgTC = new TextComponent(
                    "Le joueur '"+Color.INFO+playerName+Color.WARN+"', uuid '"+Color.INFO+ uniqueId+Color.WARN
                    +"' vient de se connecter. ");
            msgTC.setColor(Color.WARN);
            TextComponent cmdMsgTC = new TextComponent("/grant "+playerName);
            cmdMsgTC.setColor(Color.WARN);
            cmdMsgTC.setUnderlined(true);
            cmdMsgTC.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/grant "+playerName));

            msgTC.addExtra(cmdMsgTC);
            pl.getServer().spigot().broadcast(msgTC);
        }
    }
}
