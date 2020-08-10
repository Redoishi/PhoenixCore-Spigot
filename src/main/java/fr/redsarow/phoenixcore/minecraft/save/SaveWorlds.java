package fr.redsarow.phoenixcore.minecraft.save;

import fr.redsarow.phoenixcore.PhoenixCore;
import fr.redsarow.phoenixcore.minecraft.WorldGroup;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static fr.redsarow.phoenixcore.minecraft.save.Save.initFile;
import static fr.redsarow.phoenixcore.minecraft.save.Save.setSectionVal;

/**
 * File:
 * <pre>
 * {@code
 * Group:
 *   - InitGroup
 *   - G1
 *   - G2
 * InitGroup:
 *   Worlds:
 *     - W1
 *     - W2
 *   gameMode: SURVIVAL
 *   scoreboard: true
 *   deadCount: true
 *   DefaultTeam:
 *     color:
 *     prefix:
 * G1:
 *   Worlds:
 *     - W1
 *     - W2
 *   scoreboard: true
 *   deadCount: false
 *   Team:
 *     W1:
 *       color:
 *       prefix:
 * G2:
 *   Worlds:
 *     - W1
 *     - W2
 *   scoreboard: false
 *   deadCount: false
 * }
 * </pre>
 * for color see {@link org.bukkit.ChatColor}
 * for gameMode see {@link org.bukkit.GameMode}
 *
 * @author redsarow
 * @since 1.0
 */
public class SaveWorlds {

    private final static String FILE = "Worlds.yml";
    private final static String GROUP = "Group";
    private final static String WORLDS = "Worlds";
    private final static String DEFAULT_TEAM = "DefaultTeam";
    private final static String TEAM = "Team";
    private final static String TEAM_COLOR = "color";
    private final static String TEAM_PREFIX = "prefix";
    private final static String SCOREBOARD = "scoreboard";
    private final static String DEAD_COUNT = "deadCount";
    private final static String GAME_MODE = "gameMode";

    private File dataFolder;
    private File file;
    private PhoenixCore pl;
    private YamlConfiguration configFile;

    public SaveWorlds(PhoenixCore pl) throws IOException {

        this.pl = pl;
        this.pl.getLogger().info("save " + FILE);

        this.dataFolder = this.pl.getDataFolder();

        initFile(pl, dataFolder, FILE);


        file = new File(this.dataFolder, FILE);
        configFile = YamlConfiguration.loadConfiguration(file);
        if (configFile.getStringList(GROUP).isEmpty()) {
            setSectionVal(configFile, GROUP, new ArrayList<String>() {{
                add("InitGroup");
            }});
            setSectionVal(
                    configFile,
                    "InitGroup." + WORLDS,
                    pl.getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList())
            );
        }

        configFile.save(file);
    }

    public void loadWorlds() {
        for (String s : configFile.getStringList(GROUP)) {
            loadWorldofGroupe(s);
        }
    }

    public void loadWorldofGroupe(String group) {
        WorldGroup worldGroup = new WorldGroup(group);
        if (configFile.get(group + "." + DEFAULT_TEAM) != null) {
            Team team = pl.DEFAULT_PLUGIN_SCOREBOARD.getTeam(group);
            if(team == null){
                team = pl.DEFAULT_PLUGIN_SCOREBOARD.registerNewTeam(group);
            }

            ChatColor chatColor = ChatColor.valueOf(configFile.getString(group + "." + DEFAULT_TEAM + "." + TEAM_COLOR));
//            pl.getLogger().info(chatColor.getClass() + "");
//            pl.getLogger().info(chatColor + "");
            team.setColor(chatColor);//TODO bug color

            team.setPrefix(chatColor + configFile.getString(group + "." + DEFAULT_TEAM + "." + TEAM_PREFIX, ""));
            team.setSuffix("" + ChatColor.RESET);
            worldGroup.setTeam(team);
        }

        worldGroup.setScoreboard(configFile.getBoolean(group + "." + SCOREBOARD));
        worldGroup.setDeadCount(configFile.getBoolean(group + "." + DEAD_COUNT));
        worldGroup.setGameMode(configFile.getString(group + "." + GAME_MODE));

        List<String> serverWorlds = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());

        configFile.getStringList(group + "." + WORLDS).forEach(s -> {
            if (!serverWorlds.contains(s)) {
                //TODO to special class
                WorldCreator wc = new WorldCreator(s);
                wc.type(WorldType.FLAT);
                Bukkit.getServer().createWorld(wc);
            }
            Team team = null;
            if (configFile.get(group + "." + TEAM + "." + s) != null) {
                team = pl.DEFAULT_PLUGIN_SCOREBOARD.getTeam(s);
                if(team == null){
                    team = pl.DEFAULT_PLUGIN_SCOREBOARD.registerNewTeam(s);
                }


                ChatColor chatColor = ChatColor.valueOf(configFile.getString(group + "." + TEAM + "." + s + "." + TEAM_COLOR));

                team.setColor(chatColor);
                team.setPrefix(chatColor + configFile.getString(group + "." + TEAM + "." + s + "." + TEAM_PREFIX, ""));
                team.setSuffix("" + ChatColor.RESET);
            }
            worldGroup.setWorldTeam(s, team);
        });
    }

    public void rmGroup(String targetWorldGroup) {
        Save.setSectionVal(configFile, targetWorldGroup, null);

        List<String> stringList = configFile.getStringList(GROUP);
        stringList.remove(targetWorldGroup);
        Save.setSectionVal(configFile, GROUP, stringList);

        try {
            configFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
