package com.glitchedturtle.vyprisons.player.mine.action.type;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SetSchematicTypeAction implements IDatabaseAction<Void> {

    private UUID _uuid;
    private int _id;

    public SetSchematicTypeAction(UUID uuid, int id) {
        _uuid = uuid;
        _id = id;
    }

    @Override
    public Void executeAction(Connection con) throws SQLException {

        PreparedStatement statement = con.prepareStatement("UPDATE `vy_player_mine` SET `active_schematic`=? WHERE `owner_uuid`=?");
        statement.setInt(1, _id);
        statement.setString(2, _uuid.toString());

        if (statement.executeUpdate() == 0)
            throw new SQLException("Update failed");

        return null;

    }

}
