/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import org.apache.commons.lang.NotImplementedException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Represents a minecraft table-row.
 */
public class MinecraftSQL implements DatabaseGenerator, DatabaseEntry {
    private final MySQL mySQL;

    /**
     * Initializes this database entity.
     * @param mySQL the database.
     */
    public MinecraftSQL(MySQL mySQL) {
        this.mySQL = mySQL;
    }

    @Override
    public boolean exists() {
        throw new NotImplementedException();
    }

    @Override
    public void create() {
        throw new NotImplementedException();
    }

    @Override
    public String get(String type) {
        throw new NotImplementedException();
    }

    @Override
    public void set(String type, String value) {
        throw new NotImplementedException();
    }

    @Override
    public void createTableIfNotExist() throws SQLException {
        mySQL.getActiveConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS `mincraft` (" +
                        "`id` INT(250) NOT NULL AUTO_INCREMENT," +
                        "`uuid` TEXT," +
                        "`playername` TEXT," +
                        "`awaitingaprooval` TEXT, " +
                        " PRIMARY KEY (`id`)" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=3918 DEFAULT CHARSET=utf8;"
        ).execute();
    }
}
