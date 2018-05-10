package fr.redsarow.phoenixcore.minecraft.save;

import fr.redsarow.phoenixcore.minecraft.WorldGroup;
import fr.redsarow.phoenixcore.minecraft.PhoenixCore;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
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
 *   - noGgrou
 *   - G1
 *   - G2
 * noGgrou:
 *   Worlds:
 *     - W1
 *     - W2
 *   DefaultTeam:
 *     color:
 *     prefix:
 * G1:
 *   Worlds:
 *     - W1
 *     - W2
 *   Team:
 *     W1:
 *       color:
 *       prefix:
 * G2:
 *   Worlds:
 *     - W1
 *     - W2
 * }
 * </pre>
 * for color see {@link org.bukkit.ChatColor}
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

    private File dataFolder;
    private File file;
    private Plugin pl;
    private YamlConfiguration configFile;

    public SaveWorlds(JavaPlugin pl) throws IOException {

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
            Team team = PhoenixCore.TEAM_SCOREBOARD.registerNewTeam(group);
            team.setColor(ChatColor.valueOf(configFile.getString(group + "." + DEFAULT_TEAM + "." + TEAM_COLOR)));
            team.setPrefix(team.getColor() + configFile.getString(group + "." + DEFAULT_TEAM + "." + TEAM_PREFIX));
            team.setSuffix("" + ChatColor.RESET);
            worldGroup.setTeam(team);
        }

        List<String> serverWorlds = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());

        configFile.getStringList(group + "." + WORLDS).forEach(s -> {
            if(!serverWorlds.contains(s)){
                //TODO to special class
                WorldCreator wc = new WorldCreator(s);
                wc.type(WorldType.NORMAL);
                Bukkit.getServer().createWorld(wc);
            }
            Team team=null;
            if (configFile.get(group + "." + TEAM + "." + s) != null) {
                team = PhoenixCore.TEAM_SCOREBOARD.registerNewTeam(s);
                team.setColor(ChatColor.valueOf(configFile.getString(group + "." + TEAM + "." + s + "." + TEAM_COLOR)));
                team.setPrefix(team.getColor() + configFile.getString(group + "." + TEAM + "." + s + "." + TEAM_PREFIX));
                team.setSuffix("" + ChatColor.RESET);
            }
            worldGroup.setWorldTeam(s, team);
        });
    }

}
