package fun.rubicon.registries;

import fun.rubicon.command.CommandManager;
import fun.rubicon.core.music.LavalinkManager;
import fun.rubicon.features.portal.PortalMessageListener;
import fun.rubicon.listener.AutochannelListener;
import fun.rubicon.listener.GeneralMessageListener;
import fun.rubicon.listener.GeneralReactionListener;
import fun.rubicon.listener.UserMentionListener;
import fun.rubicon.listener.bot.*;
import fun.rubicon.listener.channel.TextChannelDeleteListener;
import fun.rubicon.listener.channel.VoiceChannelDeleteListener;
import fun.rubicon.listener.feature.LogListener;
import fun.rubicon.listener.feature.PunishmentListener;
import fun.rubicon.listener.feature.VerificationListener;
import fun.rubicon.listener.feature.VoteListener;
import fun.rubicon.listener.member.MemberJoinListener;
import fun.rubicon.listener.member.MemberLeaveListener;
import fun.rubicon.listener.role.RoleDeleteListener;
import fun.rubicon.setup.SetupListener;
import lombok.Data;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;

@Data
public class EventRegistry {

    private final DefaultShardManagerBuilder shardManagerBuilder;
    private final CommandManager commandManager;

    public void register() {
        shardManagerBuilder.addEventListeners(
                new BotJoinListener(),
                new BotLeaveListener(),
                commandManager,
                new UserMentionListener(),
                new ShardListener(),
                new SelfMentionListener(),
                new VoteListener(),
                new MemberJoinListener(),
                new MemberLeaveListener(),
                new TextChannelDeleteListener(),
                new VoiceChannelDeleteListener(),
                new GeneralReactionListener(),
                new AutochannelListener(),
                new PunishmentListener(),
                new GeneralMessageListener(),
                new RoleDeleteListener(),
                new LavalinkManager(),
                new VerificationListener(),
                new SetupListener(),
                new PortalMessageListener(),
                new AllShardsLoadedListener(),
                new LogListener()
        );
    }
}
