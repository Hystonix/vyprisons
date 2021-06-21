package com.glitchedturtle.vyprisons.schematic.pool.action;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;

import java.sql.*;

public class CreateProtoSchematicInstanceAction implements IDatabaseAction<Integer> {

    private int _schematicType;

    public CreateProtoSchematicInstanceAction(int schematicType) {
        _schematicType = schematicType;
    }

    @Override
    public Integer executeAction(Connection con) throws SQLException {

        PreparedStatement statement = con.prepareStatement("INSERT INTO `vy_schematic_instance` (schematic_type) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, _schematicType);

        int affectedRows = statement.executeUpdate();
        if(affectedRows == 0)
            throw new SQLException("Failed to create row");
        try (ResultSet keySet = statement.getGeneratedKeys()) {

            if(!keySet.next())
                throw new SQLException("Failed to create row");
            return keySet.getInt(1);

        }

    }

}
