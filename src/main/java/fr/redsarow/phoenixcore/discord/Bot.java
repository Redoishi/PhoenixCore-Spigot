package fr.redsarow.phoenixcore.discord;

import fr.redsarow.phoenixcore.PhoenixCore;
import fr.redsarow.phoenixcore.discord.command.Help;
import fr.redsarow.phoenixcore.discord.command.Info;
import fr.redsarow.phoenixcore.discord.listener.TryCommand;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.DiscordException;

import java.util.logging.Logger;

/**
 * @author redsarow
 * @since 1.0
 */
public class Bot {

    public static Logger LOGGER;
    public static String PREFIX;

    private static IDiscordClient client;

    public Bot(PhoenixCore plugin) {
        LOGGER = plugin.getLogger();
        PREFIX = PhoenixCore.CONFIG.getStringVal("prefix");
        client = createClient(PhoenixCore.CONFIG.getStringVal("token"), true);

        //event
        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(new TryCommand());

        //cmd
        new Info();
        new Help();
    }

    private IDiscordClient createClient(String token, boolean login) {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);
        clientBuilder.setPresence(StatusType.ONLINE, ActivityType.PLAYING, PREFIX + "help");

        try {
            if (login) {
                return clientBuilder.login();
            } else {
                return clientBuilder.build();
            }
        } catch (DiscordException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static IDiscordClient getClient() {
        return client;
    }
}
