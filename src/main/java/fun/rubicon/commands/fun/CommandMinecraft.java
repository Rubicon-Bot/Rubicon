package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.MojangUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;

public class CommandMinecraft extends CommandHandler{


    public CommandMinecraft() {
        super(new String[] {"minecraft", "mc"}, CommandCategory.FUN, new PermissionRequirements("command.minecraft", false, true), "Some tools for Minecrafters", " <player/connect/server> <name/ip>", false);
    }

    private MojangUtil mojang = new MojangUtil();
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        if(args.length == 0)
            return createHelpMessage();
        switch (args[0]){
            case "player":
                commandPlayer(args, parsedCommandInvocation);
                break;
            case "connect":
                //commandConnect(args, parsedCommandInvocation);
                break;

        }
        return null;
    }



    private void commandConnect(String[] args, CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        if(args.length > 2) { SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), createHelpMessage(), 6); return; }

    }

    private void commandPlayer(String[] args, CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        if(args.length < 2){ SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), createHelpMessage(), 5); return; }
        Message message = SafeMessage.sendMessageBlocking(parsedCommandInvocation.getTextChannel(), new MessageBuilder().setEmbed(EmbedUtil.info("Fetching", "Fetching player's uuid").build()).build());
        String uuid = mojang.fetchUUID(args[1]);
        if(uuid == null){ message.delete().queue(); SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), EmbedUtil.error("Error while UUID fetching","Unable to find players UUID").build(), 6); return; }
        MojangUtil.MinecraftPlayer player = mojang.fromUUID(uuid);
        message.editMessage(createUserEmbed(player)).queue();
    }

    private MessageEmbed createUserEmbed(MojangUtil.MinecraftPlayer player){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Info of user " + player.getName());
        builder.addField("UUID", player.getUuid(), false);
        if(!player.getNamechanges().isEmpty()) {
            builder.addField("Firstname", player.getFirstName(), false);
            builder.addField("Name history", buildNameHistory(player), false);
        }
        else
            builder.addField("Name", player.getName(), true);
        builder.setThumbnail("https://crafatar.com/avatars/" + player.getUuid());
        builder.setFooter("Minecraft command by RubiconBot", null);
        builder.setColor(Colors.COLOR_SECONDARY);
        return builder.build();
    }




    private String buildNameHistory(MojangUtil.MinecraftPlayer player){
        SimpleDateFormat sfd = new SimpleDateFormat("dd/mm/yyyy HH:mm");
        StringBuilder names = new StringBuilder();
        player.getNamechanges().forEach((n, t) -> {
            names.append(sfd.format(t)).append(" -> ").append("`" + n + "`").append("\n");
        });
        return names.toString();
    }

}
