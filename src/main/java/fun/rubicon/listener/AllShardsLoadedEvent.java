/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.moderation.CommandMute;
import fun.rubicon.util.BotListHandler;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class AllShardsLoadedEvent {

    public AllShardsLoadedEvent() {
        call();
    }

    private void call() {
        RubiconBot.setAllShardsInited(true);
        CommandMute.loadMutes();
        //Post Guild Stats
        BotListHandler.postStats(false);
    }
}
