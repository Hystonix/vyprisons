package com.glitchedturtle.vyprisons.player.mine.action.lottery;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;
import com.glitchedturtle.vyprisons.player.mine.MineAccessLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SetTaxLevelAction implements IDatabaseAction<Void> {

    private UUID _uuid;
    private double _taxLevel;

    public SetTaxLevelAction(UUID uuid, double taxLevel) {

        _uuid = uuid;
        _taxLevel = taxLevel;

    }

    @Override
    public Void executeAction(Connection con) throws SQLException {

        PreparedStatement statement = con.prepareStatement("UPDATE `vy_player_mine` SET `tax_level`=? WHERE `owner_uuid`=?");
        statement.setDouble(1, _taxLevel);
        statement.setString(2, _uuid.toString());

        if (statement.executeUpdate() == 0)
            throw new SQLException("Update failed");

        return null;

    }
}
