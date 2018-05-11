package fr.redsarow.phoenixcore.discord.listener;

import fr.redsarow.phoenixcore.discord.Bot;
import fr.redsarow.phoenixcore.discord.command.CommandManagement;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * @author redsarow
 * @since 1.0
 */
public class TryCommand {

    @EventSubscriber
    public void onMsg(MessageReceivedEvent event){
        IMessage message = event.getMessage();
        if(message.getContent().startsWith(Bot.PREFIX)){
            CommandManagement.run(message);
        }
    }
}
