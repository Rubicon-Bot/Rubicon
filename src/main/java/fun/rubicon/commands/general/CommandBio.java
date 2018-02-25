/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandBio extends CommandHandler {

    public CommandBio() {
        super(new String[]{"bio"}, CommandCategory.GENERAL, new PermissionRequirements("bio", false, true), "Describe yourself in some sentences.", "| Shows your current bio.\nset <text> | Updates your bio.");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconMember member = RubiconMember.fromMember(invocation.getMember());

        if(invocation.getArgs().length == 0) {
            return EmbedUtil.message(EmbedUtil.info(invocation.translate("command.bio.current.title"), member.getBio()));
        }

        if(invocation.getArgs()[0].equalsIgnoreCase("set")) {
            String bioText = invocation.getArgsString().replace("set ", "");
            if (bioText.toCharArray().length > 280) {
                return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.bio.set.error.title"), invocation.translate("command.bio.set.error.description").replaceAll("%count%", "280")));
            }
            bioText = filterWords(bioText);
            member.setBio(bioText);
            return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.bio.set.title") + "!", bioText));
        }
        return createHelpMessage();
    }

    private String filterWords(String text) {
        String[] blacklist = {"penis", "dick", "cock", "cunt", "pussy", "nigga", "nigger", "trump", "porno", "porn"};
        for (String word : blacklist) {
            text = text.replace(word.toLowerCase(), "*****");
        }
        return text;
    }
}
