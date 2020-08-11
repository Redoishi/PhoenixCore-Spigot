package fr.redsarow.phoenixcore;

import fr.redsarow.mi18n.api.I18n;
import fr.redsarow.phoenixcore.discord.Bot;
import fr.redsarow.phoenixcore.minecraft.command.Grant;
import fr.redsarow.phoenixcore.minecraft.command.RmGroup;
import fr.redsarow.phoenixcore.minecraft.command.TpMap;
import fr.redsarow.phoenixcore.minecraft.listener.*;
import fr.redsarow.phoenixcore.minecraft.save.*;
import fr.redsarow.phoenixcore.minecraft.save.config.Config;
import fr.redsarow.phoenixcore.minecraft.save.config.GetConfig;
import fr.redsarow.phoenixcore.minecraft.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author redsarow
 * @since 1.0
 */
public final class PhoenixCore extends JavaPlugin {

    public final static double VERS_CONFIG = 1.0;

    public static final Map<String, UUID> waitGranted = new HashMap<>();

    private static I18n i18n;

    public Scoreboard DEFAULT_PLUGIN_SCOREBOARD;
    public GetConfig CONFIG;
    public Bot discordBot;

    private SaveGrantedPlayer grantedPlayer;
    private static Logger LOGGER;

    @Override
    public void onEnable() {
        try {

            LOGGER = getLogger();
            i18n = new I18n(this.getClass(), "phoenixCoreLangMinecraft", Locale.FRENCH, Locale.ENGLISH);

//            DEFAULT_PLUGIN_SCOREBOARD = getServer().getScoreboardManager().getNewScoreboard();
            DEFAULT_PLUGIN_SCOREBOARD = getServer().getScoreboardManager().getMainScoreboard();
            if(DEFAULT_PLUGIN_SCOREBOARD.getObjective("Vie") == null){
                Objective objectiveHealth = DEFAULT_PLUGIN_SCOREBOARD.registerNewObjective("Vie", Criterias.HEALTH, "vie");
                objectiveHealth.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                objectiveHealth.setDisplayName(ChatColor.GREEN + "vie");
                objectiveHealth.setRenderType(RenderType.HEARTS);
            }
            Objective objectiveDeath = DEFAULT_PLUGIN_SCOREBOARD.getObjective("Mort");
            if(objectiveDeath == null){
                objectiveDeath = DEFAULT_PLUGIN_SCOREBOARD.registerNewObjective("Mort", Criterias.DEATHS, "Mort");
                objectiveDeath.setDisplaySlot(DisplaySlot.SIDEBAR);
                objectiveDeath.setDisplayName(ChatColor.RED + "Mort");
            }

            getLogger().info(i18n.get("init.config"));
            if (!Config.checkConfig(this, "config.yml", "")) {
                getLogger().severe("Error setup config. Stop plugin");
                this.getPluginLoader().disablePlugin(this);
                return;
            }
            CONFIG = new GetConfig(this);


            getLogger().info(i18n.get("init.saveWorlds"));
            SaveWorlds SaveWorlds = new SaveWorlds(this);
            SaveWorlds.loadWorlds();

            getLogger().info(i18n.get("init.playerWorldParam"));
            SavePlayerWorldParam playerWorldParam = new SavePlayerWorldParam(this);

            getLogger().info(i18n.get("init.saveGrantedPlayer"));
            grantedPlayer = new SaveGrantedPlayer(this, objectiveDeath);

//TODO 1.13
            SavePlayerInformation savePlayerInformation = new SavePlayerInformation(this);


            //event
            getLogger().info(i18n.get("init.listener"));
            PluginManager pm = Bukkit.getPluginManager();
            pm.registerEvents(new Join(this, grantedPlayer), this);
            pm.registerEvents(new Leave(this), this);
            pm.registerEvents(new PlayerWorldChange(this), this);
            pm.registerEvents(new Death(this, objectiveDeath), this);
            pm.registerEvents(new AdvancementDone(this), this);


            //command
            getLogger().info(i18n.get("init.command"));
            Field f = null;
            f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            CommandMap commandMap = (CommandMap) f.get(Bukkit.getServer());

            new TpMap(this, commandMap, playerWorldParam, savePlayerInformation);
            new RmGroup(this, commandMap, SaveWorlds, playerWorldParam);
            new Grant(this, commandMap);

            if (CONFIG.getBoolVal("discord")) {
                getLogger().info(i18n.get("init.discord"));
                discordBot = new Bot(this);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            this.getPluginLoader().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if(discordBot != null){
            discordBot.disconnect();
        }
    }

    public Map<String, Integer> getPlayerDeathCount() {
//        return playerDeathCount;
        Map<String, Integer> allDeath = new HashMap<>();
        Objective objectiveDeath = DEFAULT_PLUGIN_SCOREBOARD.getObjective("Mort");
        Arrays.stream(getServer().getOfflinePlayers())
                .forEach(offlinePlayer -> {
                    int score = objectiveDeath.getScore(offlinePlayer.getName()).getScore();
                    allDeath.put(offlinePlayer.getUniqueId().toString(), score);
                });

        return allDeath;
    }

    public void addGrant(String sender, String newPlayer) {
        try {
            grantedPlayer.addGranted(Bukkit.getOfflinePlayer(waitGranted.get(newPlayer)));

            String msg = i18n.get("msg.addGrant"
                    , Color.INFO + newPlayer + Color.OK
                    , Color.INFO + sender + Color.OK);
            getServer().broadcastMessage(msg);
            String discordMsg = i18n.get("msg.addGrant"
                    , newPlayer
                    , sender);
            discordBot.getSendMessage().sendNewGrantedPlayer(discordMsg);

        } catch (IOException e) {
            getServer().broadcastMessage("error");
            discordBot.getSendMessage().sendMsg("error");

            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    public static I18n getI18n() {
        return i18n;
    }
}
