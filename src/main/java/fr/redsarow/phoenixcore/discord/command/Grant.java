package fr.redsarow.phoenixcore.discord.command;

import fr.redsarow.phoenixcore.discord.Bot;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import static fr.redsarow.phoenixcore.discord.Bot.PREFIX;
import static fr.redsarow.phoenixcore.discord.Bot.ROLES;

/**
 * @author redsarow
 * @since 1.0
 */
public class Grant extends ACommand {

    private final Bot bot;

    public Grant(Bot bot) {
        super("Grant", "Commande Grant", PREFIX + "grant", null, "g");
        this.bot = bot;
    }

    @Override
    public boolean run(IMessage message) {
        String[] msgContent = message.getContent().split(" ");
        if(msgContent.length<2){
            return false;
        }
        IUser author = message.getAuthor();
        boolean userOk = author.getRolesForGuild(message.getGuild()).stream()
                .anyMatch(iRole -> ROLES.contains(iRole.getName()));
        if(!userOk){
            message.getChannel().sendMessage(":x: Non autoriser");
            return true;
        }
        bot.getPlugin().addGrant(author.mention(), msgContent[1]);

        return true;
    }
}
