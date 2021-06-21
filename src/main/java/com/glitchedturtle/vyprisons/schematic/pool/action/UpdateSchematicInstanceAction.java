package com.glitchedturtle.vyprisons.schematic.pool.action;

import com.glitchedturtle.common.util.StringParser;
import com.glitchedturtle.vyprisons.data.IDatabaseAction;
import org.bukkit.util.Vector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateSchematicInstanceAction implements IDatabaseAction<Void> {

    private int _id;
    private Vector _originPosition;

    public UpdateSchematicInstanceAction(int id, Vector originPosition) {
        _id = id;
        _originPosition = originPosition;
    }

    @Override
    public Void executeAction(Connection con) throws SQLException {

        PreparedStatement statement = con.prepareStatement("UPDATE `vy_schematic_instance` SET `ready`=1,`origin_point`=? WHERE `id`=?");
        statement.setString(1, StringParser.locationToString(_originPosition));
        statement.setInt(2, _id);

        int affectedRows = statement.executeUpdate();
        if(affectedRows == 0)
            throw new SQLException("Update failed");

        return null;

    }

}
