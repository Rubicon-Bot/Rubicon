/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.mysql;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseGenerator {


    public static boolean createAllDatabasesIfNecessary() {
        try {
            createGuildTable();
            createMemberTable();
            createJoinmessageTable();
            createLeavemessageTable();
            createUserDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void createGuildTable() {
        try {
            PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `guilds`" +
                    "(`id` INT(25) UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "`serverid` BIGINT(25) NOT NULL ," +
                    "`prefix` VARCHAR(5) NOT NULL ," +
                    " PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    private static void createJoinmessageTable() {
        try {
            PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `joinmessages`" +
                    "(`id` INT(25) UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "`serverid` BIGINT(25) NOT NULL," +
                    "`message` TEXT NOT NULL," +
                    "`channel` BIGINT(25)," +
                    "PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8"
            );
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    private static void createLeavemessageTable() {
        try {
            PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `leavemessages`" +
                    "(`id` INT(25) UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "`serverid` BIGINT(25) NOT NULL," +
                    "`message` TEXT NOT NULL," +
                    "`channel` BIGINT(25)," +
                    "PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8"
            );
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    private static void createMemberTable() {
        try {
            PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `members`" +
                    "(`id` INT(250) UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "`userid` BIGINT(25)," +
                    "`serverid` BIGINT(25)," +
                    "`level` INT(50)," +
                    "`points` INT(50)," +
                    " PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createUserDatabase() {
        try {
            PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `users`" +
                    "(`id` INT(250) UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "`userid` BIGINT(25)," +
                    "`bio` TEXT," +
                    "`money` INT(250)," +
                    "`premium` BIGINT(50)," +
                    "`language` VARCHAR(10),"+
                    "`afk` TEXT," +
                    " PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}