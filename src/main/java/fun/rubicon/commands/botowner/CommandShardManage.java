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
import jdk.nashorn.internal.scripts.JD;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;

import java.util.stream.Collectors;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandShardManage extends CommandHandler {

    /*
     * No translation because its a bot owner command.
     */

    public CommandShardManage() {
        super(new String[]{"shardmanage", "smanage"}, CommandCategory.BOT_OWNER, new PermissionRequirements("shardmanage", true, false), "Shows information or options to start/stop/restart about a specific shard or.",
                "| Info about current shard.\n" +
                        "[shardid] | Shows info about a specific shard.\n" +
                        "[shardid] start/s | Starts a shard.\n" +
                        "[shardid] restart/rs | Restarts a shard." +
                        "[shardid] stop | Stops a shard.");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length == 0) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Shardinfo - Overview");
            embedBuilder.setColor(Colors.COLOR_SECONDARY);
            embedBuilder.setDescription("Loaded " + RubiconBot.getMaximumShardCount() + " Shards.\nAverage Ping: " + RubiconBot.getShardManager().getAveragePing() + "ms");
            for (JDA jda : RubiconBot.getShardManager().getShards()) {
                embedBuilder.addField("Shard " + jda.getShardInfo().getShardId() + "/" + RubiconBot.getMaximumShardCount(),
                        "Status: " + jda.getStatus() + "\n" +
                                "Ping: " + jda.getPing() + "ms", false);
            }
            SafeMessage.sendMessage(invocation.getTextChannel(), embedBuilder.build(), 300);

        } else if (invocation.getArgs().length == 1) {
            if (!StringUtil.isNumeric(invocation.getArgs()[0])) {
                return EmbedUtil.message(EmbedUtil.error("Wrong argument!", "Parameter must be numeric."));
            }
            int shardId = Integer.parseInt(invocation.getArgs()[0]);
            if (shardId == RubiconBot.getMaximumShardCount()) {
                return EmbedUtil.message(EmbedUtil.error("Wrong shardId!", "There are only " + RubiconBot.getMaximumShardCount() + " shards.").setFooter("ShardId is 0-index-based", null));
            }
            JDA shard = RubiconBot.getShardManager().getShardById(shardId);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Colors.COLOR_SECONDARY);
            embedBuilder.setTitle("Shardinfo - " + shard.getShardInfo().getShardId() + "/" + RubiconBot.getMaximumShardCount());
            embedBuilder.setDescription("ID: " + shard.getShardInfo().getShardId());
            embedBuilder.addField("Ping", shard.getPing() + "ms", false);
            embedBuilder.addField("Guilds", shard.getGuilds().size() + " Guilds", true);
            embedBuilder.addField("Users", shard.getUsers().size() + " Users", true);
            embedBuilder.addField("Channels", (shard.getTextChannels().size() + shard.getVoiceChannels().size()) + " Channels", true);
            SafeMessage.sendMessage(invocation.getTextChannel(), embedBuilder.build(), 300);
        } else if(invocation.getArgs().length == 2) {
            if (!StringUtil.isNumeric(invocation.getArgs()[0])) {
                return EmbedUtil.message(EmbedUtil.error("Wrong argument!", "Parameter must be numeric."));
            }
            int shardId = Integer.parseInt(invocation.getArgs()[0]);
            if (shardId == RubiconBot.getMaximumShardCount()) {
                return EmbedUtil.message(EmbedUtil.error("Wrong shardId!", "There are only " + RubiconBot.getMaximumShardCount() + " shards.").setFooter("ShardId is 0-index-based", null));
            }
            JDA shard = RubiconBot.getShardManager().getShardById(shardId);

            switch (invocation.getArgs()[1]) {
                case "s":
                case "start":
                    break;
                default:
                    return createHelpMessage();
            }
        }
        return null;
    }
}
