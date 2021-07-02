package com.glitchedturtle.vyprisons.player.mine.lottery;

import com.glitchedturtle.common.util.TAssert;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.data.DatabaseConnector;
import com.glitchedturtle.vyprisons.player.mine.MineAccessLevel;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.player.mine.action.lottery.IncreaseLotteryValueAction;
import com.glitchedturtle.vyprisons.player.mine.action.lottery.InsertLotteryEntryAction;
import com.glitchedturtle.vyprisons.player.mine.action.lottery.ResetLotteryStateAction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class MineLotteryHandler {

    public class LotteryResult {

        private double _value;
        private Set<UUID> _winners;

    }

    private PlayerMineInstance _mineInstance;
    private DatabaseConnector _dbConnector;

    private Set<UUID> _entries = new HashSet<>();
    private double _lotteryValue = 0;

    private boolean _rolling = false;

    public MineLotteryHandler(PlayerMineInstance mineInstance, DatabaseConnector dbConnector) {
        _mineInstance = mineInstance;
        _dbConnector = dbConnector;
    }

    public CompletableFuture<Void> addEntry(UUID uuid) {

        _entries.add(uuid);
        return _dbConnector.execute(new InsertLotteryEntryAction(_mineInstance.getOwnerUniqueId(), uuid));

    }

    public CompletableFuture<Double> increaseValue(double delta) {

        return _dbConnector.execute(new IncreaseLotteryValueAction(_mineInstance.getOwnerUniqueId(), delta))
                .whenComplete((newValue, ex) -> {

                if(ex != null)
                    return;

                _lotteryValue = newValue;

        });

    }

    public CompletableFuture<LotteryResult> selectWinner(int numWinners) {

        TAssert.assertFalse(_rolling, "Already rolling");

        _rolling = true;
        return _dbConnector.execute(new ResetLotteryStateAction(_mineInstance.getOwnerUniqueId())).thenApply((v) -> {

            LotteryResult result = new LotteryResult();
            UUID[] entries = _entries.toArray(new UUID[0]);

            ThreadLocalRandom random = ThreadLocalRandom.current();
            result._winners = new HashSet<>();
            result._value = this.getValue();

            for(int i = 0; i < numWinners; i++)
                result._winners.add(entries[random.nextInt(entries.length)]);

            _entries.clear();
            _lotteryValue = 0;

            _rolling = false;
            return result;

        });

    }

    public void rollLottery() {



    }

    public boolean hasEntry(UUID uuid) {
        return _entries.contains(uuid);
    }

    public Collection<UUID> getEntries() {
        return _entries;
    }

    public boolean isRolling() {
        return _rolling;
    }

    public boolean isLotteryEnabled() {
        return _mineInstance.getAccessLevel() != MineAccessLevel.PRIVATE;
    }

    public double getValue() {
        return Math.min(_lotteryValue, Conf.LOTTERY_MAX_VALUE);
    }

}
