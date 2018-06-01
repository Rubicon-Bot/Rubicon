package fun.rubicon.features.verification;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.translation.TranslationUtil;
import fun.rubicon.io.deprecated_rethink.Rethink;
import fun.rubicon.setup.ReactionSetupRequest;
import fun.rubicon.util.Colors;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;


/**
 * @author Schlaubi / Michael Rittmeister
 */

public class VerificationSetupRequest extends ReactionSetupRequest {

    private VerificationSettings settings;
    private Guild guild;


    private VerificationSetupRequest(Message msg, Member author) {
        this.infoMessage = msg;
        this.author = author;
        this.settings = new VerificationSettings();
        this.setupChannel = msg.getTextChannel();
        this.guild = msg.getGuild();
    }

    public static void createVerificationSetupRequest(CommandManager.ParsedCommandInvocation invocation) {
        if (!invocation.getSelfMember().hasPermission(invocation.getTextChannel(), Permission.MESSAGE_ADD_REACTION)) {
            SafeMessage.sendMessage(invocation.getTextChannel(), invocation.translate("error.noreactionperm"));
            return;
        }
        Message msg = SafeMessage.sendMessageBlocking(invocation.getTextChannel(), TranslationUtil.translate(invocation.getAuthor(), "verification.setup.step0"));
        msg.addReaction("✅").queue();
        msg.addReaction("⛔").queue();
        VerificationSetupRequest req = new VerificationSetupRequest(msg, invocation.getMember());
        VerificationCommandHandler.guildList.add(invocation.getGuild());
        register(req);
    }

