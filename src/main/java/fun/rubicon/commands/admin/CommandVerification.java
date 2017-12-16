package fun.rubicon.commands.admin;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Rubicon Discord bot
 *
 * @author Michael Rittmeister / Schlaubi
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.admin
 */
public class CommandVerification extends CommandHandler {

    public static HashMap<Guild, VerificationSetup> setups = new HashMap<>();
    private static HashMap<Guild, VerificationSettings> settingslist = new HashMap<>();
    public static HashMap<Message, User> users = new HashMap<>();

    public CommandVerification() {
        super(new String[] {"verification", "verify"}, CommandCategory.ADMIN, new PermissionRequirements(PermissionLevel.ADMINISTRATOR, "command.verification"), "Let you members accept rules before posting messages", "verify setup\n verify disable");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.args;
        if(args.length == 0){
            return new MessageBuilder().setEmbed(EmbedUtil.error("Usage", "verify setup\n verify disable").build()).build();
        }
        if(args[0].equalsIgnoreCase("disable"))
            return disableVerification(parsedCommandInvocation);
        else if (args[0].equalsIgnoreCase("setup"))
            return enableVerification(parsedCommandInvocation);
        else
            return new MessageBuilder().setEmbed(EmbedUtil.error("Usage", "verify setup\n verify disable").build()).build();

    }

    private Message disableVerification(CommandManager.ParsedCommandInvocation parsedCommandInvocation){
        Message message = parsedCommandInvocation.invocationMessage;
        if(!RubiconBot.getMySQL().verificationEnabled(message.getGuild())){
            return new MessageBuilder().setEmbed(EmbedUtil.error("Not enabled", "Verification System is not enabled on this guild").build()).build();
        }

        RubiconBot.getMySQL().deleteGuildVerification(message.getGuild());

        return new MessageBuilder().setEmbed(EmbedUtil.success("Disabled", "Successfully disabled verification system").build()).build();
    }

    private Message enableVerification(CommandManager.ParsedCommandInvocation parsedCommandInvocation){
        Message message = parsedCommandInvocation.invocationMessage;
        if(RubiconBot.getMySQL().verificationEnabled(message.getGuild())){
            return new MessageBuilder().setEmbed(EmbedUtil.error("Already enabled", "Verification System is already enabled on this guild").build()).build();
        }
        MessageEmbed embed = EmbedUtil.info("Step 1 - Confirm setup", "Please react with :white_check_mark: \n **Requirements:** \n - Verification channel \n - Verified role").build();
        Message setupmsg = message.getTextChannel().sendMessage(embed).complete();
        setupmsg.addReaction("✅").queue();
        setupmsg.addReaction("❌").queue();
        VerificationSetup setup = new VerificationSetup(parsedCommandInvocation, setupmsg);
        setups.put(message.getGuild(), setup);
        return null;
    }

    public class VerificationSetup{
        public Message message;
        public User author;
        Guild guild;
        public int step;

        public VerificationSetup(CommandManager.ParsedCommandInvocation parsedCommandInvocation, Message message){
            this.message = message;
            this.author = parsedCommandInvocation.invocationMessage.getAuthor();
            this.guild = parsedCommandInvocation.invocationMessage.getGuild();
            this.step = 1;
        }
    }

    public static class VerificationSettings{

        public TextChannel channel;
        public String verifytext;
        public String verifiedtext;
        public Role role;
        public int kicktime;
        public String kicktext;

        public VerificationSettings(TextChannel verificationChannel, String verifytext, String verifiedtext, Role verifiedrole, int kicktime, String kicktext){
            this.channel = verificationChannel;
            this.verifytext = verifytext;
            this.verifiedtext = verifiedtext;
            this.role = verifiedrole;
            this.kicktime = kicktime;
            this.kicktext = kicktext;
        }
    }

    public static void handleReaction(MessageReactionAddEvent event){
        Message message = event.getTextChannel().getMessageById(event.getMessageId()).complete();
        if(!message.getAuthor().equals(RubiconBot.getJDA().getSelfUser())) return;
        if(!event.getUser().equals(users.get(message))) return;
        if(RubiconBot.getMySQL().verificationEnabled(event.getGuild())){
            TextChannel channel = event.getGuild().getTextChannelById(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "channelid"));
            if(event.getTextChannel().equals(channel)){
                event.getReaction().removeReaction().queue();
                Role verfied = event.getGuild().getRoleById(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "roleid"));
                event.getGuild().getController().addRolesToMember(event.getMember(), verfied).queue();
                message.editMessage(new EmbedBuilder().setDescription(RubiconBot.getMySQL().getVerificationValue(event.getGuild(), "verifiedtext").replace("%user%", event.getUser().getAsMention())).build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            }
        } else {
            if (!setups.containsKey(event.getGuild())) return;
            if (!setups.get(event.getGuild()).author.equals(event.getUser())) return;
            event.getReaction().removeReaction(event.getUser()).queue();
            message.getReactions().forEach(r -> r.removeReaction().queue());
            String emote = event.getReactionEmote().getName();
            if (emote.equalsIgnoreCase("✅"))
                message.editMessage(EmbedUtil.info("Step 2 - Channel", "Please mention the channel where verification messages should me posted").build()).queue();
            else if (emote.equalsIgnoreCase("❌"))
                message.editMessage(EmbedUtil.error("Aborted", "Successfully aborted setup").build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
        }


    }

