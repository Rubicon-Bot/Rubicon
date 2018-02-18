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


    public static boolean createAllDatabasesIfNecessary() {
        try {
            createGuildDatabase();
            createMemberDatabase();
            createUserDatabase();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
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
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createMemberDatabase(){
        try {
            PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `members`"+
            "(`id` INT(250) unsigned NOT NULL auto_increment,"+
            "`userid` INT(25),"+
            "`serverid` INT(25),"+
            "`permissionlevel` VARCHAR(50),"+
            "`level` INT(50),"+
            "`points` INT(50),"+
            " PRIMARY KEY (`id`)"+
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createUserDatabase(){
        try {
            PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `members`"+
            "(`id` INT(250) unsigned NOT NULL auto_increment,"+
            "`userid` INT(25),"+
            "`bio` TEXT,"+
            "`money` INT(250),"+
            "`premium` VARCHAR(50),"+
            " PRIMARY KEY (`id`)"+
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
