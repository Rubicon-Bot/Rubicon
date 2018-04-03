/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener.bot;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.BotListHandler;
import net.dv8tion.jda.core.entities.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class AllShardsLoadedEvent {

    public AllShardsLoadedEvent() {
        call();
    }

    private void call() {
        RubiconBot.setAllShardsInitialised(true);
        //Load all punishments (bans & mutes)
        RubiconBot.getPunishmentManager().loadPunishments();
        //Post Guild Stats
        BotListHandler.postStats(false);
        //Load all polls
        RubiconBot.getPollManager().loadPolls();
    }
}
