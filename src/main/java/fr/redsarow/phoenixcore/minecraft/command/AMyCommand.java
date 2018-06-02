package fr.redsarow.phoenixcore.minecraft.command;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author redsarow
 * @since 1.0
 */
public abstract class AMyCommand<T extends JavaPlugin> extends Command implements CommandExecutor, PluginIdentifiableCommand {

    private T plugin;
    private boolean register = false;
    private HashMap<Integer, ArrayList<TabCommand>> tabComplete;

    //--------------------------------------------------
    //|                 constructeur                   |
    //--------------------------------------------------


    /**
     * A la création ne pas oublier CommandMap pou la methode registerCommand();
     *
     * @param plugin le plugin responsable de la command.
     * @param name   le nom de la command.
     */
    AMyCommand(T plugin, String name) {
        super(name);

        assert plugin != null;
        assert name != null;
        assert name.length() > 0;

        setLabel(name);
        this.plugin = plugin;
        tabComplete = new HashMap<>();
    }

    //--------------------------------------------------
    //|                     add                        |
    //--------------------------------------------------

    /**
     * @param description la description de la commande
     *
     * @return AMyCommand
     */
    protected AMyCommand addDescription(String description) {
        if (register || description != null)
            setDescription(description);
        return this;
    }

    /**
     * @param usage comment utiliser la commande ex: /maCmd [val]
     *
     * @return AMyCommand
     */
    protected AMyCommand addUsage(String usage) {
        if (register || usage != null)
            setUsage(usage);
        return this;
    }

    /**
     * @param aliases les aliases de la command.
     *
     * @return AMyCommand
     */
    protected AMyCommand addAliases(String... aliases) {
        if (aliases != null && (register || aliases.length > 0))
            setAliases(Arrays.stream(aliases).collect(Collectors.toList()));
        return this;
    }

    //--------------------------------------------------

    /**
     * Permet d'ajouter un argumet à un indice avec une permission et le/les mots avant
     *
     * @param indice     l'indice où ce trouve arg dans la commande. /macommande ce trouve à l'indice 0, donc /macommande indice1 indice2 ...
     * @param arg        le mot à ajouter
     * @param permission la permition lier (peut être null)
     * @param textAvant  le texte précédant le mot (peut être null)
     *
     * @return AMyCommand
     */
    protected AMyCommand addOneTabbComplete(int indice, String arg, String permission, String... textAvant) {
        if (arg != null && indice >= 0) {
            if (tabComplete.containsKey(indice)) {
                tabComplete.get(indice).add(new TabCommand(indice, arg, permission, textAvant));
            } else {
                tabComplete.put(indice, new ArrayList<TabCommand>() {{
                    add(new TabCommand(indice, arg, permission, textAvant));
                }});
            }
        }
        return this;
    }

    /**
     * Permet d'ajouter plusieur mots à un indice avec une permission et le/les mots avant.
     *
     * @param indice     l'indice où ce trouve arg dans la commande. /macommande ce trouve à l'indice 0, donc /macommande indice1 indice2 ...
     * @param permission la permition lier (peut être null)
     * @param textAvant  le texte précédant le mot (peut être null)
     * @param arg        les mots à ajouter
     *
     * @return AMyCommand
     */
    protected AMyCommand addListTabbComplete(int indice, String permission, String[] textAvant, String... arg) {
        if (arg != null && arg.length > 0 && indice >= 0) {
            if (tabComplete.containsKey(indice)) {
                tabComplete.get(indice).addAll(Arrays.stream(arg).collect(
                        ArrayList::new,
                        (tabCommands, s) -> tabCommands.add(new TabCommand(indice, s, permission, textAvant)),
                        ArrayList::addAll));
            } else {
                tabComplete.put(indice, Arrays.stream(arg).collect(
                        ArrayList::new,
                        (tabCommands, s) -> tabCommands.add(new TabCommand(indice, s, permission, textAvant)),
                        ArrayList::addAll)
                );
            }
        }
        return this;
    }

    /**
     * Permet d'ajouter plusieur mots à un indice.
     *
     * @param indice l'indice où ce trouve arg dans la commande. /macommande ce trouve à l'indice 0, donc /macommande indice1 indice2 ...
     * @param arg    les mots à ajouter
     *
     * @return AMyCommand
     */
    protected AMyCommand addListTabbComplete(int indice, String... arg) {
        if (arg != null && arg.length > 0 && indice >= 0) {
            addListTabbComplete(indice, null, null, arg);
        }
        return this;
    }

