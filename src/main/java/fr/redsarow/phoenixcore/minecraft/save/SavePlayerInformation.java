package fr.redsarow.phoenixcore.minecraft.save;

import fr.redsarow.phoenixcore.PhoenixCore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


/**
 * @author
 * @since
 *///TODO change for advancement in 1.13
public class SavePlayerInformation {

    private PhoenixCore phoenixCore;
    private File dataFolder;

    public SavePlayerInformation(PhoenixCore phoenixCore) {
        this.phoenixCore = phoenixCore;
        this.dataFolder = this.phoenixCore.getDataFolder();
    }

    private File testFolder(String worldGroupeName){
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
            phoenixCore.getLogger().info("creation dossier: "+dataFolder);
        }

        File worldGroupe = new File(dataFolder, worldGroupeName);
        File worldGroupeData = new File(worldGroupe, "data");
        if (!worldGroupeData.exists()) {
            worldGroupeData.mkdirs();
            phoenixCore.getLogger().info("creation dossier: "+worldGroupeData);
        }
        return worldGroupe;
    }

    public boolean save(UUID uuid, String originWorldGroupeName, String targetWorldGroupeName) throws IOException {
        boolean dataOk = false;
        File world = new File(dataFolder.getParentFile().getParentFile(),"world");
        File data = new File(world+File.separator+"playerdata",uuid.toString()+".dat");

        File fileOriginWG = testFolder(originWorldGroupeName);
        File fileOriginWGData = new File(fileOriginWG+File.separator+"data",uuid.toString()+".dat");

        File fileTargetWG = testFolder(targetWorldGroupeName);
        File fileTargetWGData = new File(fileTargetWG+File.separator+"data",uuid.toString()+".dat");


        //data
        Files.move(data.toPath(), fileOriginWGData.toPath(), StandardCopyOption.REPLACE_EXISTING);
        if(fileTargetWGData.exists()){
            Files.copy(fileTargetWGData.toPath(), data.toPath(), StandardCopyOption.REPLACE_EXISTING);
            dataOk=true;
        }

        return dataOk;
    }
}
