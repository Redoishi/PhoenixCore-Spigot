package fr.redsarow.phoenixcore.discord;

import fr.redsarow.mi18n.api.I18n;
import fr.redsarow.phoenixcore.PhoenixCore;
import fr.redsarow.phoenixcore.discord.command.DeathCount;
import fr.redsarow.phoenixcore.discord.command.Grant;
import fr.redsarow.phoenixcore.discord.command.Help;
import fr.redsarow.phoenixcore.discord.command.Info;
import fr.redsarow.phoenixcore.discord.listener.TryCommand;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.DiscordException;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author redsarow
 * @since 1.0
 */
public class Bot {

    public static Logger LOGGER;
    public static String PREFIX;
    public static List<String> ROLES;

    private static IDiscordClient client;
    private static I18n i18n;

    private IChannel channelOut;
    private List<IChannel> channelIn;

    private final PhoenixCore plugin;
    private final SendMessage sendMessage;

    public Bot(PhoenixCore plugin) throws URISyntaxException {
        this.plugin = plugin;
        i18n = new I18n(this.getClass(), "phoenixCoreLangDiscord", Locale.FRENCH);
        LOGGER = plugin.getLogger();
        PREFIX = plugin.CONFIG.getStringVal("prefix");
        ROLES = plugin.CONFIG.getStringListVal("roles");

        client = createClient(plugin.CONFIG.getStringVal("token"), true);

        //event
        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(new TryCommand(this));
        dispatcher.registerListener(this);

        //command
        new Info();
        new Help();
        new DeathCount(this);
        new Grant(this);

        sendMessage = new SendMessage(this);
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

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        List<IChannel> channels = client.getGuilds().get(0).getChannels();

        List<String> stringChannels = plugin.CONFIG.getStringListVal("channel.in");

        channelIn = stringChannels.stream().collect(
                ArrayList<IChannel>::new
                , (channels1, s) -> channels1.add(channels.stream().filter(channel -> channel.getName().equalsIgnoreCase(s)).findFirst().get())
                , ArrayList::addAll
        );

        String stringChannel = plugin.CONFIG.getStringVal("channel.out");
        channelOut = channels.stream().filter(channel -> channel.getName().equalsIgnoreCase(stringChannel)).findFirst().get();

        System.out.println("Bot is now ready!");
    }

    public static IDiscordClient getClient() {
        return client;
    }

    public IChannel getChannelOut() {
        return channelOut;
    }

    public List<IChannel> getChannelIn() {
        return channelIn;
    }

    public PhoenixCore getPlugin() {
        return plugin;
    }

    public SendMessage getSendMessage() {
        return sendMessage;
    }
}
