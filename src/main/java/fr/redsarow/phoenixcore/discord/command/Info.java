package fr.redsarow.phoenixcore.discord.command;

import fr.redsarow.phoenixcore.discord.Bot;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

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
    public boolean run(IMessage message) {
        IUser client = Bot.getClient().getOurUser();
        EmbedObject build = new EmbedBuilder()
                .withAuthorName(client.getName())
                .withAuthorIcon(client.getAvatarURL())
                .withThumbnail(client.getAvatarURL())

                .withDesc(client.mention()+" créer par redsarow")
                .appendField("Link","[github](https://github.com/redsarow)",false)
                .withFooterText(PREFIX+"help")

                .withColor(Color.WHITE)

                .build();
        message.getChannel().sendMessage(build);
        return true;
    }
}
