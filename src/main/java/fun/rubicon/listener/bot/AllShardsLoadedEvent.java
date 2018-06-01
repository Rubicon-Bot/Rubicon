/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener.bot;

import fun.rubicon.listener.events.RubiconEvent;
import net.dv8tion.jda.core.JDA;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class AllShardsLoadedEvent extends RubiconEvent {

    public AllShardsLoadedEvent(JDA api, long responseNumber) {
        super(api, responseNumber);
    }


}
