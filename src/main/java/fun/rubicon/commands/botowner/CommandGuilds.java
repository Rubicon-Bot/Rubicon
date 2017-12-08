/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.managers.fields.GuildField;

import java.util.List;

import static fun.rubicon.util.EmbedUtil.*;

public class CommandGuilds extends CommandHandler {
    public CommandGuilds() {
        super(new String[]{"guilds","guild"}, CommandCategory.BOT_OWNER,
                new PermissionRequirements(4, "command.guilds"),
                "Shows all Guilds the Bot is running on!", "guilds");
    }

    @Override
    //FEHLER SEITENZALEN STIMMEN NOCH NICHT !
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        StringBuilder runningOnServers = new StringBuilder();
        int count_server = 1;

        List<Guild> guild_sublist;
        int SideNumbInput = 1;
        if (parsedCommandInvocation.args.length > 1) {
            SideNumbInput = Integer.parseInt(parsedCommandInvocation.args[1]);
        }

        if(RubiconBot.getJDA().getGuilds().size() > 20){
            guild_sublist = RubiconBot.getJDA().getGuilds().subList((SideNumbInput-1)*20, (SideNumbInput-1)*20+20);
        } else {
            guild_sublist = RubiconBot.getJDA().getGuilds();
        }
        for (Guild guild : guild_sublist) {
            runningOnServers.append("`\t " + count_server +". ").append(guild.getName()).append("(").append(guild.getId()).append(")`\n");
            count_server++;
        }
        int sideNumbAll;
        if (RubiconBot.getJDA().getGuilds().size() >= 2) sideNumbAll = RubiconBot.getJDA().getGuilds().size() / 20;
        else sideNumbAll = 1;
        int sideNumb = SideNumbInput;
            return message(info("RubiconBot running on following guilds","`Total guilds: " + RubiconBot.getJDA().getGuilds().size() + " Side " + sideNumb + " / " + sideNumbAll +"`\n\n"+runningOnServers.toString()));

    }
}
