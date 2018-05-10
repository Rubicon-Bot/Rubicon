/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.entities;

import fun.rubicon.core.entities.impl.RubiconUserImpl;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.List;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class RubiconUser extends RubiconUserImpl {

    public RubiconUser(User user, String bio, long money, long premium, String language, String afk, HashMap<String, List<String>> playlists) {
        super(user, bio, money, premium, language, afk, playlists);
    }

    public RubiconUser(User user, HashMap<String, ?> map) {
        super(user, map);
    }
}