    public static void setupStepOne(Message message, Message response){
          if(response.getMentionedChannels().isEmpty()){
            setups.remove(message.getGuild());
            message.delete().queue();
            return;
        }
        VerificationSettings settings = new VerificationSettings(response.getMentionedChannels().get(0), null, null, null, 0, null);
        settingslist.put(message.getGuild(), settings);

        message.editMessage(EmbedUtil.info("Step 3 - Verify message", "Please enter the text of the message that'll be sent to new users. (Use `%user%` to mention the user)").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
    }

    public static void setupStepTwo(Message message, Message response){
        if(response.getContentDisplay().length() > 1048){
            message.getTextChannel().sendMessage(EmbedUtil.error("To long", "Your message can't be longer than 1048 chars").build()).queue(msg -> msg.delete().queueAfter(4, TimeUnit.SECONDS));
            return;
        }
        VerificationSettings settings = settingslist.get(message.getGuild());
        settings.verifytext = response.getContentDisplay();
        settingslist.replace(message.getGuild(), settings);
        message.editMessage(EmbedUtil.info("Step 4 - Verified message", "Please enter the message that should be sent when a user accepted rules (Use `%user%` to mention the user)").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
    }

    public static void setupStepThree(Message message, Message response){
        if(response.getContentDisplay().length() > 1048){
            message.getTextChannel().sendMessage(EmbedUtil.error("To long", "Your message can't be longer than 1048 chars").build()).queue(msg -> msg.delete().queueAfter(4, TimeUnit.SECONDS));
            return;
        }
        VerificationSettings settings = settingslist.get(message.getGuild());
        settings.verifiedtext = response.getContentDisplay();
        settingslist.replace(message.getGuild(), settings);
        message.editMessage(EmbedUtil.info("Step 4 - Verified role", "Please mention the verified role").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
    }

    public static void setupStepFour(Message message, Message response) {
        VerificationSettings settings = settingslist.get(message.getGuild());
        if (response.getMentionedRoles().isEmpty()) {
            setups.remove(message.getGuild());
            settingslist.remove(message.getGuild());
            message.delete().queue();
            return;
        }
        settings.role = response.getMentionedRoles().get(0);
        settingslist.replace(message.getGuild(), settings);
        message.editMessage(EmbedUtil.info("Step 5 - Time", "Please enter the time (minutes) after that the user should be kicked when he does not accept the rules").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);

    }

    public static void setupStepFive(Message message, Message response){
        int kicktime;
        try {
            kicktime = Integer.parseInt(response.getContentDisplay());
        } catch (NumberFormatException e){
            message.getTextChannel().sendMessage(EmbedUtil.error("Invalid number", "Please enter a valid number").build()).complete();
            return;
        }
        VerificationSettings settings = settingslist.get(message.getGuild());
        if (kicktime == 0){
            settings.verifiedtext = "NULL";
            message.editMessage(EmbedUtil.success("Saved!", "Successfully enabled verification").build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            setups.remove(message.getGuild());
            settingslist.remove(message.getGuild());
            message.delete().queue();
            RubiconBot.getMySQL().createVerification(settings);
            return;
        }
        settings.kicktime = kicktime;
        message.editMessage(EmbedUtil.info("Step 6 - Kick message", "Please enter the message that'll be sent to the user after he got kicked").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
    }

    public static void setupStepSix(Message message, Message response){
        if(response.getContentDisplay().length() > 1048){
            message.getTextChannel().sendMessage(EmbedUtil.error("To long", "Your message can't be longer than 1048 chars").build()).queue(msg -> msg.delete().queueAfter(4, TimeUnit.SECONDS));
            return;
        }
        VerificationSettings settings = settingslist.get(message.getGuild());
        settings.kicktext = response.getContentDisplay();
        settingslist.replace(message.getGuild(), settings);
        RubiconBot.getMySQL().createVerification(settings);
        message.editMessage(EmbedUtil.success("Saved!", "Successfully enabled verification").build()).queue();
        setups.remove(message.getGuild());
        settingslist.remove(message.getGuild());
        message.delete().queue();
    }

}
