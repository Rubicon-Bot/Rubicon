/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.events.*;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class ShardListener extends ListenerAdapter {

    private static int tempLoadedShards = 0;

    @Override
    public void onReady(ReadyEvent event) {
        tempLoadedShards++;
        if(tempLoadedShards == RubiconBot.getMaximumShardCount())
            new AllShardsLoadedEvent();
    }

    @Override
    public void onResume(ResumedEvent event) {
        super.onResume(event);
    }

    @Override
    public void onReconnect(ReconnectedEvent event) {
        super.onReconnect(event);
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        super.onDisconnect(event);
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        super.onShutdown(event);
    }

    @Override
    public void onStatusChange(StatusChangeEvent event) {
        super.onStatusChange(event);
    }

    @Override
    public void onException(ExceptionEvent event) {
        super.onException(event);
    }
}
