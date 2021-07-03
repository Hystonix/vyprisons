package com.glitchedturtle.vyprisons.player.mine.action.lottery;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class ResetLotteryStateAction implements IDatabaseAction<Void> {

    private UUID _uuid;

    public ResetLotteryStateAction(UUID uuid) {
        _uuid = uuid;
    }

    @Override
    public Void executeAction(Connection con) throws SQLException {

        PreparedStatement entryDeleteStatement = con.prepareStatement("DELETE FROM `vy_lottery_entry` WHERE `mine_owner_uuid`=?");
        entryDeleteStatement.setString(1, _uuid.toString());

        if(entryDeleteStatement.executeUpdate() == 0)
            throw new SQLException("Failed delete");

        PreparedStatement resetValueStatement = con.prepareStatement("UPDATE `vy_player_mine` SET `lottery_value`=0 WHERE `owner_uuid`=?");
        resetValueStatement.setString(1, _uuid.toString());

        if(resetValueStatement.executeUpdate() == 0)
            throw new SQLException("Failed update");

        return null;

    }

}
