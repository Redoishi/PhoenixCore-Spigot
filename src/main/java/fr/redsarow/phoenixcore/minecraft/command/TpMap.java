package fr.redsarow.phoenixcore.minecraft.command;

import fr.redsarow.phoenixcore.PhoenixCore;
import fr.redsarow.phoenixcore.minecraft.WorldGroup;
import fr.redsarow.phoenixcore.minecraft.save.SavePlayerInformation;
import fr.redsarow.phoenixcore.minecraft.save.SavePlayerWorldParam;
import fr.redsarow.phoenixcore.minecraft.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * @author redsarow
 * @since 1.0
 */
public class TpMap extends AMyCommand<PhoenixCore> {

    private final SavePlayerWorldParam playerWorldParam;
    private final SavePlayerInformation savePlayerInformation;

    public TpMap(PhoenixCore phoenixCore, SavePlayerWorldParam playerWorldParam, SavePlayerInformation savePlayerInformation) {
        super(phoenixCore, "tpMap");
        this.playerWorldParam = playerWorldParam;
        this.savePlayerInformation = savePlayerInformation;
        setDescription("tp other map");
        setUsage("/tpMap <WorldGroup>");
        String[] tabComplete = WorldGroup.getListNameGroups().toArray(new String[0]);

        addTabbComplete(0, tabComplete);
        registerCommand();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.ERROR + PhoenixCore.getI18n().get("cmd.tpMap.error.noPlayer"));
            return true;
        }
        if (args.length < 1) {
            return false;
        }
        Player player = (Player) sender;
        String targetWorldGroup = args[0];

        //if targetWorldGroup unknown
        if (!WorldGroup.getListNameGroups().contains(targetWorldGroup)) {
            StringBuilder builder = new StringBuilder("[");
            WorldGroup.getListNameGroups().forEach(s -> {
                if (!builder.toString().equalsIgnoreCase("[")) {
                    builder.append(",");
                }
                builder.append(s);
            });
            builder.append("]");

            player.sendMessage(Color.ERROR + "Error. "
                    + ChatColor.RESET +
                    PhoenixCore.getI18n().get("cmd.error.choices"
                            ,Color.INFO + targetWorldGroup+ ChatColor.RESET
                            ,Color.INFO + builder.toString())
            );

            return true;
        }

        try {
            playerWorldParam.setLastWorldInformations(player);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(Color.ERROR + PhoenixCore.getI18n().get("error"));
            return true;
        }

        Location targetWorldLocation =
                playerWorldParam.getLastWorldLocation(
                        player,
                        targetWorldGroup
                );
        player.sendMessage(PhoenixCore.getI18n().get("cmd.tpMap.tp"
                , Color.INFO + targetWorldGroup + ChatColor.RESET));
        //TODO 1.13
        player.saveData();

        //TODO active for advancement in 1.13
        try {
            savePlayerInformation.save(player.getUniqueId(),
                    WorldGroup.findWorldGroupByWorldName(player.getWorld().getName()).getName(),
                    targetWorldGroup);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(Color.ERROR + PhoenixCore.getI18n().get("error"));
            return true;
        }

        player.teleport(targetWorldLocation);
//        player.loadData();
//        player.updateInventory();

        setPlayerParam(player, targetWorldGroup);
        if(player.getGameMode() != GameMode.SPECTATOR){
            WorldGroup worldGroup = WorldGroup.findWorldGroupByName(targetWorldGroup);
            GameMode gameMode = worldGroup.getGameMode();
            if(gameMode!= null){
                player.setGameMode(gameMode);
            }
        }else{
            player.setGameMode(GameMode.SPECTATOR);
        }

//        player.saveData();
        return true;
    }


    private void setPlayerParam(Player player, String targetWorldGroup) {
        player.getInventory().clear();
        player.getInventory().setContents(playerWorldParam.getInventoriContent(player, targetWorldGroup));
        player.setHealth(playerWorldParam.getHealth(player, targetWorldGroup));
        player.setFoodLevel(playerWorldParam.getFood(player, targetWorldGroup));
        double RealLvl = playerWorldParam.getXp(player, targetWorldGroup);
        float xp = (float) (RealLvl % 1);
        int lvl = (int) RealLvl;
        player.setLevel(lvl);
        player.setExp(xp);

        Location BedSpawnLocation = playerWorldParam.getLastBedSpawnLocation(player, targetWorldGroup);

        getPlugin().getLogger().info("BedSpawnLocation == null : "+ (BedSpawnLocation==null));
        if(BedSpawnLocation!=null){
            getPlugin().getLogger().info(BedSpawnLocation.toString());
        }
        player.setBedSpawnLocation(BedSpawnLocation);//TODO bug

        if(player.getGameMode() != GameMode.SPECTATOR){
            WorldGroup worldGroup = WorldGroup.findWorldGroupByName(targetWorldGroup);
            GameMode gameMode = worldGroup.getGameMode();
            if(gameMode!= null){
                player.setGameMode(gameMode);
            }
        }
    }

}
