package hakasenz.tep2;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class nobugdamage extends JavaPlugin implements Listener {

    private Map<UUID, BukkitTask> taskMap;

    @Override
    public void onEnable() {
        taskMap = new HashMap<>();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        taskMap.values().forEach(BukkitTask::cancel);
        taskMap.clear();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        UUID playerId = player.getUniqueId();

        if (taskMap.containsKey(playerId) && !taskMap.get(playerId).isCancelled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (taskMap.containsKey(playerId) && !taskMap.get(playerId).isCancelled()) {
            taskMap.get(playerId).cancel();
        }

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                taskMap.remove(playerId);
            }
        }.runTaskLater(this, 5);

        taskMap.put(playerId, task);
    }
}
