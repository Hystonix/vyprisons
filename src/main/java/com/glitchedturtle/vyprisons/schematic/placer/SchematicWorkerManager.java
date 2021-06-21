package com.glitchedturtle.vyprisons.schematic.placer;

import com.glitchedturtle.vyprisons.configuration.Conf;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class SchematicWorkerManager {

    private JavaPlugin _pluginInstance;

    private Queue<SchematicWorker.PlaceJob> _jobQueue = new LinkedList<>();
    private Collection<SchematicWorker> _workers = new LinkedList<>(); // because remove has O(1) time complexity.

    public SchematicWorkerManager(JavaPlugin pluginInstance) {
        _pluginInstance = pluginInstance;
    }

    public void addJob(SchematicWorker.PlaceJob job) {

        _jobQueue.add(job);
        job.setStatus(SchematicWorker.JobStatus.ENQUEUED);

        if(_workers.size() < Conf.MINE_WORKER_MAX_INSTANCES)
            this.createWorker();

    }

    private void createWorker() {

        SchematicWorker worker = new SchematicWorker(this);

        BukkitScheduler scheduler = Bukkit.getScheduler();
        int runId = scheduler.scheduleSyncRepeatingTask(_pluginInstance, worker, 1, 1); // Execute as often as possible (every tick)

        worker.setRunnableId(runId);
        _workers.add(worker);

    }

    SchematicWorker.PlaceJob nextJob() {
        return _jobQueue.poll();
    }

    boolean hasNextJob() {
        return _jobQueue.size() > 0;
    }

    void destroyWorker(SchematicWorker worker) {

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.cancelTask(worker.getRunnableId());

        _workers.remove(worker);

    }

}
