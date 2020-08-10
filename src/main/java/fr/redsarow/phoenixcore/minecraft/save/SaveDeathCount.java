package fr.redsarow.phoenixcore.minecraft.save;

import fr.redsarow.phoenixcore.PhoenixCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static fr.redsarow.phoenixcore.minecraft.save.Save.initFile;

/**
 * @deprecated
 * @author redsarow
 * @since 1.0
 */
public class SaveDeathCount {

    private final static String FILE = "PlayerDeathCount.yml";

    private File dataFolder;
    private File file;
    private PhoenixCore pl;
    private YamlConfiguration configFile;

    public SaveDeathCount(PhoenixCore phoenixCore) throws IOException {
        this.pl = phoenixCore;
        this.pl.getLogger().info("save " + FILE);

        this.dataFolder = this.pl.getDataFolder();

        initFile(pl, dataFolder, FILE);

        file = new File(this.dataFolder, FILE);
        configFile = YamlConfiguration.loadConfiguration(file);
    }

    public void initPlayer(OfflinePlayer player){
        String playerId = player.getUniqueId().toString();
        Save.setSectionVal(configFile, playerId, 0);

        try {
            configFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        configFile = YamlConfiguration.loadConfiguration(file);
    }

    public int increment(Player player){
        String playerId = player.getUniqueId().toString();
        int deadCount = configFile.getInt(playerId);
        Save.setSectionVal(configFile, playerId, deadCount+1);
        try {
            configFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        configFile = YamlConfiguration.loadConfiguration(file);

        return deadCount+1;
    }

    public Map<String, Integer> getAll() {
        Set<String> keys = configFile.getKeys(false);
        return keys.stream().collect(() -> new HashMap<String, Integer>()
                ,(stringIntegerHashMap, s) -> stringIntegerHashMap.put(s, configFile.getInt(s))
                , HashMap::putAll);
    }
}
