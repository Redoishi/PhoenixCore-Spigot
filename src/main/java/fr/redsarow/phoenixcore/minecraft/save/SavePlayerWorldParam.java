package fr.redsarow.phoenixcore.minecraft.save;

import fr.redsarow.phoenixcore.minecraft.WorldGroup;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static fr.redsarow.phoenixcore.minecraft.save.Save.*;
import static fr.redsarow.phoenixcore.minecraft.save.Save.setSectionVal;

/**
 * @author redsarow
 * @since 1.0
 */
public class SavePlayerWorldParam {

    private static String FILE = "PlayerWorldParam.yml";

    private final static String WORLD = "world";
    private final static String X = "x";
    private final static String Y = "y";
    private final static String Z = "z";
    private final static String INVENTORY = "inventory";
    private final static String LIFE = "life";
    private final static String FOOD = "food";
    private final static String XP = "xp";
    private final static String SPAWN = "spawn";

    private File dataFolder;
    private Plugin pl;
    private File file;
    private YamlConfiguration configFile;


    public SavePlayerWorldParam(JavaPlugin pl) throws IOException {

        this.pl = pl;
        this.pl.getLogger().info("save " + FILE);

        this.dataFolder = this.pl.getDataFolder();

        initFile(pl, dataFolder, FILE);

        file = new File(this.dataFolder, FILE);
        configFile = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * try by cmd for tp
     *
     * @param player
     *
     * @throws IOException
     */
    public void setLastWorldInformations(Player player) throws IOException {
        String pathConfig = player.getUniqueId().toString();

        if (!configFile.contains(pathConfig)) {
            addPlayer(configFile, player);
        }

        World world = player.getWorld();
        WorldGroup worldGroup = WorldGroup.findWorldGroupByWorldName(world.getName());
        //TODO test worldGroup == null ?

        String pathLastWorldGroup = pathConfig + "." + worldGroup.getName();

        setSection(configFile, pathLastWorldGroup);
        //set las world for this world group
        setSectionVal(configFile, pathLastWorldGroup + "." + WORLD, world.getName());
        setSectionVal(configFile, pathLastWorldGroup + "." + X, player.getLocation().getX());
        setSectionVal(configFile, pathLastWorldGroup + "." + Y, player.getLocation().getY());
        setSectionVal(configFile, pathLastWorldGroup + "." + Z, player.getLocation().getZ());

        //save inventory
        ItemStack[] playerInventoryContents = player.getInventory().getContents();
        setSectionVal(configFile, pathLastWorldGroup + "." + INVENTORY, UtilsSaveItemStack.toString(playerInventoryContents));

        //save life
        setSectionVal(configFile, pathLastWorldGroup + "." + LIFE, player.getHealth());

        //save food
        setSectionVal(configFile, pathLastWorldGroup + "." + FOOD, player.getFoodLevel());

        //save XP
        setSectionVal(configFile, pathLastWorldGroup + "." + XP, player.getExp() + player.getLevel());

        //save spawn
        Location bedSpawnLocation = player.getBedSpawnLocation();
        pl.getLogger().info("Save bedSpawnLocation == null : "+ (bedSpawnLocation==null));
        if(bedSpawnLocation!=null){
            pl.getLogger().info("Save "+bedSpawnLocation.toString());
        }
        if (bedSpawnLocation != null) {
            setSection(configFile, pathLastWorldGroup + "." + SPAWN);
            setSectionVal(configFile, pathLastWorldGroup + "." + SPAWN + "." + WORLD, bedSpawnLocation.getWorld().getName());
            setSectionVal(configFile, pathLastWorldGroup + "." + SPAWN + "." + X, bedSpawnLocation.getX());
            setSectionVal(configFile, pathLastWorldGroup + "." + SPAWN + "." + Y, bedSpawnLocation.getY());
            setSectionVal(configFile, pathLastWorldGroup + "." + SPAWN + "." + Z, bedSpawnLocation.getZ());
        }

        configFile.save(file);
        configFile = YamlConfiguration.loadConfiguration(file);
    }

    public Location getLastWorldLocation(Player player, String worldGroupName) {
        String pathConfig = getDefaultPathConfig(player, worldGroupName);

        String targetWorld = configFile.getString(pathConfig + "." + WORLD);
        if (targetWorld == null) {
            WorldGroup worldGroup = WorldGroup.findWorldGroupByName(worldGroupName);
            targetWorld = worldGroup.getWorlds().iterator().next();
        }
        String worldName = targetWorld;
        double x = configFile.getDouble(pathConfig + "." + X);
        double y = configFile.getDouble(pathConfig + "." + Y);
        double z = configFile.getDouble(pathConfig + "." + Z);

        return new Location(pl.getServer().getWorld(worldName), x, y, z);
    }

    public ItemStack[] getInventoriContent(Player player, String worldGroupName) {
        String pathConfig = getDefaultPathConfig(player, worldGroupName);

        String stringItemStack = configFile.getString(pathConfig + "." + INVENTORY);
        try {
            return UtilsSaveItemStack.toObject(stringItemStack);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getHealth(Player player, String worldGroupName) {
        String pathConfig = getDefaultPathConfig(player, worldGroupName);
        return configFile.getDouble(pathConfig + "." + LIFE, 20);
    }

    public int getFood(Player player, String worldGroupName) {
        String pathConfig = getDefaultPathConfig(player, worldGroupName);
        return configFile.getInt(pathConfig + "." + FOOD, 20);
    }

    public double getXp(Player player, String worldGroupName) {
        String pathConfig = getDefaultPathConfig(player, worldGroupName);
        return configFile.getDouble(pathConfig + "." + XP, 0);
    }

    public Location getLastBedSpawnLocation(Player player, String worldGroupName) {
        String pathConfig = getDefaultPathConfig(player, worldGroupName);

        if(configFile.getString(pathConfig + "." + SPAWN)==null){
            return null;
        }

        String worldName = configFile.getString(pathConfig + "." + SPAWN + "." + WORLD);
        double x = configFile.getDouble(pathConfig + "." + SPAWN + "." + X);
        double y = configFile.getDouble(pathConfig + "." + SPAWN + "." + Y);
        double z = configFile.getDouble(pathConfig + "." + SPAWN + "." + Z);

        return new Location(pl.getServer().getWorld(worldName), x, y, z);
    }

    private String getDefaultPathConfig(Player player, String worldGroupName) {
        WorldGroup worldGroup = WorldGroup.findWorldGroupByName(worldGroupName);
        //TODO test worldGroup == null ?
        return player.getUniqueId().toString() + "." + worldGroup.getName();
    }

    private void addPlayer(YamlConfiguration configFile, Player player) {
        String pathConfig = player.getUniqueId().toString();

        setSection(configFile, pathConfig);

        setSectionVal(configFile, pathConfig + ".name", player.getName());
    }

    public void rmGroup(String targetWorldGroup) {
        Set<String> player = configFile.getKeys(false);
        player.forEach(s -> {
            setSectionVal(configFile, s+"."+targetWorldGroup, null);
        });
        try {
            configFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
