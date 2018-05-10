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

import static fr.redsarow.phoenixcore.minecraft.save.Save.*;

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
        setSectionVal(configFile, pathLastWorldGroup + "." + X, player.getLocation().getBlockX());
        setSectionVal(configFile, pathLastWorldGroup + "." + Y, player.getLocation().getBlockY());
        setSectionVal(configFile, pathLastWorldGroup + "." + Z, player.getLocation().getBlockZ());

        //save inventory
        ItemStack[] playerInventoryContents = player.getInventory().getContents();
        setSectionVal(configFile, pathLastWorldGroup + "." + INVENTORY, SaveItemStackUtils.toString(playerInventoryContents));

        //save life
        setSectionVal(configFile, pathLastWorldGroup + "." + LIFE, player.getHealth());

        //save food
        setSectionVal(configFile, pathLastWorldGroup + "." + FOOD, player.getFoodLevel());

        //save XP
        setSectionVal(configFile, pathLastWorldGroup + "." + XP, player.getExp()+player.getLevel());

        configFile.save(file);
        configFile = YamlConfiguration.loadConfiguration(file);
    }

    public Location getLastWorldLocation(Player player, String worldGroupName) {
        String pathConfig = getDefaultPathConfig(player, worldGroupName);

        String worldName = configFile.getString(pathConfig + "." + WORLD);
        int x = configFile.getInt(pathConfig + "." + X);
        int y = configFile.getInt(pathConfig + "." + Y);
        int z = configFile.getInt(pathConfig + "." + Z);

        return new Location(pl.getServer().getWorld(worldName), x, y, z);
    }

    public ItemStack[] getInventoriContent(Player player, String worldGroupName) {
        String pathConfig = getDefaultPathConfig(player, worldGroupName);

        String stringItemStack = configFile.getString(pathConfig + "." + INVENTORY);
        try {
            return SaveItemStackUtils.toObject(stringItemStack);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getHealth(Player player, String worldGroupName){
        String pathConfig = getDefaultPathConfig(player, worldGroupName);
        return configFile.getDouble(pathConfig + "." + LIFE);
    }

    public int getFood(Player player, String worldGroupName){
        String pathConfig = getDefaultPathConfig(player, worldGroupName);
        return configFile.getInt(pathConfig + "." + FOOD);
    }

    public double getXp(Player player, String worldGroupName){
        String pathConfig = getDefaultPathConfig(player, worldGroupName);
        return configFile.getDouble(pathConfig + "." + XP);
    }

    private String getDefaultPathConfig(Player player,String worldGroupName){
        WorldGroup worldGroup = WorldGroup.findWorldGroupByName(worldGroupName);
        //TODO test worldGroup == null ?
        return player.getUniqueId().toString() + "." + worldGroup.getName();
    }

    private void addPlayer(YamlConfiguration configFile, Player player) {
        String pathConfig = player.getUniqueId().toString();

        setSection(configFile, pathConfig);

        setSectionVal(configFile, pathConfig + ".name", player.getName());
    }
}
