/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;

import static fun.rubicon.util.EmbedUtil.info;
import static fun.rubicon.util.EmbedUtil.message;

public class CommandGuilds extends CommandHandler {
    public CommandGuilds() {
        super(new String[]{"guilds"}, CommandCategory.BOT_OWNER,
                new PermissionRequirements("command.guilds", true, false),
                "Shows all Guilds the Bot is running on!", "guilds");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        StringBuilder runningOnServers = new StringBuilder();
        int count_server = 1;

        List<Guild> guild_sublist;
        int SideNumbInput = 1;
        if (parsedCommandInvocation.getArgs().length > 0) {
            SideNumbInput = Integer.parseInt(parsedCommandInvocation.getArgs()[0]);
            System.out.println(SideNumbInput);
        }

        if (RubiconBot.getJDA().getGuilds().size() > 20) {
            guild_sublist = RubiconBot.getJDA().getGuilds().subList((SideNumbInput - 1) * 20, (SideNumbInput - 1) * 20 + 20);
        } else {
            guild_sublist = RubiconBot.getJDA().getGuilds();
        }


        int sideNumbAll;
        if (RubiconBot.getJDA().getGuilds().size() >= 20) {
            for (Guild guild : guild_sublist) {
                runningOnServers.append("`\t " + (((SideNumbInput - 1) * 20) + count_server) + ". ").append(guild.getName()).append("(").append(guild.getId()).append(")`\n");
                count_server++;
            }
            sideNumbAll = RubiconBot.getJDA().getGuilds().size() / 20;
        } else {
            for (Guild guild : guild_sublist) {
                runningOnServers.append("`\t " + count_server + ". ").append(guild.getName()).append("(").append(guild.getId()).append(")`\n");
                count_server++;
            }
            sideNumbAll = 1;
        }
        int sideNumb = SideNumbInput;
        return message(info("RubiconBot running on following guilds", "`Total guilds: " + RubiconBot.getJDA().getGuilds().size() + " - Side " + sideNumb + " / " + sideNumbAll + "`\n\n" + runningOnServers.toString()));
    }
}
