package fr.redsarow.phoenixcore;

import fr.redsarow.phoenixcore.discord.Bot;
import fr.redsarow.phoenixcore.minecraft.cmd.TpMap;
import fr.redsarow.phoenixcore.minecraft.listener.Join;
import fr.redsarow.phoenixcore.minecraft.listener.Leave;
import fr.redsarow.phoenixcore.minecraft.save.config.Config;
import fr.redsarow.phoenixcore.minecraft.save.config.GetConfig;
import fr.redsarow.phoenixcore.minecraft.save.SavePlayerWorldParam;
import fr.redsarow.phoenixcore.minecraft.save.SaveWorlds;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Field;

/**
 * @author redsarow
 * @since 1.0
 */
public final class PhoenixCore extends JavaPlugin {

    public final static double VERS_CONFIG = 1.0;
    public static Scoreboard TEAM_SCOREBOARD;
    public static GetConfig CONFIG;

    @Override
    public void onEnable() {
        try {

            TEAM_SCOREBOARD = getServer().getScoreboardManager().getNewScoreboard();

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

            getLogger().info("init playerWorldParam");
            SavePlayerWorldParam playerWorldParam = new SavePlayerWorldParam(this);


            //event
            getLogger().info("init Listener");
            PluginManager pm = Bukkit.getPluginManager();
            pm.registerEvents(new Join(this), this);
            pm.registerEvents(new Leave(this), this);


            //cmd
            getLogger().info("init commands");
            Field f = null;
            f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            CommandMap commandMap = (CommandMap) f.get(Bukkit.getServer());

            new TpMap(this, commandMap, playerWorldParam);
//            new TimeSet(this, "timeSet", commandMap);

            if(CONFIG.getBoolVal("discord")){
                getLogger().info("init discord Bot");
                new Bot(this);
            }

        } catch (Exception ex) {
            getLogger().severe(ex.getLocalizedMessage());
            this.getPluginLoader().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}