    //--------------------------------------------------

    /**
     * @param permission la permission a ajouter
     *
     * @return AMyCommand
     */
    protected AMyCommand addPermission(String permission) {
        if (register || permission != null)
            setPermission(permission);
        return this;
    }

    /**
     * @param permissionMessage le message si le joueur na pas la permition.
     *
     * @return AMyCommand
     */
    protected AMyCommand addPermissionMessage(String permissionMessage) {
        if (register || permissionMessage != null)
            setPermissionMessage(permissionMessage);
        return this;
    }

    /**
     * /!\ à faire à la fin /!\ permet denregister la commande.
     *
     * @param commandMap via:   Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
     *                   f.setAccessible(true);
     *                   CommandMap commandMap = (CommandMap) f.get(Bukkit.getServer());
     *
     * @return true si la commande a bient ete enregistrer
     */
    protected boolean registerCommand(CommandMap commandMap) {
        return register ? false : commandMap.register("", this);
    }

    //--------------------------------------------------
    //|                    get & set                   |
    //--------------------------------------------------

    /**
     * @return le plugin responsable de la commande
     */
    @Override
    public T getPlugin() {
        return this.plugin;
    }

    /**
     * @return tabComplete
     */
    public HashMap<Integer, ArrayList<TabCommand>> getTabComplete() {
        return tabComplete;
    }

    //--------------------------------------------------
    //|                      @Over                     |
    //--------------------------------------------------

    /**
     * @param commandSender le sender
     * @param command       la command
     * @param arg           les argument de la command
     *
     * @return true si ok, false sinon
     */
    @Override
    public boolean execute(CommandSender commandSender, String command, String[] arg) {
        if (getPermission() != null) {
            if (!commandSender.hasPermission(getPermission())) {
                if (getPermissionMessage() == null) {
                    commandSender.sendMessage(ChatColor.RED + "pas la perme :(");
                } else {
                    commandSender.sendMessage(getPermissionMessage());
                }
                return false;
            }
        }
        if (onCommand(commandSender, this, command, arg))
            return true;
        commandSender.sendMessage(ChatColor.RED + getUsage());
        return false;

    }

    /**
     * @param sender le sender
     * @param alias
     * @param args
     *
     * @return
     */
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {

        int indice = args.length - 1;

        if ((getPermission() != null && !sender.hasPermission(getPermission())) || tabComplete.size() == 0 || !tabComplete.containsKey(indice))
            return super.tabComplete(sender, alias, args);

        ArrayList<String> list = tabComplete.get(indice).stream().filter(tabCommand ->
                (tabCommand.getTextAvant() == null || tabCommand.getTextAvant().contains(args[indice - 1])) &&
                        (tabCommand.getPermission() == null || sender.hasPermission(tabCommand.getPermission())) &&
                        (tabCommand.getText().startsWith(args[indice]))
        ).map(TabCommand::getText).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        return list.size() < 1 ? super.tabComplete(sender, alias, args) : list;

    }

    //--------------------------------------------------
    //|               class TabCommand                 |
    //--------------------------------------------------

    private class TabCommand {

        private int indice;
        private String text;
        private String permission;
        private ArrayList<String> textAvant;

        //--------------------------------------------------
        //|                 constructeur                   |
        //--------------------------------------------------

        private TabCommand(int indice, String text, String permission, String... textAvant) {
            this.indice = indice;
            this.text = text;
            this.permission = permission;
            if (textAvant == null || textAvant.length < 1) {
                this.textAvant = null;
            } else {
                this.textAvant = Arrays.stream(textAvant).collect(ArrayList::new,
                        ArrayList::add,
                        ArrayList::addAll);
            }
        }

        private TabCommand(int indice, String text, String permission) {
            this(indice, text, permission, "");
        }

        private TabCommand(int indice, String text, String[] textAvant) {
            this(indice, text, null, textAvant);
        }

        private TabCommand(int indice, String text) {
            this(indice, text, null, "");
        }

        //--------------------------------------------------
        //|                    get & set                   |
        //--------------------------------------------------

        public String getText() {
            return text;
        }

        public int getIndice() {
            return indice;
        }

        public String getPermission() {
            return permission;
        }

        public ArrayList<String> getTextAvant() {
            return textAvant;
        }

    }
}
