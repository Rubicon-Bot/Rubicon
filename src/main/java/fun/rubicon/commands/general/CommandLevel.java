/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.sql.MemberSQL;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

@Deprecated
public class CommandLevel extends CommandHandler {


    @Deprecated
    public CommandLevel() {
        super(new String[]{"level", "lvl"}, CommandCategory.FUN, new PermissionRequirements(PermissionLevel.EVERYONE, "command.level"), "Get your level, points and ruby's.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        MemberSQL memberSQL = new MemberSQL(parsedCommandInvocation.getMember());
        User user = parsedCommandInvocation.getMessage().getAuthor();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_PRIMARY);
        builder.setAuthor(user.getName(), null, user.getAvatarUrl());
        builder.addField("Points", memberSQL.get("points"), true);
        builder.addField("Level", memberSQL.get("level"), true);
        builder.addField("Ruby's", memberSQL.get("money"), true);
        return EmbedUtil.message(builder);
    }
}
