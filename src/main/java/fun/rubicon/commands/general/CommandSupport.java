package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Invite;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class CommandSupport extends CommandHandler {

    public CommandSupport() {
        super(new String[]{"support"}, CommandCategory.GENERAL, new PermissionRequirements("support", false, true), "Notifies a Rubicon Supporter who helps you.", "<Reason>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length < 1)
            return createHelpMessage(invocation);
        String reason = invocation.getArgsString();
        Invite i = null;
        if(!RubiconBot.getConfiguration().has("supporthook"))
            return null;
        if (invocation.getGuild().getMember(RubiconBot.getSelfUser()).hasPermission(invocation.getTextChannel(), Permission.CREATE_INSTANT_INVITE))
            i = invocation.getTextChannel().createInvite().complete();
        WebhookClientBuilder builder = new WebhookClientBuilder(RubiconBot.getConfiguration().getString("supporthook"));
        WebhookClient client = builder.build();
        String invite = i == null ? "No invite could be Provided" : i.getURL();
        WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder()
                .setAvatarUrl(RubiconBot.getSelfUser().getAvatarUrl())
                .setUsername("Support Request Boi")
                .append("<@&" + Info.SUPPORT_ROLE + ">")
                .addEmbeds(info("New Support Request from " + invocation.getAuthor().getName() + "#" + invocation.getAuthor().getDiscriminator(), "Guild Name: " + invocation.getGuild().getName() + "\nGuild Owner: " + invocation.getGuild().getOwner().getUser().getName() + "#" + invocation.getGuild().getOwner().getUser().getDiscriminator() + "\nGuild Invite: " + invite + "\n**Reason:** " + reason + "\n" + CommandPermissionCheck.buildPermssionMessage(invocation)).build());
        client.send(messageBuilder.build());
        client.close();
        return message(success(invocation.translate("command.support"), invocation.translate("command.support.description")));
    }

}