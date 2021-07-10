package com.glitchedturtle.vyprisons.player.mine.action;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class DeleteMineInstanceAction implements IDatabaseAction<Void> {

    private UUID _ownerUuid;

    public DeleteMineInstanceAction(UUID ownerUuid) {
        _ownerUuid = ownerUuid;
    }

    @Override
    public Void executeAction(Connection con) throws SQLException {

        con.setAutoCommit(false);

        try {

            PreparedStatement mineDelete = con.prepareStatement(
                    "DELETE FROM `vy_player_mine` WHERE `owner_uuid`=?"
            );
            PreparedStatement lotteryDelete = con.prepareStatement(
                    "DELETE FROM `vy_lottery_entry` WHERE `mine_owner_uuid`=?"
            );

            mineDelete.setString(1, _ownerUuid.toString());
            lotteryDelete.setString(1, _ownerUuid.toString());

            mineDelete.execute();
            lotteryDelete.execute();

            con.commit();

        } catch(Exception ex) {
            con.rollback();
        }

        return null;

    }

}
