/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.admin;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.features.VerificationKickHandler;
import fun.rubicon.features.VerificationUserHandler;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Handles the 'verification' command.
 *
 * @author Michael Rittmeister / Schlaubi
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.admin
 * =======
 */
public class CommandVerification extends CommandHandler {

    public static HashMap<Guild, VerificationSetup> setups = new HashMap<>();
    private static HashMap<Guild, VerificationSettings> settingslist = new HashMap<>();
    public static HashMap<Message, User> users = new HashMap<>();

    public static boolean showInspired = false;

    public CommandVerification() {
        super(new String[]{"verification", "verify"}, CommandCategory.ADMIN, new PermissionRequirements("command.verification", false, false), "Let you members accept rules before posting messages\n\nThis feature is partially inspired by [Flashbot](https://flashbot.de)", "setup\ndisable");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        if (args.length == 0) {
            return createHelpMessage();
        }
        if (args[0].equalsIgnoreCase("disable"))
            return disableVerification(parsedCommandInvocation);
        else if (args[0].equalsIgnoreCase("setup"))
            return enableVerification(parsedCommandInvocation);
        else
            return createHelpMessage();

    }

    private Message disableVerification(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        if (!RubiconBot.getMySQL().verificationEnabled(message.getGuild())) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Not enabled", "Verification System is not enabled on this guild").build()).build();
        }

        RubiconBot.getMySQL().deleteGuildVerification(message.getGuild());

