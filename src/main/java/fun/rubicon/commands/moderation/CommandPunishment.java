package fun.rubicon.commands.moderation;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.setup.SetupRequest;
import fun.rubicon.util.Colors;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class CommandPunishment extends CommandHandler {

    private Rethink rethink = RubiconBot.getRethink();
    public CommandPunishment() {
        super(new String[] {"punishment", "punish"}, CommandCategory.MODERATION, new PermissionRequirements("punishment", false, false), "Easy punishment system based on Rubicon's mod tools", "<@User> <reason>\n settings log channel/embed/disable \n settings log embed add\remove");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        Message message = invocation.getMessage();
        RubiconGuild guild = RubiconGuild.fromGuild(invocation.getGuild());
        if(args.length == 0)
            return createHelpMessage();
        switch (args[0]){
            case "settings":
                if(args.length < 2)
                    return createHelpMessage();
                if(!userPermissions.hasPermissionNode("command.punishment.settings"))
                    return message(error());
                switch (args[1]){
                    case "log":
                        switch (args[2]){
                            case "channel":
                                if(message.getMentionedChannels().isEmpty())
                                    return message(error(invocation.translate("command.punishment.nochannel.title"), invocation.translate("command.punishment.nochannel.description")));
                                TextChannel channel = message.getMentionedChannels().get(0);
                                if(!invocation.getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS))
                                    return message(error(invocation.translate("command.punishment.noperm.title"), invocation.translate("command.punishment.noperm.description")));
                                guild.setPunishmentLoggingChannel(channel);
                                SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.punishment.setlogchannel.title"), invocation.translate("command.punishment.setlogchannel.description"))));
                                break;
                            case "disable":
                                if(!guild.usePunishmentLogs())
                                    return message(error(invocation.translate("command.punishment.alreadydisabled.title"), invocation.translate("command.punishment.alreadydisabled.description")));
                                guild.disablePunishmentLogging();
                                SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.punishment.disabledlog.title"), invocation.translate("command.punishment.disabledlog.description"))));
                                break;
                            case "embed":
                                Message msg = SafeMessage.sendMessageBlocking(invocation.getTextChannel(), message(info(invocation.translate("punishment.embed.step1.info.title"), invocation.translate("punishment.embed.step2.info.description"))));
                                new PunishmentLogSetupRequest(msg, invocation.getMember());
                                break;
                        }
                        break;
                }
                break;
            default:
                SafeMessage.sendMessage(invocation.getTextChannel(), createHelpMessage());
                break;
        }
        return null;
    }

    private class PunishmentLogSetupRequest extends SetupRequest{

        private PunishmentLogEmbed embed;

        private PunishmentLogSetupRequest(Message msg, Member author){
            this.infoMessage = msg;
            this.author = author;
            this.setupChannel = msg.getTextChannel();
            this.embed = new PunishmentLogEmbed();
            this.guild = msg.getGuild();
            register(this);
        }

        @Override
        public void next(Message invokeMsg) {
            switch (step){
                case 0:
                    embed.title = invokeMsg.getContentDisplay();
                    infoMessage.editMessage(setupMessage(translate("punishment.embed.step2.info.title"), translate("punishment.embed.step2.info.description"), Colors.COLOR_SECONDARY).build()).queue();
                    break;
                case 1:
                    embed.description = invokeMsg.getContentDisplay();
                    embed.title = invokeMsg.getContentDisplay();
                    infoMessage.editMessage(setupMessage(translate("punishment.embed.step3.info.title"), translate("punishment.embed.step3.info.description"), Colors.COLOR_SECONDARY).build()).queue();
                    break;
                case 2:
                    embed.footer = invokeMsg.getContentDisplay();
                    infoMessage.editMessage(success(translate("punishment.embed.finish.info.title"), translate("punishment.embed.finish.info.description")).build()).queue();
                    finish();
                    break;
            }
            update();
        }

        @Override
        public void abort() {

        }

        private void finish(){
            unregister();
            rethink.db.table("guilds").filter(rethink.rethinkDB.hashMap("guildId", setupChannel.getGuild().getId())).update(rethink.rethinkDB.hashMap("logembed", rethink.rethinkDB.hashMap("title", embed.title).with("description", embed.description).with("footer", embed.footer))).run(rethink.getConnection());
        }
    }

    public class PunishmentLogEmbed{
        private String title;
        private String description;
        private String footer;

    }
}
