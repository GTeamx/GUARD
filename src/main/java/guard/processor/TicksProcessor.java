package guard.processor;

import guard.Guard;
import guard.data.GuardPlayerManager;
import guard.utils.Pair;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

public final class TicksProcessor implements Runnable {

    @Getter
    private int ticks;
    private static BukkitTask task;

    public void start() {
        assert task == null : "TickProcessor has already been started!";

        task = Bukkit.getScheduler().runTaskTimer(Guard.instance, this, 0L, 1L);
    }

    public static void stop() {
        if (task == null) return;

        task.cancel();
        task = null;
    }

    @Override
    public void run() {
        ticks++;

        GuardPlayerManager.guardPlayers.parallelStream()
                .forEach(data -> {
                    final Entity target = data.getTarget();
                    final Entity lastTarget = data.lastTarget;
                    if (target != null && lastTarget != null) {
                        if (target != lastTarget) {
                            data.getTargetLocations().clear();
                        }
                        Location location = target.getLocation();
                        data.getTargetLocations().add(new Pair<>(location, ticks));
                    }
                });
    }
}
