/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.entities;

import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class RubiconUser extends RubiconUserImpl {

    /*
     *  If you want to add new methods, add them in RubiconUserImpl
     */

    public RubiconUser(User user) {
        super(user);
    }

    public RubiconUser unban(Guild guild){
        try{
            PreparedStatement ps = mySQL.getConnection().prepareStatement("DELETE FROM punishments WHERE serverid =? AND serverid=? AND type ='ban'");
            ps.setLong(1, guild.getIdLong());
            ps.setLong(2, user.getIdLong());
            ps.execute();
        } catch (SQLException e){
            Logger.error(e);
        }
        return this;
    }
}
