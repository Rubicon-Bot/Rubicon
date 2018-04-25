/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.entities;

import net.dv8tion.jda.core.entities.User;

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
}
