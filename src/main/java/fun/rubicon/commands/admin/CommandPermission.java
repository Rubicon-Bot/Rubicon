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
        Role role = null;
        Member member = null;
        if(e.getMessage().getMentionedUsers().size() == 1) {
            member = e.getGuild().getMember(e.getMessage().getMentionedUsers().get(0));
        }
        if(e.getMessage().getMentionedRoles().size() == 1) {
            role = e.getMessage().getMentionedRoles().get(0);
        }

        if(member == null && role == null) {
            sendErrorMessage("You have to mention one user or role!");
            return;
        }

        if(member != null) {

        }
    }

    @Override
    public String getDescription() {
        return "Manages permissions of a user or a role.";
    }

    @Override
    public String getUsage() {
        return "permission <@User/@Role> <add/remove> <command>\n" +
                "permission <@User/@Role> <list>";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }
}
