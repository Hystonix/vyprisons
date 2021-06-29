package com.glitchedturtle.vyprisons.player.mine.action.tier;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class IncrementMineTierAction implements IDatabaseAction<Integer> {

    private UUID _uuid;
    private int _amount;

    public IncrementMineTierAction(UUID uuid, int amount) {
        _uuid = uuid;
        _amount = amount;
    }

    @Override
    public Integer executeAction(Connection con) throws SQLException {

        PreparedStatement statement = con.prepareStatement("UPDATE `vy_player_mine` SET `tier`=`tier`+? WHERE `owner_uuid`=?");
        statement.setInt(1, _amount);
        statement.setString(2, _uuid.toString());

        if(statement.executeUpdate() == 0)
            throw new SQLException("Update failed");

        PreparedStatement fetchStatement = con.prepareStatement("SELECT tier FROM `vy_player_mine` WHERE `owner_uuid`=?");
        fetchStatement.setString(1, _uuid.toString());

        ResultSet rs = fetchStatement.executeQuery();
        return rs.getInt("tier");

    }

}
