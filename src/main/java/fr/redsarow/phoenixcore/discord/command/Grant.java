package fr.redsarow.phoenixcore.discord.command;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import fr.redsarow.phoenixcore.discord.Bot;

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
    public boolean run(Message message) {
        String[] msgContent = message.getContent().split(" ");
        if(msgContent.length < 2){
            return false;
        }
        Member mbr = message.getAuthorAsMember().block();

        boolean userOk = mbr.getRoles().any(iRole -> ROLES.contains(iRole.getName())).block();
        if(!userOk){
            message.getChannel().block().createMessage(":x: Non autoriser");
            return true;
        }
        bot.getPlugin().addGrant(mbr.getMention(), msgContent[1]);

        return true;
    }
}
