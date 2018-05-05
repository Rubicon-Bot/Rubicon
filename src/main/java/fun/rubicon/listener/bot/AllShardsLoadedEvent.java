/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener.bot;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.tools.CommandYouTube;
import fun.rubicon.core.entities.RubiconGiveaway;
import fun.rubicon.core.entities.RubiconRemind;
import fun.rubicon.util.BotListHandler;

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
        //Load all YouTube Events
        new CommandYouTube.YouTubeChecker(RubiconBot.getShardManager().getGuilds());
        //Load verification cache
        RubiconBot.getVerificationLoader().loadVerificationCache();
        //Load all reminders
        RubiconRemind.loadReminders();
        //Load all Giveaways
        RubiconGiveaway.loadGiveaways();
    }
}
