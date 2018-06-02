package fr.redsarow.phoenixcore.discord.command;

import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author redsarow
 * @since 1.0
 */
public abstract class ACommand {

    private final String name;
    private String description;
    private String usage;
    private List<String> exemple;
    private String alias ;

    public ACommand(String name, String description, String usage, List<String> exemple, String... alias) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.exemple = exemple == null ? new ArrayList<>() : exemple;

        CommandManagement.registerCommand(this);
        CommandManagement.registerAlias(name, alias);

        if (alias != null && alias.length > 0) {
            this.alias = Arrays.stream(alias).reduce((s, s2) -> s + "/" + s2).get();
        }else{
            this.alias = "N/C";
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public List<String> getExemple() {
        return exemple;
    }

    public String getAlias() {
        return alias;
    }

    public abstract boolean run(IMessage message);

}
