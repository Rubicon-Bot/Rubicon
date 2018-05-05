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

        if(invocation.getArgs().length<1)
           return createHelpMessage(invocation);

        if (invocation.getArgs().length == 1 && invocation.getArgs()[0].equalsIgnoreCase("info")) {
            if (!rubiconGuild.hasAutoroleEnabled()) {
                return EmbedUtil.message(EmbedUtil.info(invocation.translate("command.autorole.info.not.title"), invocation.translate("command.autorole.info.not.description")));
            }
            String roleId = rubiconGuild.getAutorole();
            Role role = invocation.getGuild().getRoleById(roleId);
            if (role == null) {
                rubiconGuild.disableAutorole();
            }
            return EmbedUtil.message(EmbedUtil.info(invocation.translate("command.autorole.info.title"), invocation.translate("command.autorole.info.description").replace("%role%", role != null ? role.getAsMention() : "Invalid")));
        } else if (invocation.getArgs().length == 1 && invocation.getArgs()[0].equalsIgnoreCase("disable")) {
            if (!rubiconGuild.hasAutoroleEnabled()) {
                return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.autorole.disable.error.title"), invocation.translate("command.autorole.disable.error.description")));
            }
            rubiconGuild.disableAutorole();
            return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.autorole.disable.title"), invocation.translate("command.autorole.disable.description")));
        }

        if (invocation.getArgs()[0].equalsIgnoreCase("set")) {
            if (invocation.getMessage().getMentionedRoles().size() == 1) {
                Role role = invocation.getMessage().getMentionedRoles().get(0);
                if (!invocation.getMember().canInteract(role)) {
                    return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.autorole.set.error.interact.title"), invocation.translate("command.autorole.set.error.interact.description")));
                }
                if (!invocation.getGuild().getSelfMember().canInteract(role)) {
                    return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.autorole.set.error.rc.title"), invocation.translate("command.autorole.set.error.rc.description")));
                }
                rubiconGuild.setAutorole(role.getId());
                return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.autorole.set.title"), invocation.translate("command.autorole.set.description").replace("%role%", role.getAsMention())));
            }
            if (invocation.getArgs().length == 2) {
                if (invocation.getGuild().getRolesByName(invocation.getArgs()[1], true).size() == 1) {
                    Role role = invocation.getGuild().getRolesByName(invocation.getArgs()[1], true).get(0);
                    if (!invocation.getMember().canInteract(role)) {
                        return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.autorole.set.error.interact.title"), invocation.translate("command.autorole.set.error.interact.description")));
                    }
                    if (!invocation.getGuild().getSelfMember().canInteract(role)) {
                        return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.autorole.set.error.rc.title"), invocation.translate("command.autorole.set.error.rc.description")));
                    }
                    rubiconGuild.setAutorole(role.getId());
                    return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.autorole.set.title"), invocation.translate("command.autorole.set.description").replace("%role%", role.getAsMention())));
                } else
                    return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.autorole.set.nf.title"), invocation.translate("command.autorole.set.nf.description")));
            }
        }
        return createHelpMessage();
    }
}
