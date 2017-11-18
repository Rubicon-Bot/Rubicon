package fun.rubicon.commands.admin;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandPermission extends Command {

    public CommandPermission(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        Role role;
        Member member;
        if(e.getMessage().getMentionedUsers().size() == 1) {
            member = e.getGuild().getMember(e.getMessage().getMentionedUsers().get(0));
        }
    }

    @Override
    public String getDescription() {
        return "Manages permissions of a user or a role.";
    }

    @Override
    public String getUsage() {
        return "permission <@User/@Role> <add/remove/list> <command>";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }
}
