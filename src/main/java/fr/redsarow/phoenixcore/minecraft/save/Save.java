package fr.redsarow.phoenixcore.minecraft.save;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * @author redsarow
 * @since 1.0
 */
public abstract class Save {

    protected static void initFile(JavaPlugin pl, File dataFolder, String fileName) throws IOException {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
            pl.getLogger().info("creation dossier: "+dataFolder);
        }

        File file = new File(dataFolder, fileName);
        if (!file.exists()) {
            file.createNewFile();
            pl.getLogger().info("creation fichier: "+fileName);
        }
    }

    protected static void setSection(YamlConfiguration configFile, String path) {
        if (!configFile.contains(path)) {
            configFile.createSection(path);
        }
    }

    protected static void setSectionVal(YamlConfiguration configFile, String path, Object val) {
        setSection(configFile, path);
        configFile.set(path, val);
    }
}
