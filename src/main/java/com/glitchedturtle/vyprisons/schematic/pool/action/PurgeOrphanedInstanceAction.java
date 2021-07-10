package com.glitchedturtle.vyprisons.schematic.pool.action;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PurgeOrphanedInstanceAction implements IDatabaseAction<Void> {

    @Override
    public Void executeAction(Connection con) throws SQLException {

        PreparedStatement executeStatement = con.prepareStatement(
                "DELETE FROM `vy_schematic_instance` WHERE `ready`=0"
        );

        executeStatement.execute();
        return null;

    }

}
