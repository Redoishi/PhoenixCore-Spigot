package fr.redsarow.phoenixcore.discord;

import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

import static fr.redsarow.phoenixcore.discord.Bot.PREFIX;

/**
 * @author redsarow
 * @since 1.0
 */
public class SendMessage {

    private Bot bot;

    public SendMessage(Bot bot) {
        this.bot = bot;
    }

    public void sendMsg(String msg){
        bot.getChannelOut().sendMessage(msg);
    }

    public void sendDeath(String msg){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.withTitle(":skull_crossbones: Mort :skull_crossbones:");
        embedBuilder.withColor(Color.RED);
        embedBuilder.withDesc(msg);
        bot.getChannelOut().sendMessage(embedBuilder.build());
    }

    public void sendAdvancement(String msg){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.withTitle(":100: Advancement Get");
        embedBuilder.withColor(Color.BLUE);
        embedBuilder.withDesc(msg);
        bot.getChannelOut().sendMessage(embedBuilder.build());
    }

    public void sendNotGrantedPlayer(String msg, String playername){//TODO
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.withTitle(":warning: Joueur non connue!");
        embedBuilder.withColor(Color.ORANGE);
        embedBuilder.withDesc(msg);
        embedBuilder.appendDesc(" Utiliser: "+PREFIX+"grant "+playername);
        bot.getChannelOut().sendMessage(embedBuilder.build());
    }

    public void sendNewGrantedPlayer(String msg){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.withTitle(":white_check_mark:  Nouveau joueur");
        embedBuilder.withColor(Color.GREEN);
        embedBuilder.withDesc(msg);
        bot.getChannelOut().sendMessage(embedBuilder.build());
    }

}
