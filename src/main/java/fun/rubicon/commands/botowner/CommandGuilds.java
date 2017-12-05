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
import static fun.rubicon.util.EmbedUtil.*;

public class CommandGuilds extends CommandHandler {
    public CommandGuilds() {
        super(new String[]{"guilds","guild"}, CommandCategory.BOT_OWNER,
                new PermissionRequirements(4, "command.guilds"),
                "Shows all Guilds the Bot is running on!", "guilds");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        StringBuilder runningOnServers = new StringBuilder();
        int count_server = 0;
        for (Guild guild : RubiconBot.getJDA().getGuilds()){
            runningOnServers.append("`\t- ").append(guild.getName()).append("(").append(guild.getId()).append(")`\n");
            count_server++;
        }
            return message(info("RubiconBot running on following guilds","`This Bot running on " + count_server +" guilds.`\n\n"+runningOnServers.toString()));
    }
}
