package fr.redsarow.phoenixcore.minecraft.command;

import fr.redsarow.phoenixcore.PhoenixCore;
import fr.redsarow.phoenixcore.minecraft.WorldGroup;
import fr.redsarow.phoenixcore.minecraft.save.SavePlayerWorldParam;
import fr.redsarow.phoenixcore.minecraft.save.SaveWorlds;
import fr.redsarow.phoenixcore.minecraft.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author redsarow
 * @since 1.0
 */
public class RmGroup extends AMyCommand<PhoenixCore> {

    private final SaveWorlds saveWorlds;
    private final SavePlayerWorldParam playerWorldParam;

    public RmGroup(PhoenixCore phoenixCore, SaveWorlds saveWorlds, SavePlayerWorldParam playerWorldParam) {
        super(phoenixCore, "RmGroup");
        this.saveWorlds = saveWorlds;
        this.playerWorldParam = playerWorldParam;
        setDescription("sup group");
        setUsage("/RmGroup <WorldGroup>");
        setAliases("rg");

        String[] tabComplete = WorldGroup.getListNameGroups().toArray(new String[0]);
        addTabbComplete(0, tabComplete);

        registerCommand();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        if (sender instanceof Player && !sender.isOp()) {
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
            sender.sendMessage(Color.ERROR + "Error. "
                    + ChatColor.RESET +
                    PhoenixCore.getI18n().get("cmd.error.choices"
                            ,Color.INFO + targetWorldGroup+ ChatColor.RESET
                            ,Color.INFO + builder.toString())
            );

            return true;
        }

        saveWorlds.rmGroup(targetWorldGroup);
        playerWorldParam.rmGroup(targetWorldGroup);

        return true;
    }
}
