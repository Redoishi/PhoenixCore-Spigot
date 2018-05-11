package fr.redsarow.phoenixcore.minecraft.save.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author redsarow
 * @since 1.0
 */
public class GetConfig {

    JavaPlugin plugin;
    FileConfiguration conf;

    public GetConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.conf = this.plugin.getConfig();
    }

    public Object getVal(String path){
        return conf.get(path);
    }

    public String getStringVal(String path){
        return conf.getString(path);
    }

    public Boolean getBoolVal(String path){
        return conf.getBoolean(path);
    }
}
