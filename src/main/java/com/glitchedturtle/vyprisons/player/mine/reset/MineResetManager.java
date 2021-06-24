package com.glitchedturtle.vyprisons.player.mine.reset;

import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class MineResetManager {

    private JavaPlugin _pluginInstance;

    private Queue<MineResetWorker.ResetJob> _jobQueue = new LinkedList<>();
    private Collection<MineResetWorker> _workerPool = new LinkedList<>();

    public MineResetManager(JavaPlugin javaPlugin) {
        _pluginInstance = javaPlugin;
    }

    public MineResetWorker.ResetJob enqueueReset(PlayerMineInstance instance) {
        return this.enqueueReset(instance, false);
    }

    public MineResetWorker.ResetJob enqueueReset(PlayerMineInstance instance, boolean skipDelay) {

        MineResetWorker.ResetJob resetJob = new MineResetWorker.ResetJob(instance);
        if(skipDelay)
            resetJob.cancelDelay();

        _jobQueue.add(resetJob);

        if(_workerPool.size() < Conf.MINE_RESET_MAX_WORKERS) {

            MineResetWorker worker = new MineResetWorker(this);
            worker.setRunnableId(Bukkit.getScheduler()
                    .scheduleSyncRepeatingTask(_pluginInstance, worker, 1, 1));

            _workerPool.add(worker);

        }

        return resetJob;

    }

    MineResetWorker.ResetJob nextJob() {
        return _jobQueue.poll();
    }

    boolean hasNextJob() {
        return _jobQueue.size() > 0;
    }

    void destroyWorker(MineResetWorker worker) {

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.cancelTask(worker.getRunnableId());

        _workerPool.remove(worker);

    }

}
