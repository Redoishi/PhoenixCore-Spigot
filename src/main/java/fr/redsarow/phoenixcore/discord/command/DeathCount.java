package fr.redsarow.phoenixcore.discord.command;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import fr.redsarow.phoenixcore.discord.Bot;
import org.bukkit.Bukkit;

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
    public boolean run(Message message) {
        Map<String, Integer> allDeath = bot.getPlugin().getPlayerDeathCount();
        StringBuilder names = new StringBuilder();
        StringBuilder death = new StringBuilder();
        allDeath.forEach((s, integer) -> {
            names.append( Bukkit.getOfflinePlayer(UUID.fromString(s)).getName()).append("\n");
            death.append(integer).append("\n");
        });

        message.getChannel().block().createEmbed(embed ->
                embed.setTitle(":skull: Tableau des morts :skull:")
                        .setColor(Color.BLACK)
                        .addField("Nom", names.toString().equals("")?"N/C":names.toString(), true)
                        .addField("Mort(s)", death.toString().equals("")?"N/C":death.toString(), true)
        ).block();

        return true;
    }

}
