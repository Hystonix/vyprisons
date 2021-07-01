package com.glitchedturtle.vyprisons.player.mine.reset;

import com.glitchedturtle.common.region.Region;
import com.glitchedturtle.common.util.SafeLocation;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicInstance;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class MineResetWorker implements Runnable {

    public static class ResetJob {

        private PlayerMineInstance _instance;
        private int[] _cursor = new int[] { 0, 0, 0 };

        private Runnable _beginCallback = null;
        private Runnable _completeCallback = null;

        private long _delayTicks = Math.max(Conf.MINE_RESET_DELAY * 20, 1);

        public ResetJob(PlayerMineInstance instance) {
            _instance = instance;
        }

        private boolean incrementCursor() {

            SchematicInstance instance = _instance.getSchematicInstance();
            if(instance == null)
                return true;

            Vector max = instance.getMineRegion().getDimensions()
                    .add(new Vector(1, 0, 1));

            _cursor[0]++;
            if(_cursor[0] >= max.getX()) {
                _cursor[0] = 0;
                _cursor[2]++;
            }
            if(_cursor[2] >= max.getZ()) {
                _cursor[2] = 0;
                _cursor[1]++;
            }

            return _cursor[1] >= max.getY();

        }

        public void completeJob() {

            if(_completeCallback != null)
                _completeCallback.run();

        }

        public void cancelDelay() {
            _delayTicks = 1;
        }

        public void setBeginCallback(Runnable beginCallback) {
            _beginCallback = beginCallback;
        }
        public void setCompleteCallback(Runnable completeCallback) {
            _completeCallback = completeCallback;
        }

        public void cancel() {

            SchematicInstance instance = _instance.getSchematicInstance();
            if(instance == null)
                return;

            Vector max = instance.getMineRegion().getDimensions()
                    .add(new Vector(1, 0, 1));
            _cursor[1] = max.getBlockY() + 1;

        }

    }

    private MineResetManager _manager;
    private int _runnableId;

    private ResetJob _currentJob = null;

    public MineResetWorker(MineResetManager manager) {
        _manager = manager;
    }

    @Override
    public void run() {

        if(_currentJob == null) {

            if(!_manager.hasNextJob()) {

                _manager.destroyWorker(this);
                return;

            }

            _currentJob = _manager.nextJob();

        }

        if(_currentJob._delayTicks > 0) {

            _currentJob._delayTicks--;
            if(_currentJob._delayTicks > 0)
                return;

            if(_currentJob._beginCallback != null)
                _currentJob._beginCallback.run();

        }

        for(int i = 0; i < Conf.MINE_RESET_BLOCKS_PER_TICK; i++) {

            if(this.doPlace()) {

                _currentJob.completeJob();
                _currentJob = null;

                return;

            }

        }

    }

    private boolean doPlace() {

        SchematicInstance instance = _currentJob._instance.getSchematicInstance();
        if(instance == null)
            return true;

        int[] cursor = _currentJob._cursor;

        Location loc = instance.getMineRegion().getMinimum();
        loc.add(cursor[0], cursor[1], cursor[2]);

        loc.getBlock().setType(_currentJob._instance.randomMineType());
        return _currentJob.incrementCursor();

    }

    int getRunnableId() {
        return _runnableId;
    }

    void setRunnableId(int runnableId) {
        _runnableId = runnableId;
    }

}