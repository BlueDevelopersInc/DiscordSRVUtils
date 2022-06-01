/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2022 BlueTree242
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

package tk.bluetree242.discordsrvutils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
public class DatabaseManager {
    private final DiscordSRVUtils core;
    //database connection pool
    private HikariDataSource sql;
    private boolean hsqldb = false;
    private final Settings settings = new Settings()
            .withRenderQuotedNames(RenderQuotedNames.NEVER);
    public void setupDatabase() throws SQLException {
        System.setProperty("hsqldb.reconfig_logging", "false");
        try {
            Class.forName("tk.bluetree242.discordsrvutils.dependencies.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException e) {
            core.getLogger().severe("Could not set JDBCDriver");
            return;
        }
        HikariConfig settings = new HikariConfig();
        String jdbcurl = null;
        String user = null;
        String pass = null;
        if (core.getSqlconfig().isEnabled()) {
            jdbcurl = "jdbc:mysql://" +
                    core.getSqlconfig().Host() +
                    ":" + core.getSqlconfig().Port() + "/" + core.getSqlconfig().DatabaseName() + "?sessionVariables=&&sql_mode='ANSI_QUOTES'";
            user = core.getSqlconfig().UserName();
            pass = core.getSqlconfig().Password();
        } else {
            core.logger.info("MySQL is disabled, using hsqldb");
            hsqldb = true;
            jdbcurl = "jdbc:hsqldb:file:" + Paths.get(core.getPlatform().getDataFolder() + core.fileseparator + "database").resolve("Database") + ";hsqldb.lock_file=false;sql.syntax_mys=true;sql.lowercase_ident=true";
            user = "SA";
            pass = "";
        }
        settings.setJdbcUrl(jdbcurl);
        settings.setUsername(user);
        settings.setPassword(pass);
        sql = new HikariDataSource(settings);
        migrate();
        core.getLogger().info("MySQL/HsqlDB Connected & Setup");
    }

    public void migrate() {
        //Migrate tables, and others.
        Flyway flyway = Flyway.configure(getClass().getClassLoader())
                .dataSource(sql)
                .locations("classpath:flyway-migrations")
                .validateMigrationNaming(true).group(true)
                .table("discordsrvutils_schema")
                .load();
        //repair if there is an issue
        flyway.repair();
        flyway.migrate();
    }

    public Connection getConnection() throws SQLException {
        return sql.getConnection();
    }


    public DSLContext jooq(Connection conn) {
        return DSL.using(conn, hsqldb ? SQLDialect.HSQLDB : SQLDialect.MYSQL, settings);
    }

    public DSLContext newJooqConnection() {
        try {
            return jooq(getConnection());
        } catch (SQLException ex) {
            throw new UnCheckedSQLException(ex);
        }
    }

    public void close() {
        if (sql != null) sql.close();
        sql = null;
    }
}
