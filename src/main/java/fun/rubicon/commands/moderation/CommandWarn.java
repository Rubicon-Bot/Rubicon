package fun.rubicon.commands.moderation;

import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.PunishmentType;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.setup.SetupRequest;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;
import java.util.Map;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class CommandWarn extends CommandHandler {
    public CommandWarn() {
        super(new String[] {"warn", "warns"}, CommandCategory.MODERATION, new PermissionRequirements("warn", false,false), "Easily warn users", "<@User> <reason>\nlist <@User>\n <@User> <index> (Use rc!warn list to get warns of a user.)\npunishment (Setup punishments for an speciefied amount of warns");
    }

    private Rethink rethink = RubiconBot.getRethink();
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        if(args.length == 0)
            return createHelpMessage();
        switch (args[0]){
            case "remove":
            case "unwarn":
                break;
            case "list":
                break;
            case "punishment":
            case "punishments":
                if(args.length < 2)
                    return EmbedUtil.message(EmbedUtil.error("Usage", "rc!warns punishments add\nrc!warns punishments remove <id> \nrc!warns punishments list"));
                switch (args[1]){
                    case "add":
                    case "create":
                        createRequest(invocation);
                        break;
                    case "list":
                        SafeMessage.sendMessage(invocation.getTextChannel(), getPunishmentList(invocation).build());
                        break;
                    case "delete":
                    case "remove":
                        if(args.length == 2)
                            return EmbedUtil.message(EmbedUtil.error("Usage", "rc!warns punishments add\nrc!warns punishments remove <id> \nrc!warns punishments list"));
                        deletePunishment(invocation);
                        break;
                }
                break;
        }
        return null;
    }

    private boolean doesPunishmentExists(String id){
        Cursor cursor = rethink.db.table("warn_punishments").filter(rethink.rethinkDB.hashMap("id", id)).run(rethink.connection);
        return !cursor.toList().isEmpty();
    }

    private void deletePunishment(CommandManager.ParsedCommandInvocation invocation) {
        String id = invocation.getArgs()[2];
        if(!doesPunishmentExists(id)){
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.warn.punishment.notfound.title"), invocation.translate("command.warn.punishment.notfound.description"))), 5);
            return;
        }
        rethink.db.table("warn_punishments").filter(rethink.rethinkDB.hashMap("id", id)).delete().run(rethink.connection);
        SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success(invocation.translate("command.warn.punishment.deleted.title"), invocation.translate("command.warn.punishment.deleted.description"))), 5);
    }

    private void createRequest(CommandManager.ParsedCommandInvocation invocation){
        Message msg = SafeMessage.sendMessageBlocking(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.info(invocation.translate("warns.setup.step0.info.title"), invocation.translate("warns.setup.step0.info.description"))));
        new WarnPunishmentSetupRequest(msg, invocation.getMember());
    }

    private EmbedBuilder getPunishmentList(CommandManager.ParsedCommandInvocation invocation){
        Cursor cursor = rethink.db.table("warn_punishments").filter(rethink.rethinkDB.hashMap("guildId", invocation.getGuild().getId())).run(rethink.connection);
        List list = cursor.toList();
        if(list.isEmpty())
            return new EmbedBuilder().setTitle("No punishments").setDescription("You haven't configured any warn punishments").setColor(Colors.COLOR_ERROR);
        StringBuilder punishmentsString = new StringBuilder();
        for(Object obj : list){
            Map map = (Map) obj;
            punishmentsString.append("**ID**: `").append((String) map.get("id")).append("`").append("\n").append("**Minimum warns**:").append((String) map.get("amount")).append("\n").append("**Message**:").append(((String) map.get("message"))).append("\n").append("**Type**:").append(PunishmentType.valueOf((String) map.get("type")).getName()).append("\n\n");
        }
        return new EmbedBuilder()
                .setColor(Colors.COLOR_PRIMARY)
                .setTitle("Warn punishments")
                .setDescription(punishmentsString.toString());
    }

    private class WarnPunishmentSetupRequest extends SetupRequest {

        private WarnPunishmentSettings settings;

        private WarnPunishmentSetupRequest(Message msg, Member author){
            this.infoMessage = msg;
            this.author = author;
            this.setupChannel = msg.getTextChannel();
            this.settings = new WarnPunishmentSettings();
            this.guild = msg.getGuild();
            register(this);
        }

        @Override
        public void next(Message invokeMsg) {
            new Thread(() -> {
                switch (step){
                    case 0:
                        int amount;
                        try {
                            amount = Integer.parseInt(invokeMsg.getContentDisplay());
                        } catch (NumberFormatException e){
                            SafeMessage.sendMessage(setupChannel, setupMessage(translate("warns.setup.failed.title"), translate("warns.setup.step3.failed.description"), Colors.COLOR_ERROR).build());
                            return;
                        }
                        settings.amount = amount;
                        infoMessage.editMessage(setupMessage(translate("warns.setup.step1.info.title"), translate("warns.setup.step1.info.description"), Colors.COLOR_SECONDARY).build()).queue();
                        break;
                    case 1:
                        PunishmentType type;
                        try {
                            type = PunishmentType.valueOf(invokeMsg.getContentDisplay().toUpperCase());
                        } catch (IllegalArgumentException e){
                            SafeMessage.sendMessage(setupChannel, setupMessage(translate("warns.setup.failed.title"), translate("warns.setup.step1.failed.description"), Colors.COLOR_ERROR).build());
                            return;
                        }
                        settings.type = type;
                        infoMessage.editMessage(setupMessage(translate("warns.setup.step2.info.title"), translate("warns.setup.step2.info.description"), Colors.COLOR_SECONDARY).build()).queue();
                        break;
                    case 2:
                        settings.message = invokeMsg.getContentDisplay();
                        if(settings.type.equals(PunishmentType.KICK)){
                            finish();
                            return;
                        }
                        infoMessage.editMessage(setupMessage(translate("warns.setup.step3.info.title"), translate("warns.setup.step3.info.description"), Colors.COLOR_SECONDARY).build()).queue();
                        break;
                    case 3:
                        int length;
                        try {
                            length = Integer.parseInt(invokeMsg.getContentDisplay());
                        } catch (NumberFormatException e){
                            SafeMessage.sendMessage(setupChannel, setupMessage(translate("warns.setup.failed.title"), translate("warns.setup.step3.failed.description"), Colors.COLOR_ERROR).build());
                            return;
                        }
                        settings.lenth = length;
                        finish();
                }
                update();
            }, "WarnPunishmentSetupStep" + step + "-" + guild.getId()).start();
        }

        private void finish(){
            infoMessage.editMessage(setupMessage(translate("warns.setup.finish.title"), translate("warns.setup.finish.description"), Colors.COLOR_SECONDARY).build()).queue();
            unregister();
            rethink.db.table("warn_punishments").insert(rethink.rethinkDB.hashMap("guildId", guild.getId()).with("type", settings.type.toString()).with("length", settings.lenth).with("message", settings.message).with("amount", String.valueOf(settings.amount))).run(rethink.connection);

        }

        @Override
        public void abort() {

        }

        private class WarnPunishmentSettings{

            private int amount;
            private PunishmentType type;
            private int lenth;
            private String message;

        }
    }


}
