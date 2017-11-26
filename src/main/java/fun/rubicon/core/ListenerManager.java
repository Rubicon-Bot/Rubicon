package fun.rubicon.core;

import fun.rubicon.listener.*;
import net.dv8tion.jda.core.JDABuilder;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.core
 */

public class ListenerManager {

    private JDABuilder b;

    public ListenerManager(JDABuilder builder) {
        this.b = builder;
        initListener();
    }

    private void initListener() {
        b.addEventListener(new SelfMentionListener());
        b.addEventListener(new SQLPreventDisconnect());
        b.addEventListener(new AutoroleExecutor());
        b.addEventListener(new BotJoinListener());
        b.addEventListener(new MemberLevelListener());
        b.addEventListener(new ChannelDeleteListener());
        b.addEventListener(new BotLeaveListener());
        b.addEventListener(new ReactionListener());
        b.addEventListener(new PortalListener());
        b.addEventListener(new AutochannelListener());
    }
}
