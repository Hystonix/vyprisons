package com.glitchedturtle.vyprisons.player.mine.action.privacy;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;
import com.glitchedturtle.vyprisons.player.mine.MineAccessLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SetMineAccessLevelAction implements IDatabaseAction<Void> {

    private UUID _uuid;
    private MineAccessLevel _accessLevel;

    public SetMineAccessLevelAction(UUID uuid, MineAccessLevel accessLevel) {
        _uuid = uuid;
        _accessLevel = accessLevel;
    }

    @Override
    public Void executeAction(Connection con) throws SQLException {

        PreparedStatement statement = con.prepareStatement("UPDATE `vy_player_mine` SET `access_level`=? WHERE `owner_uuid`=?");
        statement.setString(1, _accessLevel.toString());
        statement.setString(2, _uuid.toString());

        if (statement.executeUpdate() == 0)
            throw new SQLException("Update failed");

        return null;

    }

}
