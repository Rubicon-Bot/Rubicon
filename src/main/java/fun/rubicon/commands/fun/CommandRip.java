/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static fun.rubicon.util.EmbedUtil.error;
import static fun.rubicon.util.EmbedUtil.message;

/**
 * Handles the 'rip' command which fetches tombstone images with a custom sign.
 *
 * @author DRSchlaubi, tr808axm
 */
public class CommandRip extends CommandHandler {
    /**
     * Constructs the 'rip' command handler.
     */
    public CommandRip() {
        super(new String[]{"rip", "rest-in-peace", "tombstone"}, CommandCategory.FUN,
                new PermissionRequirements("command.rip", false, true),
                "Creates a tombstone with custom text.", "<who-died> <sign text...>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length < 2)
            return createHelpMessage(invocation);
        else {
            String whoDied = invocation.getArgs()[0];
            StringBuilder sign = new StringBuilder(invocation.getArgs()[1]);
            for (int i = 2; i < invocation.getArgs().length; i++)
                sign.append(' ').append(invocation.getArgs()[i]);

            // compile url
            String tombstoneURL;
            try {
                tombstoneURL = "http://www.tombstonebuilder.com/generate.php"
                        + "?top1=R.I.P."
                        + "&top2=" + URLEncoder.encode(whoDied, StandardCharsets.UTF_8.toString())
                        + "&top3=" + URLEncoder.encode(sign.substring(0, Math.min(25, sign.length())), StandardCharsets.UTF_8.toString())
                        + "&top4=" + (sign.length() < 25 ? "" : URLEncoder.encode(sign.substring(25, Math.min(50, sign.length())), StandardCharsets.UTF_8.toString()))
                        + "&sp=";
            } catch (UnsupportedEncodingException e) {
                // should not occur
                return message(error());
            }

            // respond
            invocation.getMessage().getTextChannel().sendMessage(tombstoneURL).queue();
            return null;
        }
    }
}