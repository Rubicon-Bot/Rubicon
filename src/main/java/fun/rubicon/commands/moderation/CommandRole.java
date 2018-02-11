package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;

public class CommandRole extends CommandHandler {
    public CommandRole() {
        super(new String[]{"mod-role"}, CommandCategory.MODERATION, new PermissionRequirements("command.role", false, false), "Easily add or remove roles to users", "add/remove <@User> <role>", false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.getMessage();
        String[] args = parsedCommandInvocation.getArgs();
        if (args.length < 2 || message.getMentionedUsers().isEmpty())
            return createHelpMessage();
        GuildController controller = message.getGuild().getController();
        Member member = message.getGuild().getMember(message.getMentionedUsers().get(0));

        //Get Role
        int userNameArgsLength = message.getMentionedUsers().get(0).getAsMention().split(" ").length;
        String rolename = args[userNameArgsLength + 1];

        if (member.getGuild().getRolesByName(rolename, true).isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.error("Unknown role", "That role doesn't exist").build()).build();
        Role role = member.getGuild().getRolesByName(rolename, true).get(0);
        Member issuer = message.getMember();
        if (!issuer.canInteract(role))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Not permitted", "You are not permitted to assign or remove that role").build()).build();
        if (!message.getGuild().getSelfMember().canInteract(role))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Not permitted", "I'm not permitted to assign or remove that role").build()).build();

        if (args[0].equals("add")) {
            controller.addRolesToMember(member, role).queue();
            return new MessageBuilder().setEmbed(EmbedUtil.success("Assigned role", "Successfully asigned role `" + role.getName() + "` to " + member.getAsMention()).build()).build();
        } else if (args[0].equals("remove")) {
            controller.removeRolesFromMember(member, role).queue();
            return new MessageBuilder().setEmbed(EmbedUtil.success("Removed role", "Successfully removed role `" + role.getName() + "` from " + member.getAsMention()).build()).build();
        } else
            return createHelpMessage();
    }
}
