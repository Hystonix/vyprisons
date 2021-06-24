package com.glitchedturtle.vyprisons.player.mine.action;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SetMineTierAction implements IDatabaseAction<Void> {

    private UUID _uuid;
    private int _amount;

    public SetMineTierAction(UUID uuid, int amount) {
        _uuid = uuid;
        _amount = amount;
    }

    @Override
    public Void executeAction(Connection con) throws SQLException {

        PreparedStatement statement = con.prepareStatement("UPDATE `vy_player_mine` SET `tier`=`tier`+? WHERE `owner_uuid`=?");
        statement.setInt(1, _amount);
        statement.setString(2, _uuid.toString());

        if (statement.executeUpdate() == 0)
            throw new SQLException("Update failed");

        return null;

    }

}
