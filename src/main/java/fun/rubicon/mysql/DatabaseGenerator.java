/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.mysql;

import fun.rubicon.RubiconBot;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseGenerator {


    public static void createAllDatabasesIfNecessary() {
        createGuildDatabase();
    }

    private static void createGuildDatabase(){
        try {
            PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `guilds`"+
            "(`id` INT(25) unsigned NOT NULL auto_increment,"+
            "`serverid` INT(25) NOT NULL ,"+
            "`prefix` VARCHAR(5) NOT NULL ,"+
            "`joinmsg` TEXT," +
            "`leavemsg` TEXT," +
            "`channel` INT(25),"+
            "`logchannel` INT(25),"+
            "`autorole` INT(25),"+
            "`portal` TEXT,"+
            "`autochannels` VARCHAR(250),"+
            "`cases` INT(11),"+
            "`lvlmsg` INT(11),"+
            "`whitelist` TEXT,"+
            "`blacklist` TEXT," +
            " PRIMARY KEY (`id`)"+
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
