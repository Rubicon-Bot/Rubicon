/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandShardManage extends CommandHandler {

    /*
     * No translation because its a bot owner command.
     */

    public CommandShardManage() {
        super(new String[]{"shardmanage", "smanage", "shardinfo", "sinfo"}, CommandCategory.BOT_OWNER, new PermissionRequirements("shardmanage", true, false), "Shows information or options to start/stop/restart about a specific shard or.",
                "| Info about current shard.\n" +
                        "[shardid] | Shows info about a specific shard.\n" +
                        "[shardId] stop | Stops a shard.\n" +
                        "[shardid] restart/rs | Restarts a shard.");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length == 0) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Shardinfo - Overview");
            embedBuilder.setColor(Colors.COLOR_SECONDARY);
            embedBuilder.setDescription("Total Shards: " + RubiconBot.getShardManager().getShardsTotal() + "\nRunning Shards: " + RubiconBot.getShardManager().getShardsRunning() + "\nAverage Ping: " + ((int) RubiconBot.getShardManager().getAveragePing()) + "ms.");
            for (JDA jda : RubiconBot.getShardManager().getShards()) {
                embedBuilder.addField("ShardId " + jda.getShardInfo().getShardId() + "/" + RubiconBot.getMaximumShardCount(),
                        "Status: " + jda.getStatus() + "\n" +
                                "Ping: " + ((int) jda.getPing()) + "ms", false);
            }
            SafeMessage.sendMessage(invocation.getTextChannel(), embedBuilder.build(), 300);

        } else if (invocation.getArgs().length == 1) {
            if (!StringUtil.isNumeric(invocation.getArgs()[0])) {
                return EmbedUtil.message(EmbedUtil.error("Wrong argument!", "Parameter must be numeric."));
            }
            int shardId = Integer.parseInt(invocation.getArgs()[0]);
            if (shardId == RubiconBot.getMaximumShardCount()) {
                return EmbedUtil.message(EmbedUtil.error("Invalid shardId!", "There are only " + RubiconBot.getMaximumShardCount() + " shards.").setFooter("ShardId is 0-index-based", null));
            }
            JDA shard = RubiconBot.getShardManager().getShardById(shardId);
            Map userStatusCount = getStatusMembers(shard);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Colors.COLOR_SECONDARY);
            embedBuilder.setTitle("Shardinfo - " + shard.getShardInfo().getShardId() + "/" + RubiconBot.getMaximumShardCount());
            embedBuilder.setDescription("ID: " + shard.getShardInfo().getShardId() + "\nStatus: " + shard.getStatus());
            embedBuilder.addField("Ping", ((int) shard.getPing()) + "ms", true);
            embedBuilder.addField("Guilds", shard.getGuilds().size() + " Guilds", true);
            embedBuilder.addBlankField(true);
            embedBuilder.addField("Users", String.format("Online: %d\nIdle: %d\nDnD: %d\nOffline: %d\nTotal: %d", userStatusCount.get("online"), userStatusCount.get("idle"), userStatusCount.get("dnd"), userStatusCount.get("offline"), userStatusCount.get("total")), true);
            embedBuilder.addField("Channels", String.format("Categories: %d\nTextchannels: %d\nVoicechannels: %d", shard.getCategories().size(), shard.getTextChannels().size(), shard.getVoiceChannels().size()), true);
            embedBuilder.addBlankField(true);
            SafeMessage.sendMessage(invocation.getTextChannel(), embedBuilder.build(), 300);
        } else if (invocation.getArgs().length == 2) {
            if (!StringUtil.isNumeric(invocation.getArgs()[0])) {
                return EmbedUtil.message(EmbedUtil.error("Wrong argument!", "Parameter must be numeric."));
            }
            int shardId = Integer.parseInt(invocation.getArgs()[0]);
            if (shardId == RubiconBot.getMaximumShardCount()) {
                return EmbedUtil.message(EmbedUtil.error("Wrong shardId!", "There are only " + RubiconBot.getMaximumShardCount() + " shards.").setFooter("ShardId is 0-index-based", null));
            }

            JDA shard = RubiconBot.getShardManager().getShardById(shardId);
            if (shard == null) {
                return EmbedUtil.message(EmbedUtil.error("Invalid shardId!", "Please use a correct shardId."));
            }
            switch (invocation.getArgs()[1]) {
                case "rs":
                case "restart":
                    if (shard.getStatus() != JDA.Status.SHUTDOWN && shard.getStatus() != JDA.Status.CONNECTED)
                        return EmbedUtil.message(EmbedUtil.error("Can't restart shard!", "Shard is already starting/started."));
                    SafeMessage.sendMessageBlocking(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success("Restarting shard!", "Shard will be restarted soon.")));
                    RubiconBot.getShardManager().restart(shardId);
                    return null;
                case "stop":
                    if (shard.getStatus() != JDA.Status.CONNECTED)
                        return EmbedUtil.message(EmbedUtil.error("Can't stop shard!", "Shard is not running."));
                    SafeMessage.sendMessageBlocking(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success("Stopping shard!", "Shard will be stopped soon.")));
                    shard.shutdown();
                    return null;
                default:
                    return createHelpMessage();
            }
        }
        return null;
    }

    private Map getStatusMembers(JDA jda) {
        List<Long> filtered = new ArrayList<>();
        int onlineCount = 0;
        int idleCount = 0;
        int dndCount = 0;
        int other = 0;

        for (Guild guild : jda.getGuilds()) {
            for (Member member : guild.getMembers()) {
                if (filtered.contains(member.getUser().getIdLong())) continue;
                if (member.getOnlineStatus().equals(OnlineStatus.ONLINE))
                    onlineCount++;
                else if (member.getOnlineStatus().equals(OnlineStatus.IDLE))
                    idleCount++;
                else if (member.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB))
                    dndCount++;
                else
                    other++;
                filtered.add(member.getUser().getIdLong());
            }
        }
        Map<String, Integer> counts = new HashMap<>();
        counts.put("online", onlineCount);
        counts.put("idle", idleCount);
        counts.put("dnd", dndCount);
        counts.put("offline", jda.getUsers().size() - other);
        counts.put("total", jda.getUsers().size());
        return counts;
    }
}
