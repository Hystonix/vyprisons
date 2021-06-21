package com.glitchedturtle.vyprisons.data;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDatabaseAction<I> {

    I executeAction(Connection con) throws SQLException;

}
