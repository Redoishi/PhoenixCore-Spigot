package fr.redsarow.phoenixcore.minecraft.listener;

import fr.redsarow.phoenixcore.PhoenixCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

/**
 * @author redsarow
 * @since 1.0
 */
public class AdvancementDone implements Listener {

    private PhoenixCore pl;

    public AdvancementDone(PhoenixCore pl) {

        this.pl = pl;
    }

    @EventHandler
    public void onDeath(PlayerAdvancementDoneEvent event) {
        pl.discordBot.getSendMessage().sendAdvancement(event.getAdvancement().toString());
    }
}
