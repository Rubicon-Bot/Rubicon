package fun.rubicon.features.verification;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class VerificationCommandHandler extends CommandHandler {

    public static List<Guild> guildList = new ArrayList<>();

    public VerificationCommandHandler() {
        super(new String[]{"verification", "verify"}, CommandCategory.MODERATION, new PermissionRequirements("verification", false, false), "Super cool reaction based verification system.", "setup\ndisable");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());
        if (args.length == 0)
            return createHelpMessage();
        switch (args[0]) {
            case "setup":
                if (guildList.contains(invocation.getGuild()))
                    return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.verification.alreadyrunning.title"), invocation.translate("command.verification.alreadyrunning.description")));
                if (rubiconGuild.isVerificationEnabled())
                    return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.verification.alreadyenabled.title"), invocation.translate("command.verification.alreadyenabled.description")));
                VerificationSetupRequest.createVerificationSetupRequest(invocation);
                break;
            case "disable":
                if (!rubiconGuild.isVerificationEnabled())
                    return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.verification.notenabled.title"), invocation.translate("command.verification.notenabled.description")));
                rubiconGuild.disableVerification();
                SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success(invocation.translate("command.verification.disabled.title"), invocation.translate("command.verification.disabled.description"))), 5);
                break;
            default:
                SafeMessage.sendMessage(invocation.getTextChannel(), createHelpMessage(), 5);
        }
        return null;
    }

}
