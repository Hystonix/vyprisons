package com.glitchedturtle.vyprisons.player.mine.action;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;
import com.glitchedturtle.vyprisons.schematic.SchematicType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class CreateMineInstanceAction implements IDatabaseAction<Boolean> {

    private UUID _owner;
    private SchematicType _type;

    public CreateMineInstanceAction(UUID owner, SchematicType type) {
        _owner = owner;
        _type = type;
    }

    @Override
    public Boolean executeAction(Connection con) throws SQLException {

        PreparedStatement statement = con.prepareStatement("INSERT INTO `vy_player_mine` (owner_uuid, active_schematic) VALUES (?, ?)");

        statement.setString(1, _owner.toString());
        statement.setInt(2, _type.getIdentifier());

        return statement.executeUpdate() > 0;

    }

}
