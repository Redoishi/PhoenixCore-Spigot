package fr.redsarow.phoenixcore.discord;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import fr.redsarow.mi18n.api.I18n;
import fr.redsarow.phoenixcore.PhoenixCore;
import fr.redsarow.phoenixcore.discord.command.*;
import reactor.core.publisher.Mono;

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

    private static GatewayDiscordClient client;
    private static I18n i18n;

    private Channel channelOut;
    private List<Channel> channelIn;

    private final PhoenixCore plugin;
    private final SendMessage sendMessage;

    public Bot(PhoenixCore plugin) throws URISyntaxException {
        this.plugin = plugin;
        i18n = new I18n(this.getClass(), "phoenixCoreLangDiscord", Locale.FRENCH, Locale.ENGLISH);
        LOGGER = plugin.getLogger();
        PREFIX = plugin.CONFIG.getStringVal("prefix");
        ROLES = plugin.CONFIG.getStringListVal("roles");

        createClient(plugin.CONFIG.getStringVal("token"));

        // events
        EventDispatcher dispatcher = client.getEventDispatcher();
        dispatcher.on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(msg -> msg.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(msg -> getChannelIn().contains(msg.getChannel().block()))
                .filter(msg -> msg.getContent().startsWith(Bot.PREFIX))
                .flatMap(CommandManagement::run)
                .subscribe();

        dispatcher.on(ReadyEvent.class)
                .subscribe(this::onReady);

        //command
        new Info();
        new Help();
        new DeathCount(this);
        new Grant(this);

        sendMessage = new SendMessage(this);
    }

    private void createClient(String token) {
        client = DiscordClientBuilder.create("token")
                .build()
                .gateway()
                .setInitialStatus(s -> Presence.doNotDisturb(Activity.playing("Init ...")))
                .login()
                .block();
    }

    public void disconnect(){
        client.onDisconnect().block();
    }

    private Mono<Void> onReady(ReadyEvent event) {

        List<GuildChannel> channels = client.getGuilds()
                .toStream()
                .findFirst()
                .get()
                .getChannels()
                .collectList()
                .block();

        List<String> stringChannels = plugin.CONFIG.getStringListVal("channel.in");

        channelIn = stringChannels.stream().collect(
                ArrayList<Channel>::new
                , (channels1, s) -> channels1.add(channels.stream().filter(channel -> channel.getName().equalsIgnoreCase(s)).findFirst().get())
                , ArrayList::addAll
        );

        String stringChannel = plugin.CONFIG.getStringVal("channel.out");
        channelOut = channels.stream().filter(channel -> channel.getName().equalsIgnoreCase(stringChannel)).findFirst().get();

        System.out.println("Bot is now ready!");
        // set status
        client.updatePresence(Presence.online(Activity.playing( PREFIX + "help")));

        return Mono.empty().then();
    }

    public static GatewayDiscordClient getClient() {
        return client;
    }

    public Channel getChannelOut() {
        return channelOut;
    }

    public List<Channel> getChannelIn() {
        return channelIn;
    }

    public PhoenixCore getPlugin() {
        return plugin;
    }

    public SendMessage getSendMessage() {
        return sendMessage;
    }

    public static I18n getI18n() {
        return i18n;
    }
}
