package com.glitchedturtle.vyprisons.data.action;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;
import com.glitchedturtle.vyprisons.player.mine.MineAccessLevel;

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
                        + "`access_level` VARCHAR(32) DEFAULT \"" + MineAccessLevel.PRIVATE.toString() + "\","
                        + "`tax_level` REAL DEFAULT 0.10,"
                        + "`lottery_value` REAL DEFAULT 0,"
                    + "PRIMARY KEY(`owner_uuid`))"
        ).execute();

        con.prepareStatement(
                "CREATE TABLE IF NOT EXISTS `vy_lottery_entry` ("
                        + "`mine_owner_uuid` VARCHAR(36) NOT NULL,"
                        + "`entry_owner_uuid` VARCHAR(36) NOT NULL,"
                    + "PRIMARY KEY(`mine_owner_uuid`,`entry_owner_uuid`))"
        ).execute();

        return null;

    }

}
