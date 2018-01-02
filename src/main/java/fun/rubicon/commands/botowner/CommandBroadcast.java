/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandBroadcast extends CommandHandler {

    public static Map<Long, String> awaitingConfirm = new HashMap<>();

    public CommandBroadcast() {
        super(new String[]{"broadcast"}, CommandCategory.BOT_OWNER,
                new PermissionRequirements(4, "command.broadcast"),
                "Sends a message to all guild owners.", "<message>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.args;
        if (args.length < 3) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("You have to use more arguments!", "Use at least 3 arguments.").build()).build();
        }
        String ownerMessage = "";
        for(String s : args)
            ownerMessage += s + " ";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("Broadcast - Awaiting Confirmation", null, parsedCommandInvocation.invocationMessage.getJDA().getSelfUser().getEffectiveAvatarUrl());
        embedBuilder.setDescription(ownerMessage);
        embedBuilder.setColor(Colors.COLOR_PRIMARY);
        Message confirmMessage = parsedCommandInvocation.invocationMessage.getTextChannel().sendMessage(embedBuilder.build()).complete();
        confirmMessage.addReaction("✅").queue();
        awaitingConfirm.put(confirmMessage.getIdLong(), ownerMessage);
        return null;
    }

    public static void handleReaction(MessageReactionAddEvent e) {
        if(awaitingConfirm.containsKey(e.getMessageIdLong())) {
            List<Long> guildSent = new ArrayList<>();
            if(e.getReactionEmote().getName().equals("✅") && e.getUser() != e.getJDA().getSelfUser()) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setAuthor("Message from developers!", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl());
                embedBuilder.setDescription(awaitingConfirm.get(e.getMessageIdLong()));
                embedBuilder.setColor(Colors.COLOR_ERROR);
                for(Guild guild : e.getJDA().getGuilds()) {
                    if(!guildSent.contains(guild.getIdLong())) {
                        guild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(embedBuilder.build()).queue());
                        guildSent.add(guild.getIdLong());
                    }
                }
                awaitingConfirm.remove(e.getMessageIdLong());
                e.getTextChannel().deleteMessageById(e.getMessageId()).queue();
            }
        }
    }
}
