package fr.redsarow.phoenixcore.discord;

import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

/**
 * @author redsarow
 * @since 1.0
 */
public class SendMessage {

    private Bot bot;

    public SendMessage(Bot bot) {
        this.bot = bot;
    }

    public void sendDeath(String msg){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.withTitle(":skull_crossbones: Mort :skull_crossbones:");
        embedBuilder.withColor(Color.RED);
        embedBuilder.withDesc(msg);
        bot.getChannelOut().sendMessage(embedBuilder.build());
    }
}
