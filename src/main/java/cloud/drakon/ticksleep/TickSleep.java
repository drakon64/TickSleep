package cloud.drakon.ticksleep;

import org.bukkit.Bukkit;
import org.bukkit.ServerTickManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TickSleep extends JavaPlugin implements Listener {
    private final ServerTickManager serverTickManager = Bukkit.getServerTickManager();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onTimeSkip(TimeSkipEvent event) {
        if (event.getSkipReason() == TimeSkipEvent.SkipReason.NIGHT_SKIP) {
            event.setCancelled(true);

            serverTickManager.requestGameToSprint((int) event.getSkipAmount());
        }
    }
}
