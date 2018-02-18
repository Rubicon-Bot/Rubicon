/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
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
                new PermissionRequirements("command.broadcast", true, false),
                "Sends a message to all guild owners.", "<message>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        if (args.length < 3) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("You have to use more arguments!", "Use at least 3 arguments.").build()).build();
        }
        String ownerMessage = parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation(), "");
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("Broadcast - Awaiting Confirmation", null, parsedCommandInvocation.getMessage().getJDA().getSelfUser().getEffectiveAvatarUrl());
        embedBuilder.setDescription(ownerMessage);
        embedBuilder.setColor(Colors.COLOR_PRIMARY);
        Message confirmMessage = parsedCommandInvocation.getMessage().getTextChannel().sendMessage(embedBuilder.build()).complete();
        confirmMessage.addReaction("✅").queue();
        awaitingConfirm.put(confirmMessage.getIdLong(), ownerMessage);
        return null;
    }

    public static void handleReaction(MessageReactionAddEvent e) {
        if (awaitingConfirm.containsKey(e.getMessageIdLong())) {
            if (e.getReactionEmote().getName().equals("✅") && e.getUser() != e.getJDA().getSelfUser()) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setAuthor("Message from developers!", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl());
                embedBuilder.setDescription(awaitingConfirm.get(e.getMessageIdLong()));
                embedBuilder.setColor(Colors.COLOR_ERROR);
                List<Long> sentOwners = new ArrayList<>();
                for (Guild guild : e.getJDA().getGuilds()) {
                    if (!sentOwners.contains(guild.getOwner().getUser().getIdLong())) {
                        guild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(embedBuilder.build()).queue());
                        sentOwners.add(guild.getOwner().getUser().getIdLong());
                    }
                }
            }
            awaitingConfirm.remove(e.getMessageIdLong());
            e.getTextChannel().deleteMessageById(e.getMessageId()).queue();
        }
    }
}
