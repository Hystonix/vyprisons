package com.glitchedturtle.vyprisons.player.mine.lottery;

import com.glitchedturtle.vyprisons.data.DatabaseConnector;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.player.mine.action.lottery.IncreaseLotteryValueAction;
import com.glitchedturtle.vyprisons.player.mine.action.lottery.InsertLotteryEntryAction;
import com.glitchedturtle.vyprisons.player.mine.action.lottery.ResetLotteryStateAction;

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

    private Set<UUID> _entries;
    private double _lotteryValue = 0;

    public MineLotteryHandler(PlayerMineInstance mineInstance, DatabaseConnector dbConnector) {
        _mineInstance = mineInstance;
        _dbConnector = dbConnector;
    }

    public CompletableFuture<Void> addEntry(UUID uuid) {

        return _dbConnector.execute(new InsertLotteryEntryAction(_mineInstance.getOwnerUniqueId(), uuid))
                .whenComplete((v, ex) -> {

                if(ex != null)
                    return;

                _entries.add(uuid);

        });

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

        return _dbConnector.execute(new ResetLotteryStateAction(_mineInstance.getOwnerUniqueId())).thenApply((v) -> {

            LotteryResult result = new LotteryResult();
            UUID[] entries = _entries.toArray(new UUID[0]);

            ThreadLocalRandom random = ThreadLocalRandom.current();
            result._winners = new HashSet<>();
            result._value = _lotteryValue;

            for(int i = 0; i < numWinners; i++)
                result._winners.add(entries[random.nextInt(entries.length)]);

            _entries.clear();
            _lotteryValue = 0;

            return result;

        });

    }



}
