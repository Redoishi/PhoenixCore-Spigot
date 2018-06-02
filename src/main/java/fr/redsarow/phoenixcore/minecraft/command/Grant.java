package fr.redsarow.phoenixcore.minecraft.command;

import fr.redsarow.phoenixcore.PhoenixCore;
import fr.redsarow.phoenixcore.minecraft.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static fr.redsarow.phoenixcore.PhoenixCore.waitGranted;

/**
 * @author redsarow
 * @since 1.0.0
 */
public class Grant extends AMyCommand<PhoenixCore> {

    public Grant(PhoenixCore phoenixCore, CommandMap commandMap) {
        super(phoenixCore, "grant");
        addDescription("");
        addUsage("/grant <PlayerName>");
        addAliases("g");

        registerCommand(commandMap);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((args.length<1)) {
            return false;
        }
        if(sender instanceof Player){
            Player p  = (Player) sender;
            if (waitGranted.containsKey(p.getName())) {
                p.sendMessage("Desoler mais vous ne pouvez pas!");
                return true;
            }
        }

        try {
            getPlugin().addGrant(sender.getName(), args[0]);
        }catch (Exception e){
            sender.sendMessage(Color.ERROR+" error player");
        }

        return true;
    }
}
