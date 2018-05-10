package fr.redsarow.phoenixcore.minecraft.save.config;

import fr.redsarow.phoenixcore.minecraft.PhoenixCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * @author redsarow
 * @since 0.1
 */
public class Config {

    private static File config;
    private static JavaPlugin plugin;
    private static FileConfiguration fileConfiguration;

    private static Map<String, FileConfiguration> listeFileConfiguration = new HashMap<>();


    public static boolean checkConfig(JavaPlugin plugin, String name, String path) throws Exception {
        Config.plugin = plugin;
        return dataFolderExists() && configFileExists(name, path);
    }

    private static boolean dataFolderExists() {
        if (!plugin.getDataFolder().exists()) {
            return plugin.getDataFolder().mkdirs();
        }
        return true;
    }

    private static boolean configFileExists(String name, String path) throws Exception {
        config = new File(path.equals("")
                ?plugin.getDataFolder().toString()
                :plugin.getDataFolder()+File.separator+path, name);
        if (!config.exists()) {
            plugin.getLogger().info(name+" not found");
            plugin.saveResource(path.equals("")?name:path+File.separator+name, true);
        } else {
            plugin.getLogger().info(name +" found");
            if (!checkConfigVers(name)) {
                throw new Exception();
            }
        }

        if(fileConfiguration == null){
            fileConfiguration = plugin.getConfig();
        }
        fileConfiguration.load(config);
        listeFileConfiguration.put(name.replace(".yml", ""), fileConfiguration);
        return true;
    }


    private static boolean checkConfigVers(String name) {

        try {
            //rename config.yml
            fileConfiguration = plugin.getConfig();
            if(fileConfiguration.getDouble("version") != PhoenixCore.VERS_CONFIG){

                plugin.getLogger().info(name+" deprecated");

                //get values ancient config
//                Map<String, Object> values = fileConfiguration.getValues(true);

                //File config = new File(this.plugin.getDataFolder()+File.separator+name);
                File aConfig = new File(plugin.getDataFolder()+File.separator+name.replace(".yml", "")+fileConfiguration.get("version")+".yml");

                if(!config.renameTo(aConfig)){
                    return false;
                }
                plugin.saveDefaultConfig();
                plugin.reloadConfig();

                //fileConfiguration prent la new config
                fileConfiguration = plugin.getConfig();


                List<String> lines = Files.readAllLines(config.toPath(), StandardCharsets.UTF_8);

                Param param = new Param(new Scanner(aConfig), -1, lines);
                StringBuilder stringBuilder = new StringBuilder("");

                modif(param, null, stringBuilder);

                Files.write(config.toPath(), param.lines, StandardCharsets.UTF_8);

            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void modif(Param param, Set<String> keys, StringBuilder path){

        if(param.scanner.hasNextLine()) {
            //skip comment, version and void (ancient)
            String ligne = param.scanner.nextLine();

            param.nbLinge++;

            //if list
            if(ligne.matches("^ *-.*")){
                if(param.nbLinge<param.lines.size() && param.lines.get(param.nbLinge).matches("^ *-.*")){
                    param.lines.remove(param.nbLinge);
                    param.lines.add(param.nbLinge, ligne);
                }else{
                    //decal auto
                    param.lines.add(param.nbLinge, ligne);
                }

            }else if (!(ligne.startsWith("#") || ligne.startsWith("version") || ligne.matches("^ *$"))) {

                String[] aConfigValue = ligne.split(": |:");
                aConfigValue[0] = aConfigValue[0].replaceAll("^ *", "");

                if (aConfigValue.length == 2) {
                    if (keys == null) {
                        //si val dif alor replace
                        if (!aConfigValue[1].equalsIgnoreCase(fileConfiguration.getString(aConfigValue[0]))) {
                            param.lines.remove(param.nbLinge);
                            param.lines.add(param.nbLinge, ligne);
                        }
                    } else {
                        //si val dif alor replace
                        if (!aConfigValue[1].equalsIgnoreCase(fileConfiguration.getString(path+"."+aConfigValue[0]))) {
                            param.lines.remove(param.nbLinge);
                            param.lines.add(param.nbLinge, ligne);
                        }
                        keys.remove(aConfigValue[0]);
                        if(keys.isEmpty()){
                            return;
                        }
                    }

                } else if (aConfigValue.length == 1) {


                    String key = path.toString().equals("")
                            ? aConfigValue[0]
                            : path + "." + aConfigValue[0];


                    if (!fileConfiguration.isList(key)) {
                        Set<String> newKeys = fileConfiguration
                                .getConfigurationSection(key)
                                .getKeys(false);

                        modif(param, newKeys, new StringBuilder(key));
                    }
                }
            }
            modif(param, keys, path);
        }
        return;
    }

    public static File getConfig() {
        return config;
    }

    public static FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public static Map<String, FileConfiguration> getListeFileConfiguration() {
        return listeFileConfiguration;
    }

    private static class Param{
        Scanner scanner;
        int nbLinge;
        List<String> lines;

        Param(Scanner scanner, int nbLinge, List<String> lines) {
            this.scanner = scanner;
            this.nbLinge = nbLinge;
            this.lines = lines;
        }
    }
}