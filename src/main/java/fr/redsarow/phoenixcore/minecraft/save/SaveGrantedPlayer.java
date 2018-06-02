package fr.redsarow.phoenixcore.minecraft.save;

import fr.redsarow.phoenixcore.PhoenixCore;
import fr.redsarow.phoenixcore.minecraft.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static fr.redsarow.phoenixcore.minecraft.save.Save.initFile;
import static fr.redsarow.phoenixcore.minecraft.save.Save.setSectionVal;

/**
 * @author redsarow
 * @since 1.0.0
 */
public class SaveGrantedPlayer {

    private final static String FILE = "GrantedPlayer.yml";

    private File dataFolder;
    private File file;
    private PhoenixCore pl;
    private SaveDeathCount playerDeathCount;
    private Objective objectiveDeath;
    private YamlConfiguration configFile;
    private List<UUID> granted;

    public SaveGrantedPlayer(PhoenixCore phoenixCore, SaveDeathCount playerDeathCount, Objective objectiveDeath) throws IOException {
        this.pl = phoenixCore;
        this.playerDeathCount = playerDeathCount;
        this.objectiveDeath = objectiveDeath;
        this.pl.getLogger().info("save " + FILE);

        this.dataFolder = this.pl.getDataFolder();

        initFile(pl, dataFolder, FILE);

        file = new File(this.dataFolder, FILE);
        configFile = YamlConfiguration.loadConfiguration(file);
        granted = configFile.getKeys(false).stream().map(UUID::fromString).collect(Collectors.toList());
    }

    public List<UUID> getGranted(){
        return granted;
    }

    public void addGranted(OfflinePlayer offlinePlayer) throws IOException {
        UUID uuid = offlinePlayer.getUniqueId();
        setSectionVal(configFile, uuid.toString(), offlinePlayer.getName());
        granted.add(uuid);
        playerDeathCount.initPlayer(offlinePlayer);
        objectiveDeath.getScore(offlinePlayer.getName()).setScore(0);

        PhoenixCore.waitGranted.remove(offlinePlayer.getName());

        Player player = offlinePlayer.getPlayer();
        if (player != null) {
            WorldGroup worldGroup = WorldGroup.findWorldGroupByWorldName(player.getWorld().getName());
            GameMode gameMode = worldGroup.getGameMode();
            if(gameMode!= null){
                player.setGameMode(gameMode);
            }else{
                player.setGameMode(GameMode.SURVIVAL);
            }
        }

        configFile.save(file);
        configFile = YamlConfiguration.loadConfiguration(file);
    }

    public boolean isGranted(UUID uuid){
        return granted.contains(uuid);
    }
}
