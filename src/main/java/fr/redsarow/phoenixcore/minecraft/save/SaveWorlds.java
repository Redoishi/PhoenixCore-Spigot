package fr.redsarow.phoenixcore.minecraft.save;

import fr.redsarow.phoenixcore.minecraft.Group;
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
 * <p>
 * Group:<br>
 * - noGgrou<br>
 * - G1<br>
 * - G2<br>
 * noGgrou:<br>
 * Worlds:<br>
 * - W1<br>
 * - W2<br>
 * DefaultTeam:<br>
 * color:<br>
 * prefix:<br>
 * G1:<br>
 * Worlds:<br>
 * - W1<br>
 * - W2<br>
 * Team:<br>
 * W1:<br>
 * color:<br>
 * prefix:<br>
 * G2:<br>
 * Worlds:<br>
 * - W1<br>
 * - W2<br>
 * </p>
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
                add("noGroup");
            }});
            setSectionVal(
                    configFile,
                    "noGroup." + WORLDS,
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

    public void loadWorldofGroupe(String groupe) {
        Group group = new Group(groupe);
        if (configFile.get(groupe + "." + DEFAULT_TEAM) != null) {
            Team team = PhoenixCore.TEAM_SCOREBOARD.registerNewTeam(groupe);
            team.setColor(ChatColor.valueOf(configFile.getString(groupe + "." + DEFAULT_TEAM + "." + TEAM_COLOR)));
            team.setPrefix(team.getColor() + configFile.getString(groupe + "." + DEFAULT_TEAM + "." + TEAM_PREFIX));
            team.setSuffix("" + ChatColor.RESET);
            group.setTeam(team);
        }

        List<String> serverWorlds = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());

        configFile.getStringList(groupe + "." + WORLDS).forEach(s -> {
            if(!serverWorlds.contains(s)){
                //TODO to special class
                WorldCreator wc = new WorldCreator(s);
                wc.type(WorldType.NORMAL);
                Bukkit.getServer().createWorld(wc);
            }
            if (configFile.get(groupe + "." + TEAM + "." + s) != null) {
                Team team = PhoenixCore.TEAM_SCOREBOARD.registerNewTeam(s);
                team.setColor(ChatColor.valueOf(configFile.getString(groupe + "." + TEAM + "." + s + "." + TEAM_COLOR)));
                team.setPrefix(team.getColor() + configFile.getString(groupe + "." + TEAM + "." + s + "." + TEAM_PREFIX));
                team.setSuffix("" + ChatColor.RESET);
                group.setWorldTeam(s, team);
            }
        });
    }

}
