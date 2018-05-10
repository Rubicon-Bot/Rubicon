package fun.rubicon.commands.tools;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import javax.xml.ws.http.HTTPException;

public class CommandShort extends CommandHandler {
    public CommandShort() {
        super(new String[]{"short", "shortn"}, CommandCategory.TOOLS, new PermissionRequirements("short", false, true), "Short links easily with rucb.co", "<longurl>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        if (args.length == 0)
            return createHelpMessage();

        String shortURL;
        try {
            shortURL = RubiconBot.getBitlyAPI().shortURL(args[0]);
        } catch (IllegalArgumentException e) {
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.short.invalidargument.title"), invocation.translate("command.short.invalidargument.description")).build()).build();
        } catch (HTTPException e) {
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.short.httperror.title"), invocation.translate("command.short.httperror.description")).build()).build();
        } catch (RuntimeException e) {
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.short.apierror.title"), invocation.translate("command.short.apierror.description")).build()).build();
        }

        return new MessageBuilder().setEmbed(EmbedUtil.success(invocation.translate("command.short.success.title"), String.format(invocation.translate("command.short.success.description"), shortURL)).build()).build();
    }
}
