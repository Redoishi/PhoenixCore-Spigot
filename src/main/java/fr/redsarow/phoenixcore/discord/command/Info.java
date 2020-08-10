package fr.redsarow.phoenixcore.discord.command;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.rest.util.Color;
import fr.redsarow.phoenixcore.discord.Bot;

import static fr.redsarow.phoenixcore.discord.Bot.PREFIX;

/**
 * @author redsarow
 * @since 1.0
 */
public class Info extends ACommand {

    public Info() {
        super("info", "Les info du bot", PREFIX+"info",null);
    }

    @Override
    public boolean run(Message message) {
        User client = Bot.getClient().getSelf().block();

        message.getChannel().block().createEmbed(embed ->
                embed.setAuthor(client.getUsername(), null, client.getAvatarUrl())
                .setThumbnail(client.getAvatarUrl())
                .setDescription(client.getMention()+" créer par redsarow")
                .addField("Link","[github](https://github.com/redsarow)",false)
                .setFooter(PREFIX+"help", null)
                .setColor(Color.WHITE)
        );
        return true;
    }
}
