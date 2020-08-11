package fr.redsarow.phoenixcore.discord;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.util.Color;

import static fr.redsarow.phoenixcore.discord.Bot.PREFIX;

/**
 * @author redsarow
 * @since 1.0
 */
public class SendMessage {

    private Bot bot;
    private RestChannel restChannel;


    public SendMessage(Bot bot) {
        this.bot = bot;
        this.restChannel = bot.getChannelOut().getRestChannel();
    }

    public void sendMsg(String msg){
        restChannel.createMessage(msg);
    }

    public void sendDeath(String msg){
        EmbedCreateSpec embed = new EmbedCreateSpec()
                .setTitle(":skull_crossbones: Mort :skull_crossbones:")
                .setColor(Color.RED)
                .setDescription(msg);
        restChannel.createMessage(embed.asRequest()).block();
    }

    public void sendAdvancement(String msg){
        EmbedCreateSpec embed = new EmbedCreateSpec()
                .setTitle(":100: Advancement Get")
                .setColor(Color.BLUE)
                .setDescription(msg);
        restChannel.createMessage(embed.asRequest()).block();
    }

    public void sendNotGrantedPlayer(String msg, String playername){
        EmbedCreateSpec embed = new EmbedCreateSpec()
                .setTitle(":warning: Joueur non connue!")
                .setColor(Color.ORANGE)
                .setDescription(msg + " \n Utiliser: " + PREFIX + "grant " + playername);
        restChannel.createMessage(embed.asRequest()).block();
    }

    public void sendNewGrantedPlayer(String msg){
        EmbedCreateSpec embed = new EmbedCreateSpec()
                .setTitle(":white_check_mark:  Nouveau joueur\"")
                .setColor(Color.GREEN)
                .setDescription(msg);
        restChannel.createMessage(embed.asRequest()).block();
    }

}
