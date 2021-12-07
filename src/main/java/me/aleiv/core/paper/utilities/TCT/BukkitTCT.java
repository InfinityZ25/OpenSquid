package me.aleiv.core.paper.utilities.TCT;

import java.util.concurrent.CompletableFuture;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * BukkitTCT - Quick and dirty way to run tasks asynchronously and synchronously
 * without needing any dependencies.
 * 
 * @author jcedeno
 */
public class BukkitTCT extends TaskChainTool {

    private static Plugin plugin = null;

    /**
     * Static method to pass a Bukkit instance to the TaskChainTool. Must be called
     * only once, but can be called as many times as needed in case plugin becomes
     * null.
     * 
     * @param instance Plugin instance
     * @return true if plugin instance was set, false otherwise
     */
    public static boolean registerPlugin(Plugin instance) {
        plugin = instance;
        return isPluginReady();
    }

    /**
     * Helper method to check if plugin instance is ready.
     * 
     * @return true if plugin instance is ready, false otherwise
     */
    public static boolean isPluginReady() {
        return plugin != null && plugin.isEnabled();
    }

    /**
     * Builder method to add a task to the chain.
     * 
     * @param runnable The task to add.
     * @param delay    The delay in milliseconds after the task is executed.
     * @return The builder object.
     */

    @Override
    public TaskChainTool addWithDelay(Runnable runnable, long delay) {
        queue.add(() -> {
            if (runnable instanceof BukkitRunnable bRunnable)
                handleBukkit(bRunnable);
            else
                runnable.run();

            final var actualDelay = Math.abs(delay);

            /** Ensure the delay is greater than 0ms */
            if (actualDelay > 0)
                DelayTask.of(actualDelay).run();

        });
        return this;
    }

    @Override
    public CompletableFuture<Boolean> execute() {
        this.totalTasks = queue.size();
        var future = new CompletableFuture<Boolean>();

        EXECUTOR_SERVICE.submit(() -> {
            while (!isEmpty()) {
                var nextRunnable = this.poll();
                if (nextRunnable instanceof BukkitRunnable bukkitRunnable)
                    handleBukkit(bukkitRunnable);
                else
                    handleRunnable(nextRunnable); // use the method already defined in TaskChainTool.

            }
            future.complete(true);

        });
        return future;
    }

    /**
     * Helper method to handle BukkitRunnable. Just did this to keep the complexity
     * of {@link #execute()} down.
     * 
     * @param bukkitRunnable
     */
    private void handleBukkit(final BukkitRunnable bukkitRunnable) {
        var bukkitTask = bukkitRunnable.runTask(plugin);
        var id = bukkitTask.getTaskId();

        while (!bukkitTask.isCancelled() && plugin.getServer().getScheduler().isCurrentlyRunning(id)) {
            // Hold on bukkit finishing the task.
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}