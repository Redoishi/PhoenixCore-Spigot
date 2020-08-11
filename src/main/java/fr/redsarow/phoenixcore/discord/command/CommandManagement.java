package fr.redsarow.phoenixcore.discord.command;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author redsarow
 * @since 1.0
 */
public class CommandManagement {

    private static final Map<String, ACommand> commands = new HashMap<>();
    private static final Map<String, String> aliases = new HashMap<>();

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

    public static Mono<Void> run(Message message) {
        MessageChannel messageChannel = message.getChannel().block();
        assert messageChannel != null;

//        messageChannel.setTypingStatus(true);

        String[] msgContent = message.getContent().split(" ");
        if (msgContent[0].length() == 1) {// si msg == PREFIX et pas de commande
            return Mono.empty().then();
        }

        ACommand command = getCommand(msgContent[0].substring(1));
        if (command != null) {
            try {
                boolean run = command.run(message);
                if(!run){
                    messageChannel.createMessage(":x: " + command.getUsage()).block();
                }
            } catch (Exception e) {
                messageChannel.createMessage("L'exécution de la commande: " + command.getName() + " a subit une erreur").block();
                e.printStackTrace();
            }
        }else{
            messageChannel.createMessage("La commande : " + msgContent[0] + " est inconnue!").block();
        }
//        messageChannel.setTypingStatus(false);

        return Mono.empty().then();
    }

}
