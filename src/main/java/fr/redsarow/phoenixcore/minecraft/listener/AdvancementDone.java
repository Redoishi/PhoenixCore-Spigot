package fr.redsarow.phoenixcore.minecraft.listener;

import fr.redsarow.phoenixcore.PhoenixCore;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

/**
 * @author redsarow
 * @since 1.0
 */
public class AdvancementDone implements Listener {

    private final PhoenixCore pl;

    public AdvancementDone(PhoenixCore pl) {
        this.pl = pl;
    }

    @EventHandler
    public void onDeath(PlayerAdvancementDoneEvent event) {// TODO block si pas bon world
        Advancement advancement = event.getAdvancement();
        String nomInterne = advancement.getKey().getKey();
        if (nomInterne.startsWith("recipes")) {
            return;
        }
        pl.discordBot.getSendMessage()
                .sendAdvancement(event.getPlayer().getDisplayName() + ": " + event.getPlayer().getWorld().getName() + ": " + nomInterne);
    }
}
