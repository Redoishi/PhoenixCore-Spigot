package fr.redsarow.phoenixcore.minecraft.command;

import fr.redsarow.phoenixcore.PhoenixCore;
import fr.redsarow.phoenixcore.minecraft.WorldGroup;
import fr.redsarow.phoenixcore.minecraft.save.SavePlayerWorldParam;
import fr.redsarow.phoenixcore.minecraft.save.SaveWorlds;
import fr.redsarow.phoenixcore.minecraft.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author redsarow
 * @since 1.0
 */
public class RmGroup extends AMyCommand {

    private SaveWorlds saveWorlds;
    private SavePlayerWorldParam playerWorldParam;

    public RmGroup(PhoenixCore phoenixCore, CommandMap commandMap, SaveWorlds saveWorlds, SavePlayerWorldParam playerWorldParam) {
        super(phoenixCore, "RmGroup");
        this.saveWorlds = saveWorlds;
        this.playerWorldParam = playerWorldParam;
        addDescription("sup group");
        addUsage("/RmGroup <WorldGroup>");
        addAliases("rg");

        String tabComplete[] = WorldGroup.getListNameGroups().toArray(new String[0]);
        addListTabbComplete(0, tabComplete);

        registerCommand(commandMap);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        if (sender instanceof Player && !((Player) sender).isOp()) {
            return false;
        }
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

            sender.sendMessage(Color.ERROR + "Erreur. "
                    + ChatColor.RESET + "Le choix "
                    + Color.INFO + targetWorldGroup
                    + ChatColor.RESET + "est inconnue.\n"
                    + "Les Choix valide: "
                    + Color.INFO + builder.toString());

            return true;
        }

        saveWorlds.rmGroup(targetWorldGroup);
        playerWorldParam.rmGroup(targetWorldGroup);

        return true;
    }
}
