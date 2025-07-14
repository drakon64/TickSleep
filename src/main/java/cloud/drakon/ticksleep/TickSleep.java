package cloud.drakon.ticksleep;

import org.bukkit.Bukkit;
import org.bukkit.ServerTickManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TickSleep extends JavaPlugin implements Listener {
    private final ServerTickManager serverTickManager = Bukkit.getServerTickManager();

    // Is the server sprinting because of us?
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

            // TODO: Record the skip amount as a time and save it as a class member so we can resume our sprint if it's stopped

            // If the server isn't already sprinting because of us
            if (!sprinting) {
                sprinting = true;

                // Initiate a sprint for the same amount of ticks as would've been skipped
                // TODO: Skip this if an already-initiated sprint is longer than our requested sprint
                var skipAmount = (int) event.getSkipAmount();
                serverTickManager.requestGameToSprint(skipAmount);

                getLogger().info("Sprinting for " + skipAmount + " ticks");
            }
        }
    }

    private void stopSprinting() {
        // If the server is sprinting because of us
        if (sprinting) {
            // Stop the sprint
            serverTickManager.stopSprinting();
            sprinting = false;
        }
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        stopSprinting();

        getLogger().info("Stopping sprint due to player leaving bed");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        stopSprinting();

        getLogger().info("Stopping sprint due to player joining server");
    }

    // Prevent players starving to death while sleeping
    @EventHandler
    public void onSleepingPlayerDamageByStarvation(EntityDamageEvent event) {
        if (sprinting && event.getEntityType() == EntityType.PLAYER && event.getCause() == EntityDamageEvent.DamageCause.STARVATION) {
            var player = (Player) event.getEntity();

            if (player.isSleeping()) {
                event.setCancelled(true);
            }
        }
    }
}
