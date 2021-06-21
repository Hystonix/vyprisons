package com.glitchedturtle.vyprisons.player.mine.action;

import com.glitchedturtle.vyprisons.data.IDatabaseAction;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class FetchMineInstanceAction implements IDatabaseAction<FetchMineInstanceAction.Response> {

    public static class Response {

        private boolean _exists;
        private int _activeSchematicId;

        public boolean doesExist() {
            return _exists;
        }

        public int getActiveSchematicId() {
            return _activeSchematicId;
        }

    }

    private UUID _targetUuid;

    public FetchMineInstanceAction(UUID targetUuid) {
        _targetUuid = targetUuid;
    }

    @Override
    public Response executeAction(Connection con) throws SQLException {

        PreparedStatement statement = con.prepareStatement("SELECT * FROM `vy_player_mine` WHERE `owner_uuid`=? LIMIT 1");
        statement.setString(1, _targetUuid.toString());

        ResultSet rs = statement.executeQuery();
        Response res = new Response();

        res._exists = rs.next();
        if(!res._exists)
            return res;

        res._activeSchematicId = rs.getInt("active_schematic");
        return res;

    }

}
