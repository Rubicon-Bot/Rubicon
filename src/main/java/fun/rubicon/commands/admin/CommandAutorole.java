package fun.rubicon.commands.admin;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandAutorole extends CommandHandler {

    public CommandAutorole() {
        super(new String[]{"autorole"}, CommandCategory.ADMIN, new PermissionRequirements("autorole", false, false), "Automatically adds a role to new users.", "set <@role>\ndisable\ninfo");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());

        if(invocation.getArgs().length == 0 || invocation.getArgs().length == 1 && invocation.getArgs()[0].equalsIgnoreCase("info")) {
            if(!rubiconGuild.hasAutoroleEnabled()) {
                return EmbedUtil.message(EmbedUtil.info("Not enabled.", "Autorole is not enabled."));
            }
            long roleId = rubiconGuild.getAutorole();
            Role role = invocation.getGuild().getRoleById(roleId);
            if(role == null) {
                rubiconGuild.disableAutorole();
            }
            return EmbedUtil.message(EmbedUtil.info("Autorole", "Current autorole is " + (role != null ? role.getAsMention() : "Invalid")));
        } else if(invocation.getArgs().length == 1 && invocation.getArgs()[0].equalsIgnoreCase("disable")) {
            if(!rubiconGuild.hasAutoroleEnabled()) {
                return EmbedUtil.message(EmbedUtil.error("Can't disable autorole!", "Autorole is not enabled."));
            }
            rubiconGuild.disableAutorole();
            return EmbedUtil.message(EmbedUtil.success("Disabled Autorole!", "Successfully disabled autorole."));
        }

        if(invocation.getArgs()[0].equalsIgnoreCase("set")) {
            if(invocation.getMessage().getMentionedRoles().size() == 1) {
                Role role = invocation.getMessage().getMentionedRoles().get(0);
                if(!invocation.getMember().canInteract(role)) {
                    return EmbedUtil.message(EmbedUtil.error("No permissions!", "The role is higher than your highest role."));
                }
                if(!invocation.getGuild().getSelfMember().canInteract(role)) {
                    return EmbedUtil.message(EmbedUtil.error("No permissions!", "I have no permissions to add this role to a member."));
                }
                rubiconGuild.setAutorole(role.getIdLong());
                return EmbedUtil.message(EmbedUtil.success("Enabled Autorole!", String.format("Autorole is now %s", role.getAsMention())));
            }
            if(invocation.getArgs().length == 2) {
                if(invocation.getGuild().getRolesByName(invocation.getArgs()[1], true).size() == 1) {
                    Role role = invocation.getGuild().getRolesByName(invocation.getArgs()[1], true).get(0);
                    if(!invocation.getMember().canInteract(role)) {
                        return EmbedUtil.message(EmbedUtil.error("No permissions!", "The role is higher than your highest role."));
                    }
                    if(!invocation.getGuild().getSelfMember().canInteract(role)) {
                        return EmbedUtil.message(EmbedUtil.error("No permissions!", "I have no permissions to add this role to a member."));
                    }
                    rubiconGuild.setAutorole(role.getIdLong());
                    return EmbedUtil.message(EmbedUtil.success("Enabled Autorole!", String.format("Autorole is now %s", role.getAsMention())));
                } else
                    return EmbedUtil.message(EmbedUtil.error("Role not found!", "There are either too many or no role with that name."));
            }
        }
        return createHelpMessage();
    }
}
