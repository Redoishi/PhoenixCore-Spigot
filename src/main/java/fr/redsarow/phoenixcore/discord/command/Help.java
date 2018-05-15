package fr.redsarow.phoenixcore.discord.command;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

import static fr.redsarow.phoenixcore.discord.Bot.PREFIX;

/**
 * @author redsarow
 * @since 1.0
 */
public class Help extends ACommand {

    public Help() {
        super("help", "Commande help", PREFIX + "help [commande]", null, "h");
    }

    @Override
    public void run(IMessage message) {
        String[] msgContent = message.getContent().split(" ");
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.withTitle(":question: Help");
        embedBuilder.withDesc("Prefix des commande: " + PREFIX);
        embedBuilder.withColor(Color.GREEN);

        if (msgContent.length < 2) {
            embedBuilder.appendDesc("\nListe des commandes");
            helpAllCommand(embedBuilder);
        } else {
            embedBuilder.appendDesc("\nAide pour " + msgContent[1]);
            ACommand command = CommandManagement.getCommand(msgContent[1]);
            if (command == null) {
                message.getChannel().sendMessage("La commande : " + msgContent[1] + "est inconnue!");
                return;
            }
            embedBuilder.appendField("Nom", command.getName(), true);
            embedBuilder.appendField("Description", command.getDescription(), true);
            embedBuilder.appendField("Usage", command.getUsage(), true);
            embedBuilder.appendField("Exemple", command.getExemple().toString(), true);
            embedBuilder.appendField("Alias", command.getAlias(), true);
        }

        message.getChannel().sendMessage(embedBuilder.build());
    }

    private void helpAllCommand(EmbedBuilder embedBuilder) {
        StringBuilder names = new StringBuilder();
        StringBuilder descriptions = new StringBuilder();
        StringBuilder usage = new StringBuilder();
        CommandManagement.getAllCommands().forEach(aCommand -> {
            names.append(aCommand.getName()).append("\n");
            descriptions.append(aCommand.getDescription()).append("\n");
            usage.append(aCommand.getUsage()).append("\n");
        });
        embedBuilder.appendField("Nom", names.toString(), true);
        embedBuilder.appendField("Description", descriptions.toString(), true);
        embedBuilder.appendField("Usage", usage.toString(), true);
    }
}
