package fr.redsarow.phoenixcore.discord.command;

import sx.blah.discord.handle.obj.IMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author redsarow
 * @since 1.0
 */
public class CommandManagement {

    private static Map<String, ACommand> commands = new HashMap<>();
    private static Map<String, String> aliases = new HashMap<>();

    public static void registerCommand(ACommand command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    public static void registerAlias(String command, String... alias) {
        if (alias != null && alias.length > 0) {
            for (String s : alias) {
                if (!command.equalsIgnoreCase(s))
                    aliases.put(s, command);
            }
        }
    }

    public static Collection<ACommand> getAllCommands(){
        return commands.values();
    }

    public static ACommand getCommand(String arg) {
        if (aliases.containsKey(arg))
            return getCommand(aliases.get(arg));
        return commands.get(arg.toLowerCase());
    }

    public static void run(IMessage message) {
        message.getChannel().setTypingStatus(true);
        if (message.getAuthor().isBot())
            return;

        String[] msgContent = message.getContent().split(" ");
        if (msgContent[0].length() == 1) {
            return;
        }
        ACommand command = getCommand(msgContent[0].substring(1));
        if (command != null) {
            try {
                command.run(message);
            } catch (Exception e) {
                message.getChannel().sendMessage("L'exécution de la commande: " + command.getName() + " a subit une erreur");
                e.printStackTrace();
            }
        }else{
            message.getChannel().sendMessage("La commande : " + msgContent[0] + "est inconnue!");
        }
        message.getChannel().setTypingStatus(false);
    }

}
