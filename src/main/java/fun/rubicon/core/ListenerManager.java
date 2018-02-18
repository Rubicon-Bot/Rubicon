/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core;

import fun.rubicon.RubiconBot;
import fun.rubicon.listener.*;
import net.dv8tion.jda.core.JDABuilder;

public class ListenerManager {

    private JDABuilder b;

    public ListenerManager(JDABuilder builder) {
        this.b = builder;
        initListener();
    }

    private void initListener() {
        b.addEventListener(new SelfMentionListener());
        b.addEventListener(new AutoroleExecutor());
        b.addEventListener(new BotJoinListener());
        b.addEventListener(new MemberLevelListener());
        b.addEventListener(new ChannelDeleteListener());
        b.addEventListener(new BotLeaveListener());
        b.addEventListener(new ReactionListener());
        b.addEventListener(new PortalListener());
        b.addEventListener(new AutochannelListener());
        b.addEventListener(new UserJoinListener());
        b.addEventListener(new VerificationListener());
        b.addEventListener(new MessageDeleteListener());
        b.addEventListener(new MemberLeaveListener());
        b.addEventListener(new ServerLogHandler());
        b.addEventListener(new ChannelDeleteListener());
        b.addEventListener(new RoleListener());
        b.addEventListener(RubiconBot.getWebpanelManager());
        b.addEventListener(new TextChannelListener());
        b.addEventListener(new MessageListener());

    }
}
