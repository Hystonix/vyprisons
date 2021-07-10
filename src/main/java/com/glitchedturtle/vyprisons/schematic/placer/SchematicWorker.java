package com.glitchedturtle.vyprisons.schematic.placer;

import com.glitchedturtle.common.util.SafeLocation;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.schematic.SchematicType;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class SchematicWorker implements Runnable {

    public enum JobStatus {

        NONE,
        ENQUEUED,
        IN_PROGRESS,
        DONE

    }

    public static class PlaceJob {

        private int _blocksPerTick;
        private int _placesPerTick;

        private SafeLocation _pastePoint;
        private Vector _origin;

        private SchematicType _type;
        private SchematicWorker.JobStatus _status = SchematicWorker.JobStatus.NONE;

        private int[] _cursor = new int[] { 0, 0, 0 };
        private int _counter = 0;
        private Clipboard _schematicData;
        private CompletableFuture<Void> _completeFuture;

        public PlaceJob(SafeLocation pastePoint, SchematicType type, int blocksPerTick, int placesPerTick, CompletableFuture<Void> completeFuture) {

            _pastePoint = pastePoint;
            _type = type;
            _blocksPerTick = blocksPerTick;
            _placesPerTick = placesPerTick;
            _completeFuture = completeFuture;

        }

        private void loadSchematicData() throws IOException {

            _schematicData = _type.loadSchematic();

            Vector pastePoint = _pastePoint.toVector();
            BlockVector3 pasteOrigin = _schematicData.getOrigin();
            BlockVector3 minimum = _schematicData.getMinimumPoint();

            _origin = new Vector(
                    pastePoint.getX() - pasteOrigin.getX() + minimum.getX(),
                    pastePoint.getY() - pasteOrigin.getY() + minimum.getY(),
                    pastePoint.getZ() - pasteOrigin.getZ() + minimum.getZ()
            );

        }

        public SchematicWorker.JobStatus getStatus() {
            return _status;
        }

        void setStatus(SchematicWorker.JobStatus status) {

            _status = status;
            System.out.println("[Schematic] Job status updated to " + status.toString());

        }

        private boolean incrementCursor() {

            BlockVector3 max = _schematicData.getDimensions();
            _counter++;

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

        private void completeJob() {

            _schematicData = null;
            this.setStatus(JobStatus.DONE);

            _completeFuture.complete(null);

        }

        public int getVolume() {

            BlockVector3 dim = _schematicData.getDimensions();
            return dim.getX() * dim.getY() * dim.getZ();

        }

        public double getProgress() {
            return _counter * 1.0d/ this.getVolume();
        }

        public int getBlocksLeft() {
            return this.getVolume() - _counter;
        }

        public long getEta() {
            return this.getBlocksLeft() / (_blocksPerTick * 20L);
        }

    }

    private SchematicWorkerManager _placerManager;
    private int _runnableId = -1;

    private PlaceJob _currentJob = null;

    public SchematicWorker(SchematicWorkerManager manager) {
        _placerManager = manager;
    }

    @Override
    public void run() {

        if(_currentJob == null) {

            if(!_placerManager.hasNextJob()) {

                _placerManager.destroyWorker(this);
                return;

            }

            _currentJob = _placerManager.nextJob();
            _currentJob.setStatus(JobStatus.IN_PROGRESS);

            try {
                _currentJob.loadSchematicData();
            } catch(IOException ex) {

                ex.printStackTrace();

                _currentJob._completeFuture.completeExceptionally(ex);

                _currentJob._schematicData = null;
                _currentJob.setStatus(JobStatus.DONE);
                _currentJob = null;

                return;

            }

        }

        int placeCounter = 0;
        for(int i = 0; i < _currentJob._blocksPerTick
                && placeCounter < _currentJob._placesPerTick; i++) {

            PlaceResponse res = this.doPlace();
            if(res == PlaceResponse.PLACE)
                placeCounter++;

            if(res == PlaceResponse.DONE) {

                _currentJob.completeJob();
                _currentJob = null;
                return;

            }

        }

    }

    private enum PlaceResponse {
        NONE, PLACE, DONE;
    }

    private PlaceResponse doPlace() {

        PlaceJob job = _currentJob;

        Clipboard schematic = job._schematicData;
        BlockVector3 pos = schematic.getMinimumPoint().add(
                job._cursor[0],
                job._cursor[1],
                job._cursor[2]
        );

        BaseBlock block = schematic.getFullBlock(pos);
        PlaceResponse res = PlaceResponse.NONE;

        if(block != null
                && block.getBlockType() != BlockTypes.AIR) {

            Location realPos = _currentJob._origin.toLocation(Conf.MINE_WORLD.getWorld())
                    .add(job._cursor[0], job._cursor[1], job._cursor[2]);

            Block realBlock = realPos.getBlock();
            BlockData data = BukkitAdapter.adapt(block);

            realBlock.setType(data.getMaterial());
            realBlock.setBlockData(data);

            res = PlaceResponse.PLACE;

        }

        return _currentJob.incrementCursor() ? PlaceResponse.DONE : res;

    }

    int getRunnableId() {
        return _runnableId;
    }

    void setRunnableId(int runnableId) {
        _runnableId = runnableId;
    }

}
