package fr.redsarow.phoenixcore.discord.command;

import fr.redsarow.phoenixcore.discord.Bot;
import org.bukkit.Bukkit;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.Map;
import java.util.UUID;

import static fr.redsarow.phoenixcore.discord.Bot.PREFIX;

/**
 * @author redsarow
 * @since 1.0
 */
public class DeathCount extends ACommand {

    private Bot bot;

    public DeathCount(Bot bot) {
        super("DeathCount", "Commande DeathCount", PREFIX + "DeathCount", null, "dc");
        this.bot = bot;
    }

    @Override
    public void run(IMessage message) {
        Map<String, Integer> allDeath = bot.getPlugin().getPlayerDeathCount().getAll();
        StringBuilder names = new StringBuilder();
        StringBuilder death = new StringBuilder();
        allDeath.forEach((s, integer) -> {
            names.append( Bukkit.getOfflinePlayer(UUID.fromString(s)).getName()).append("\n");
            death.append(integer).append("\n");
        });

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.withTitle(":skull: Tableau des morts :skull:");
        embedBuilder.withColor(Color.ORANGE);
        embedBuilder.appendField("Nom",names.toString(), true);
        embedBuilder.appendField("Morts", death.toString(), true);
        message.getChannel().sendMessage(embedBuilder.build());
    }

}
