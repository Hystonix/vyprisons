package com.glitchedturtle.vyprisons.player.mine.action.lottery;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class InsertLotteryEntryAction implements IDatabaseAction<Void> {

    private UUID _ownerUuid;
    private UUID _uuid;

    public InsertLotteryEntryAction(UUID ownerUuid, UUID uuid) {
        _ownerUuid = ownerUuid;
        _uuid = uuid;
    }

    @Override
    public Void executeAction(Connection con) throws SQLException {

        PreparedStatement statement = con.prepareStatement("INSERT INTO `vy_lottery_entry` VALUES (?,?)");
        statement.setString(1, _ownerUuid.toString());
        statement.setString(2, _uuid.toString());

        if (statement.executeUpdate() == 0)
            throw new SQLException("Update failed");

        return null;


    }

}