        return new MessageBuilder().setEmbed(EmbedUtil.success("Disabled", "Successfully disabled verification system").build()).build();
    }

    private Message enableVerification(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        if (RubiconBot.getMySQL().verificationEnabled(message.getGuild())) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Already enabled", "Verification System is already enabled on this guild").build()).build();
        }
        MessageEmbed embed = EmbedUtil.info("Step 1 - Confirm setup", "Please react with :white_check_mark: \n **Requirements:** \n - Channel for the verification messages \n - Role to be added to the users \n - Custom accept emoji").build();
        Message setupmsg = message.getTextChannel().sendMessage(embed).complete();
        setupmsg.addReaction("✅").queue();
        setupmsg.addReaction("❌").queue();
        VerificationSetup setup = new VerificationSetup(parsedCommandInvocation, setupmsg);
        setups.put(message.getGuild(), setup);
        users.put(setupmsg, message.getAuthor());
        return null;
    }

    public class VerificationSetup {
        public Message message;
        public User author;
        Guild guild;
        public int step;

        VerificationSetup(CommandManager.ParsedCommandInvocation parsedCommandInvocation, Message message) {
            this.message = message;
            this.author = parsedCommandInvocation.getMessage().getAuthor();
            this.guild = parsedCommandInvocation.getMessage().getGuild();
            this.step = 1;
        }
    }

    public static class VerificationSettings {

        public TextChannel channel;
        public String verifytext;
        public String verifiedtext;
        public Role role;
        public MessageReaction.ReactionEmote emote;
        public int kicktime;
        public String kicktext;

        VerificationSettings(TextChannel verificationChannel, String verifytext, String verifiedtext, Role verifiedrole, int kicktime, String kicktext, MessageReaction.ReactionEmote emote) {
            this.channel = verificationChannel;
            this.verifytext = verifytext;
            this.verifiedtext = verifiedtext;
            this.role = verifiedrole;
            this.emote = emote;
            this.kicktime = kicktime;
            this.kicktext = kicktext;
        }
    }

    public static void handleReaction(MessageReactionAddEvent event) {
        Message message = null;
        try {
            message = event.getTextChannel().getMessageById(event.getMessageId()).complete();
        } catch (InsufficientPermissionException ignored) {
        }
        if (message == null) return;

        if (!message.getAuthor().equals(event.getJDA().getSelfUser())) return;
        if (!event.getUser().equals(users.get(message))) return;
        if (RubiconBot.getMySQL().verificationEnabled(event.getGuild())) {
            TextChannel channel = event.getGuild().getTextChannelById(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "channelid"));
            if (event.getTextChannel().equals(channel)) {
                event.getReaction().removeReaction().queue();
                String emote = RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "emote");
                if (!emote.equals(event.getReactionEmote().getName()) && !emote.equals(event.getReactionEmote().getId()))
                    return;
                Role verfied = event.getGuild().getRoleById(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "roleid"));
                if (!event.getGuild().getSelfMember().canInteract(verfied)) {
                    event.getTextChannel().sendMessage(EmbedUtil.error("Error!", "I can not assign roles that are higher than my role.").build()).queue();
                }

                event.getGuild().getController().addRolesToMember(event.getMember(), verfied).queue();
                VerificationUserHandler.VerifyUser.fromMember(event.getMember()).remove();
                message.getReactions().forEach(r -> {
                    r.removeReaction().queue();
                });
                message.editMessage(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "verifiedtext").replace("%user%", event.getUser().getAsMention()).replace("%guild%", event.getGuild().getName())).queue();
                message.getReactions().forEach(r -> {
                    r.getUsers().forEach(u -> {
                        r.removeReaction(u).queue();
                    });
                });
                message.delete().queueAfter(5, TimeUnit.SECONDS);
                VerificationKickHandler.VerifyKick.fromMember(event.getMember(), true).remove();
            }
        } else {
            if (!setups.containsKey(event.getGuild())) return;
            if (!setups.get(event.getGuild()).author.equals(event.getUser())) return;
            if (!event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE)) {
                message.editMessage(EmbedUtil.error("Aborted", "Bot has not the Permissions MESSAGE_MANAGE.Please contact the Owner for the Permissions").build()).queue();
                return;
            }
            event.getReaction().removeReaction(event.getUser()).queue();
            message.getReactions().forEach(r -> r.removeReaction().queue());
            String emote = event.getReactionEmote().getName();
            if (setups.get(event.getGuild()).step == 4)
                setupStepFour(event);
            else if (emote.equalsIgnoreCase("✅"))
                message.editMessage(EmbedUtil.info("Step 2 - Channel", "Please mention the channel where verification messages should me posted").build()).queue();
            else if (emote.equalsIgnoreCase("❌"))
                message.editMessage(EmbedUtil.error("Aborted", "Successfully aborted setup").build()).queue();
        }
    }

    public static void setupStepOne(Message message, Message response) {
        if (response.getMentionedChannels().isEmpty()) {
            setups.remove(message.getGuild());
            message.delete().queue();
            return;
        }
        VerificationSettings settings = new VerificationSettings(response.getMentionedChannels().get(0), null, null, null, 0, null, null);
        settingslist.put(message.getGuild(), settings);

        message.editMessage(EmbedUtil.info("Step 3 - Verify message", "Please enter the text of the message that'll be sent to new users. (Use `%user%` to mention the user and `%guild%` for the servername)").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
    }

    public static void setupStepTwo(Message message, Message response) {
        if (response.getContentDisplay().length() > 1048) {
            SafeMessage.sendMessage(message.getTextChannel(), EmbedUtil.message(EmbedUtil.error("Too long", "Your message can't be longer than 1048 chars")), 4);
            return;
        }
        VerificationSettings settings = settingslist.get(message.getGuild());
        settings.verifytext = response.getContentDisplay();
        settingslist.replace(message.getGuild(), settings);
        message.editMessage(EmbedUtil.info("Step 4 - Verified message", "Please enter the message that should be sent when a user accepted rules (Use `%user%` to mention the user and `%guild%` for the servername)").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
    }

    public static void setupStepThree(Message message, Message response) {
        if (response.getContentDisplay().length() > 1048) {
            SafeMessage.sendMessage(message.getTextChannel(), EmbedUtil.message(EmbedUtil.error("Too long", "Your message can't be longer than 1048 chars")), 4);
            return;
        }
        VerificationSettings settings = settingslist.get(message.getGuild());
        settings.verifiedtext = response.getContentDisplay();
        settingslist.replace(message.getGuild(), settings);
        message.editMessage(EmbedUtil.info("Step 4 - Verified emote", "Please react with the verify emote. Emote must be a **custom** emote from **your** server.").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
    }

    public static void setupStepFour(MessageReactionAddEvent event) {
        Message message = event.getTextChannel().getMessageById(event.getMessageId()).complete();
        VerificationSettings settings = settingslist.get(event.getGuild());
        MessageReaction.ReactionEmote emote = event.getReactionEmote();
        //System.out.println(event.getReactionEmote().getEmote().isManaged());
        try {


            if (!event.getReactionEmote().getEmote().isManaged()) {
                if (!event.getGuild().getEmotes().contains(emote.getEmote())) {
                    SafeMessage.sendMessage(message.getTextChannel(), EmbedUtil.message(EmbedUtil.error("Unsupported emote", "You can only use global or custom emotes of your server")), 4);
                    return;
                }
            }
        } catch (NullPointerException ignored) {

        }
        settings.emote = emote;
        settingslist.replace(event.getGuild(), settings);
        message.editMessage(EmbedUtil.info("Step 5 - Verified role", "Please mention the role that should be added to user").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
        event.getReaction().removeReaction(event.getUser()).queue();
    }

    public static void setupStepFive(Message message, Message response) {
        VerificationSettings settings = settingslist.get(message.getGuild());
        if (response.getMentionedRoles().isEmpty()) {
            setups.remove(message.getGuild());
            settingslist.remove(message.getGuild());
            message.delete().queue();
            return;
        }
        settings.role = response.getMentionedRoles().get(0);
        settingslist.replace(message.getGuild(), settings);
        message.editMessage(EmbedUtil.info("Step 6 - Time", "Please enter the time (minutes) after that the user should be kicked when he does not accept the rules").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);

    }

    public static void setupStepSix(Message message, Message response) {
        int kicktime;
        try {
            kicktime = Integer.parseInt(response.getContentDisplay());
        } catch (NumberFormatException e) {
            SafeMessage.sendMessageBlocking(message.getTextChannel(), EmbedUtil.message(EmbedUtil.error("Invalid number", "Please enter a valid number")));
            return;
        }
        VerificationSettings settings = settingslist.get(message.getGuild());
        if (kicktime == 0) {
            settings.kicktext = "NULL";
            message.editMessage(EmbedUtil.success("Saved!", "Successfully enabled verification").build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            setups.remove(message.getGuild());
            settingslist.remove(message.getGuild());
            RubiconBot.getMySQL().createVerification(settings);
            return;
        }
        settings.kicktime = kicktime;
        message.editMessage(EmbedUtil.info("Step 7 - Kick message", "Please enter the message that'll be sent to the user after he got kicked use `%invite%` to embed a custom invite link to verification channel (1 user limited) or `%guild%` for the servername").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
    }

    public static void setupStepSeven(Message message, Message response) {
        if (response.getContentDisplay().length() > 1048) {
            SafeMessage.sendMessage(message.getTextChannel(), EmbedUtil.message(EmbedUtil.error("Too long", "Your message can't be longer than 1048 chars")), 4);
            return;
        }
        VerificationSettings settings = settingslist.get(message.getGuild());
        settings.kicktext = response.getContentDisplay();
        settingslist.replace(message.getGuild(), settings);
        RubiconBot.getMySQL().createVerification(settings);
        message.delete().queue();
        message.getTextChannel().sendMessage((EmbedUtil.success("Saved!", "Successfully enabled verification").build())).queue();
        setups.remove(message.getGuild());
        settingslist.remove(message.getGuild());
    }

    public static void toggleInspired() {
        if (showInspired) {
            showInspired = false;
        } else {
            showInspired = true;
        }
    }
}
