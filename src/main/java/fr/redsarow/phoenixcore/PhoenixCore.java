package fr.redsarow.phoenixcore;

import fr.redsarow.phoenixcore.discord.Bot;
import fr.redsarow.phoenixcore.minecraft.cmd.RmGroup;
import fr.redsarow.phoenixcore.minecraft.cmd.TpMap;
import fr.redsarow.phoenixcore.minecraft.listener.Death;
import fr.redsarow.phoenixcore.minecraft.listener.Join;
import fr.redsarow.phoenixcore.minecraft.listener.Leave;
import fr.redsarow.phoenixcore.minecraft.listener.PlayerWorldChange;
import fr.redsarow.phoenixcore.minecraft.save.SaveDeathCount;
import fr.redsarow.phoenixcore.minecraft.save.SavePlayerWorldParam;
import fr.redsarow.phoenixcore.minecraft.save.SaveWorlds;
import fr.redsarow.phoenixcore.minecraft.save.config.Config;
import fr.redsarow.phoenixcore.minecraft.save.config.GetConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

/**
 * @author redsarow
 * @since 1.0
 */
public final class PhoenixCore extends JavaPlugin {

    public final static double VERS_CONFIG = 1.0;

    public Scoreboard DEFAULT_PLUGIN_SCOREBOARD;
    public GetConfig CONFIG;
    public Bot discordBot;
    private SaveDeathCount playerDeathCount;

    @Override
    public void onEnable() {
        try {

            DEFAULT_PLUGIN_SCOREBOARD = getServer().getScoreboardManager().getNewScoreboard();
            Objective objectiveHealth = DEFAULT_PLUGIN_SCOREBOARD.registerNewObjective("vie", "health");
            objectiveHealth.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            objectiveHealth.setDisplayName("vie");
            Objective objectiveDeath = DEFAULT_PLUGIN_SCOREBOARD.registerNewObjective("Mort", "dummy");
            objectiveDeath.setDisplaySlot(DisplaySlot.SIDEBAR);
            objectiveDeath.setDisplayName(ChatColor.RED+"Mort");

            getLogger().info("init config");
            if (!Config.checkConfig(this, "config.yml", "")) {
                getLogger().severe("Error setup config. Stop plugin");
                this.getPluginLoader().disablePlugin(this);
                return;
            }
            getLogger().info("get config");
            CONFIG = new GetConfig(this);


            getLogger().info("init SaveWorlds");
            SaveWorlds SaveWorlds = new SaveWorlds(this);
            SaveWorlds.loadWorlds();

            getLogger().info("init PlayerWorldParam");
            SavePlayerWorldParam playerWorldParam = new SavePlayerWorldParam(this);

            getLogger().info("init PlayerDeathCount");
            playerDeathCount = new SaveDeathCount(this);
            Map<String, Integer> allPlayerDeath = playerDeathCount.getAll();
            allPlayerDeath.forEach((s, integer) -> objectiveDeath.getScore(
                    Bukkit.getOfflinePlayer(UUID.fromString(s)).getName())
                    .setScore(integer)
            );


            //event
            getLogger().info("init Listener");
            PluginManager pm = Bukkit.getPluginManager();
            pm.registerEvents(new Join(this), this);
            pm.registerEvents(new Leave(this), this);
            pm.registerEvents(new PlayerWorldChange(this), this);
            pm.registerEvents(new Death(this, playerDeathCount, objectiveDeath), this);


            //cmd
            getLogger().info("init commands");
            Field f = null;
            f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            CommandMap commandMap = (CommandMap) f.get(Bukkit.getServer());

            new TpMap(this, commandMap, playerWorldParam);
            new RmGroup(this, commandMap, SaveWorlds, playerWorldParam);

            if(CONFIG.getBoolVal("discord")){
                getLogger().info("init discord Bot");
                discordBot = new Bot(this);
                //event
//                pm.registerEvents(new (this), this);
            }

        } catch (Exception ex) {
            getLogger().severe(ex.getLocalizedMessage());
            ex.printStackTrace();
            this.getPluginLoader().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public SaveDeathCount getPlayerDeathCount() {
        return playerDeathCount;
    }
}
