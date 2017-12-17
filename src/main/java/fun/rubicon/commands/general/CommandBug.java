package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;


public class CommandBug extends CommandHandler {

    public CommandBug() {
        super(new String[] {"bug"}, CommandCategory.GENERAL, new PermissionRequirements(0, "command.bug"), "Sends a bug to the bot developers", "[message] (min. 3 args)");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.args;
        Message message = parsedCommandInvocation.invocationMessage;
        //Check if enough args
        if (args.length < 3) {
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "bug [message] (min. 3 args)").build()).build();
        }
        //Make String out of args
        String text = "";
        for (String arg : args) {
            text += arg + " ";
        }

        //Post Report to Dev Server
        RubiconBot.getJDA().getTextChannelById("382231366064144384").sendMessage(
                new EmbedBuilder()
                        .setAuthor(message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator(), null, message.getAuthor().getAvatarUrl())
                        .setDescription("**New Bug Detected!**\n```fix\n" + text + "```")
                        .build()
        ).queue();
        //User Feedback
        return new MessageBuilder().setEmbed(EmbedUtil.success("Bug reported", "Successfully send the Bug to Head Developers").build()).build();
    }




}
