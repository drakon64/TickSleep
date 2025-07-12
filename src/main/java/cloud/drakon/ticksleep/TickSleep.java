package cloud.drakon.ticksleep;

import org.bukkit.Bukkit;
import org.bukkit.ServerTickManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TickSleep extends JavaPlugin implements Listener {
    private final ServerTickManager serverTickManager = Bukkit.getServerTickManager();
    private boolean sprinting = false;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onTimeSkip(TimeSkipEvent event) {
        // If the time skip is initiated by players sleeping in beds
        if (event.getSkipReason() == TimeSkipEvent.SkipReason.NIGHT_SKIP) {
            // Cancel the time skip
            event.setCancelled(true);

            // Initiate a sprint for the same amount of ticks as would've been skipped
            // TODO: Get if a sprint was already initiated and if it was for longer than our requested sprint, and do nothing if it is
            serverTickManager.requestGameToSprint((int) event.getSkipAmount());
            sprinting = true;
        }
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        // If a sprint is already initiated because of us
        if (sprinting) {
            // Stop the sprint
            serverTickManager.stopSprinting();
            sprinting = false;
        }
    }
}
