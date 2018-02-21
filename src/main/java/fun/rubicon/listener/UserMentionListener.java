/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class UserMentionListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getType() == ChannelType.PRIVATE)
            return;
        if (event.getAuthor().isFake() || event.getAuthor().isBot())
            return;
        if (event.getMessage().getMentionedUsers().size() == 1) {
            RubiconUser rubiconUser = RubiconUser.fromUser(event.getMessage().getMentionedUsers().get(0));
            if (rubiconUser.getUser() == event.getAuthor())
                return;
            if (rubiconUser.isAFK()) {
                SafeMessage.sendMessage(event.getTextChannel(), EmbedUtil.info("**" + rubiconUser.getUser().getName() + "** " + RubiconBot.sGetTranslations().getUserLocale(event.getAuthor()).getResourceBundle().getString("event.afk.title") + "!", rubiconUser.getAFKState()).build());
            }
        }
    }
}