    @Override
    public void next(Message invoke_msg) {
        new Thread(() -> {
            switch (step) {
                case 1:
                    if (invoke_msg.getMentionedChannels().isEmpty()) {
                        SafeMessage.sendMessage(setupChannel, setupMessage(translate("verification.setup.failed.title"), translate("verification.setup.step1.failed"), Colors.COLOR_ERROR).build(), 4);
                        return;
                    }
                    TextChannel channel = invoke_msg.getMentionedChannels().get(0);
                    if (!guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION)) {
                        SafeMessage.sendMessage(setupChannel, setupMessage(translate("verification.setup.failed.title"), translate("verification.setup.step1.noperm"), Colors.COLOR_ERROR).build(), 4);
                        return;
                    }
                    settings.channel = invoke_msg.getMentionedChannels().get(0);
                    infoMessage.editMessage(setupMessage(translate("verification.setup.step2.info.title"), translate("verification.setup.step2.info.description"), Colors.COLOR_SECONDARY).build()).queue();
                    break;
                case 2:
                    if (invoke_msg.getContentDisplay().length() > 1048) {
                        SafeMessage.sendMessage(setupChannel, setupMessage(translate("verification.setup.failed.title"), translate("verification.setup.toolong"), Colors.COLOR_ERROR).build(), 4);
                        return;
                    }
                    settings.verifytext = invoke_msg.getContentDisplay();
                    infoMessage.editMessage(setupMessage(translate("verification.setup.step3.info.title"), translate("verification.setup.step3.info.description"), Colors.COLOR_SECONDARY).build()).queue();
                    break;
                case 3:
                    if (invoke_msg.getContentDisplay().length() > 1048) {
                        SafeMessage.sendMessage(setupChannel, setupMessage(translate("verification.setup.failed.title"), translate("verification.setup.toolong"), Colors.COLOR_ERROR).build(), 4);
                        return;
                    }
                    settings.verifiedtext = invoke_msg.getContentDisplay();
                    infoMessage.editMessage(setupMessage(translate("verification.setup.step4.info.title"), translate("verification.setup.step4.info.description"), Colors.COLOR_SECONDARY).build()).queue();
                    break;
                case 4:
                    if (invoke_msg.getMentionedRoles().isEmpty()) {
                        SafeMessage.sendMessage(setupChannel, setupMessage(translate("verification.setup.failed.title"), translate("verification.setup.step4.failed"), Colors.COLOR_ERROR).build(), 4);
                        return;
                    }
                    Role verifyRole = invoke_msg.getMentionedRoles().get(0);
                    if (!guild.getSelfMember().canInteract(verifyRole) || !guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
                        SafeMessage.sendMessage(setupChannel, setupMessage(translate("verification.setup.failed.title"), translate("verification.setup.step4.noperms"), Colors.COLOR_ERROR).build(), 4);
                        return;
                    }
                    settings.role = verifyRole;
                    infoMessage.editMessage(setupMessage(translate("verification.setup.step5.info.title"), translate("verification.setup.step5.info.description"), Colors.COLOR_SECONDARY).build()).queue();
                    break;
                case 6:
                    int time;
                    try {
                        time = Integer.parseInt(invoke_msg.getContentDisplay());
                    } catch (NumberFormatException e) {
                        SafeMessage.sendMessage(setupChannel, setupMessage(translate("verification.setup.failed.title"), translate("verification.setup.step6.failed"), Colors.COLOR_ERROR).build(), 4);
                        return;
                    }
                    if (time == 0) {
                        finish();
                        return;
                    }
                    settings.kicktime = time;
                    infoMessage.editMessage(setupMessage(translate("verification.setup.step7.info.title"), translate("verification.setup.step7.info.description"), Colors.COLOR_SECONDARY).build()).queue();
                    break;
                case 7:
                    if (invoke_msg.getContentDisplay().length() > 1048) {
                        SafeMessage.sendMessage(setupChannel, setupMessage(translate("verification.setup.failed.title"), translate("verification.setup.toolong"), Colors.COLOR_ERROR).build(), 4);
                        return;
                    }
                    settings.kicktext = invoke_msg.getContentDisplay();
                    finish();
                    break;
                default:
                    return;
            }
            update();
        }, "VerificationSetupStep" + step + "Handler-" + guild.getId()).start();
    }

    @Override
    public void abort() {
        VerificationCommandHandler.guildList.remove(guild);
    }

    private void finish() {
        unregister();
        VerificationCommandHandler.guildList.remove(guild);
        SafeMessage.sendMessage(setupChannel, TranslationUtil.translate(author, "verification.setup.finish"));
        Rethink rethink = RubiconBot.getRethink();
        rethink.db.table("verification_settings").insert(rethink.rethinkDB.hashMap("guildId", guild.getId()).with("channelId", settings.channel.getId()).with("roleId", settings.role.getId()).with("emote", settings.emote.getId() != null ? settings.emote.getId() : settings.emote.getName()).with("welcomeText", settings.verifytext).with("verifiedText", settings.verifiedtext).with("kickTime", String.valueOf(settings.kicktime)).with("kickText", settings.kicktext)).run(rethink.getConnection());
    }

    @Override
    public void handleReaction(GuildMessageReactionAddEvent event) {
        new Thread(() -> {
            if (step == 0) {
                if (event.getReactionEmote().getName() == null) return;
                String emote = event.getReactionEmote().getName();
                if (emote.equals("✅")) {
                    event.getReaction().removeReaction(event.getUser()).queue();
                    event.getChannel().getMessageById(event.getMessageId()).complete().getReactions().forEach(r -> r.removeReaction().queue());
                    infoMessage.editMessage(setupMessage(translate("verification.setup.step1.info.title"), translate("verification.setup.step1.info.description"), Colors.COLOR_SECONDARY).build()).queue();
                } else if (emote.equals("⛔")) {
                    infoMessage.delete().queue();
                    unregister();
                    return;
                }
            } else if (step == 5) {
                if (event.getReactionEmote().getId() != null) {
                    if (!event.getGuild().getEmotes().contains(event.getReactionEmote().getEmote())) {
                        SafeMessage.sendMessage(setupChannel, setupMessage(translate("verification.setup.failed.title"), translate("verification.setup.step5.failed"), Colors.COLOR_ERROR).build(), 4);
                        return;

                    }
                }
                event.getReaction().removeReaction(event.getUser()).queue();
                settings.emote = event.getReactionEmote();
                infoMessage.editMessage(setupMessage(translate("verification.setup.step6.info.title"), translate("verification.setup.step6.info.description"), Colors.COLOR_SECONDARY).build()).queue();
            }
            update();
        }, "VerificationSetupStep" + step + "Handler-" + guild.getId()).start();
    }


    private class VerificationSettings {

        public TextChannel channel;
        public String verifytext;
        public String verifiedtext;
        public Role role;
        public MessageReaction.ReactionEmote emote;
        public int kicktime;
        public String kicktext;
    }

}
