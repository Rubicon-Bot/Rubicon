package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.MojangUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class CommandMinecraft extends CommandHandler{
    public CommandMinecraft() {
        super(new String[] {"minecraft", "mc"}, CommandCategory.FUN, new PermissionRequirements(PermissionLevel.EVERYONE, "command.minecraft"), "Some funny tools for Minecraft players", " <status/player/sever> <playername/serverip>", false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        if(args.length == 0)
            return createHelpMessage();
        switch (args[0]){
            case "player":
                commandPlayer(args, parsedCommandInvocation);
                break;
        }
        return null;
    }

    private void commandPlayer(String[] args, CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        if(args.length < 2){ SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), createHelpMessage(), 5); return; }
        Message message = SafeMessage.sendMessageBlocking(parsedCommandInvocation.getTextChannel(), new MessageBuilder().setEmbed(EmbedUtil.info("Fetching", "Fetching player's uuid").build()).build());
        String uuid = MojangUtil.fetchUUID(args[1]);
        if(uuid == null){ SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), EmbedUtil.error("Error white UUID fetching","Unable to find players UUID").build(), 6); return; }
        message.editMessage(EmbedUtil.info(uuid, uuid).build()).queue();
    }
}
