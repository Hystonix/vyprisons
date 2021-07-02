package com.glitchedturtle.vyprisons.player.mine.action.lottery;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class IncreaseLotteryValueAction implements IDatabaseAction<Double> {

    private UUID _ownerUuid;
    private double _delta;

    public IncreaseLotteryValueAction(UUID ownerUuid, double delta) {
        _ownerUuid = ownerUuid;
        _delta = delta;
    }

    @Override
    public Double executeAction(Connection con) throws SQLException {

        PreparedStatement executeStatement =
                con.prepareStatement("UPDATE `vy_player_mine` SET `lottery_worth`=`lottery_worth`+? WHERE `owner_uuid`=?");

        executeStatement.setDouble(1, _delta);
        executeStatement.setString(2, _ownerUuid.toString());

        if(executeStatement.executeUpdate() == 0)
            throw new SQLException("Update failed");

        PreparedStatement fetchStatement =
                con.prepareStatement("SELECT `lottery_worth` FROM `vy_player_mine` WHERE `owner_uuid`=?");
        fetchStatement.setString(1, _ownerUuid.toString());

        ResultSet rs = fetchStatement.executeQuery();
        return rs.getDouble("lottery_worth");

    }

}
