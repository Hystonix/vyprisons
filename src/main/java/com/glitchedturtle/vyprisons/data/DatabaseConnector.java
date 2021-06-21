package com.glitchedturtle.vyprisons.data;

import com.glitchedturtle.vyprisons.PluginStartException;
import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.data.action.CreateDatabaseStructureAction;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DatabaseConnector {

    private HikariDataSource _pool;
    private Executor _queryExecutor = Executors.newCachedThreadPool();

    public DatabaseConnector() {

        _pool = new HikariDataSource();

        _pool.setMinimumIdle(Conf.DB_MIN_IDLE);
        _pool.setMaximumPoolSize(Conf.DB_MAX_POOL_SIZE);

        _pool.setDataSourceClassName(Conf.DB_SOURCE_CLASS_NAME);

        _pool.setUsername(Conf.DB_AUTH_USERNAME);
        _pool.setPassword(Conf.DB_AUTH_PASSWORD);

        _pool.addDataSourceProperty("serverName", Conf.DB_SERVER_NAME);
        _pool.addDataSourceProperty("portNumber", Conf.DB_SERVER_PORT);
        _pool.addDataSourceProperty("databaseName", Conf.DB_DATABASE_NAME);

        _pool.addDataSourceProperty("useSSL", false);

    }

    public void initialize() throws PluginStartException {

        try {

            this.executeSync(new CreateDatabaseStructureAction());
            System.out.println("[DB] Connection pool established and tested!");

        } catch (SQLException ex) {
            throw new PluginStartException(ex, "Database Connector", ex.getMessage());
        }

    }

    public <I> I executeSync(IDatabaseAction<I> action) throws SQLException {

        Connection con = null;

        try {

            long[] benchmark = new long[5];
            benchmark[0] = System.currentTimeMillis();

            con =  _pool.getConnection();
            benchmark[1] = System.currentTimeMillis();

            I response = action.executeAction(con);
            benchmark[2] = System.currentTimeMillis();

            return response;

        } finally {

            if(con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

        }

    }

    public <I> CompletableFuture<I> execute(IDatabaseAction<I> action) {

        CompletableFuture<I> returnFuture = new CompletableFuture<>();

        _queryExecutor.execute(() -> {

            Connection con = null;

            try {

                long[] benchmark = new long[5];
                benchmark[0] = System.currentTimeMillis();

                con =  _pool.getConnection();
                benchmark[1] = System.currentTimeMillis();

                I response = action.executeAction(con);
                benchmark[2] = System.currentTimeMillis();

                Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(VyPrisonPlugin.class),
                        () -> returnFuture.complete(response));

            } catch(SQLException ex) {

                Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(VyPrisonPlugin.class),
                        () -> returnFuture.completeExceptionally(ex));

            } finally {

                if(con != null) {
                    try {
                        con.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }

            }

        });

        return returnFuture;

    }

}
