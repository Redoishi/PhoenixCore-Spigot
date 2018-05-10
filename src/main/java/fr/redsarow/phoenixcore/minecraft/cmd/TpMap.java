package fr.redsarow.phoenixcore.minecraft.cmd;

import fr.redsarow.phoenixcore.minecraft.PhoenixCore;
import fr.redsarow.phoenixcore.minecraft.WorldGroup;
import fr.redsarow.phoenixcore.minecraft.save.SavePlayerWorldParam;
import fr.redsarow.phoenixcore.minecraft.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * @author redsarow
 * @since 1.0
 */
public class TpMap extends AMyCommand {

    private SavePlayerWorldParam playerWorldParam;

    public TpMap(PhoenixCore phoenixCore, CommandMap commandMap, SavePlayerWorldParam playerWorldParam) {
        super(phoenixCore, "tpMap");
        this.playerWorldParam = playerWorldParam;
        addDescription("tp other map");
        addUsage("/tpMap <WorldGroup>");
        String tabComplete[] = WorldGroup.getListNameGroups().toArray(new String[0]);

        addListTabbComplete(0, tabComplete);
        registerCommand(commandMap);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.ERROR + "not Player");
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

            player.sendMessage(Color.ERROR + "Erreur. "
                    + ChatColor.RESET + "Le choix "
                    + Color.INFO + targetWorldGroup
                    + ChatColor.RESET + "est inconnue.\n"
                    + "Les Choix valide: "
                    + Color.INFO + builder.toString());

            return true;
        }

        try {
            playerWorldParam.setLastWorldInformations(player);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(Color.ERROR + "Une erreur est survenue contacter Redsarow");
            return true;
        }

        setPlayerParam(player, targetWorldGroup);

        Location targetWorldLocation =
                playerWorldParam.getLastWorldLocation(
                        player,
                        targetWorldGroup
                );
        player.sendMessage("Tp sur " + Color.INFO + targetWorldGroup + ChatColor.RESET);
        player.teleport(targetWorldLocation);

        //TODO gm
//        if (DEFAULT_WORLD.contains(targetWorld) && player.getGameMode() != GameMode.SPECTATOR) {
//            player.setGameMode(GameMode.SURVIVAL);
//        } else if (player.getGameMode() != GameMode.SPECTATOR) {
//            player.setGameMode(GameMode.CREATIVE);
//        }

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
    }

}