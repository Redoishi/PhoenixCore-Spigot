package fr.redsarow.phoenixcore.minecraft.save;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static fr.redsarow.phoenixcore.minecraft.save.Save.*;

/**
 * @author redsarow
 * @since 1.0
 */
public class SavePlayerWorldParam {

    private static String FILE = "PlayerWorldParam.yml";

    private List<String> SURVIE_WORLD;//TODO replace by group world

    private File dataFolder;
    private Plugin pl;


    public SavePlayerWorldParam(JavaPlugin pl) throws IOException {

        this.pl = pl;
        this.pl.getLogger().info("save " + FILE);

        this.dataFolder = this.pl.getDataFolder();

        initFile(pl, dataFolder, FILE);
    }

    public void setLastWorldLocation(Player player) throws IOException {
        File file = new File(this.dataFolder, FILE);
        YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        String pathConfig = player.getUniqueId().toString();

        if (!configFile.contains(pathConfig)) {
            addPlayer(configFile, player);
        }

        World world = player.getWorld();
        if (SURVIE_WORLD.contains(world.getName())) {
            setSectionVal(configFile, pathConfig + ".lastWorldSurvie", world.getName());
        }

        String pathLastWorld = pathConfig + "." + world.getName();
        setSection(configFile, pathLastWorld);
        setSectionVal(configFile, pathLastWorld + ".x", player.getLocation().getBlockX());
        setSectionVal(configFile, pathLastWorld + ".y", player.getLocation().getBlockY());
        setSectionVal(configFile, pathLastWorld + ".z", player.getLocation().getBlockZ());

        configFile.save(file);
    }

    public Location getLastWorldLocation(Player player, World lastWorld) {
        File file = new File(this.dataFolder, FILE);
        YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        String pathConfig = player.getUniqueId().toString();

        String pathLastWorld = pathConfig + "." + lastWorld.getName();
        int x = configFile.getInt(pathLastWorld + ".x");
        int y = configFile.getInt(pathLastWorld + ".y");
        int z = configFile.getInt(pathLastWorld + ".z");

        return new Location(lastWorld, x, y, z);
    }

    public boolean isLastWorldSurvie(Player player, String world) {
        File file = new File(this.dataFolder, FILE);
        YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        String pathConfig = player.getUniqueId().toString() + ".lastWorldSurvie";

        return configFile.getString(pathConfig).equals(world);

    }

    public String getLastWorldSurvie(Player player) {
        File file = new File(this.dataFolder, FILE);
        YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        return configFile.getString(player.getUniqueId().toString() + ".lastWorldSurvie");
    }

    private void addPlayer(YamlConfiguration configFile, Player player) {
        String pathConfig = player.getUniqueId().toString();

        setSection(configFile, pathConfig);

        setSectionVal(configFile, pathConfig + ".name", player.getName());
    }
}
