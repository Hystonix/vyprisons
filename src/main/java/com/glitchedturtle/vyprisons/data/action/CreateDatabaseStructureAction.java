package com.glitchedturtle.vyprisons.data.action;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;

import java.sql.Connection;
import java.sql.SQLException;

public class CreateDatabaseStructureAction implements IDatabaseAction<Void> {

    @Override
    public Void executeAction(Connection con) throws SQLException {

        con.prepareStatement(
                "CREATE TABLE IF NOT EXISTS `vy_schematic_instance` ("
                        + "`id` INT NOT NULL AUTO_INCREMENT,"
                        + "`schematic_type` INT NOT NULL,"
                        + "`origin_point` VARCHAR(64) DEFAULT null,"
                        + "`ready` TINYINT(1) DEFAULT 0,"
                    + "PRIMARY KEY (`id`))"
        ).execute();

        con.prepareStatement(
                "CREATE TABLE IF NOT EXISTS `vy_player_mine` ("
                        + "`owner_uuid` VARCHAR(36) NOT NULL,"
                        + "`active_schematic` TINYINT(8),"
                        + "`tier` TINYINT(8) DEFAULT 1,"
                    + "PRIMARY KEY(`owner_uuid`))"
        ).execute();

        return null;

    }

}
