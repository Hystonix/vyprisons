package com.glitchedturtle.vyprisons.schematic.pool.action;

import com.glitchedturtle.common.util.SafeLocation;
import com.glitchedturtle.common.util.StringParser;
import com.glitchedturtle.vyprisons.data.IDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class FetchSchematicInstancesOfTypeAction implements IDatabaseAction<Collection<FetchSchematicInstancesOfTypeAction.SchematicData>> {

    public class SchematicData {

        private int _id;
        private int _schematicId;
        private SafeLocation _origin;

        public SchematicData(int id, int schematicId, SafeLocation origin) {
            _id = id;
            _schematicId = schematicId;
            _origin = origin;
        }

        public int getId() {
            return _id;
        }

        public int getSchematicId() {
            return _schematicId;
        }

        public SafeLocation getOrigin() {
            return _origin;
        }

    }

    private int _schematicId;

    public FetchSchematicInstancesOfTypeAction(int schematicId) {
        _schematicId = schematicId;
    }

    @Override
    public Collection<SchematicData> executeAction(Connection con) throws SQLException {

        PreparedStatement statement = con.prepareStatement(
                "SELECT * FROM `vy_schematic_instance` WHERE `schematic_type`=? AND `ready`=1"
        );

        statement.setInt(1, _schematicId);

        ResultSet rs = statement.executeQuery();
        Collection<SchematicData> instances = new ArrayList<>();

        while(rs.next())
            instances.add(new SchematicData(
                    rs.getInt("id"),
                    rs.getInt("schematic_type"),
                    StringParser.parsePosition(rs.getString("origin_point"))));

        return instances;

    }

}
