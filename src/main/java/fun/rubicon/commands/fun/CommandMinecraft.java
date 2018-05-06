package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.MinecraftUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.text.SimpleDateFormat;

public class CommandMinecraft extends CommandHandler {


    public CommandMinecraft() {
        super(new String[]{"minecraft", "mc"}, CommandCategory.FUN, new PermissionRequirements("minecraft", false, true), "Some tools for Minecraft players", "<player>", false);
    }

    private MinecraftUtil mojang = new MinecraftUtil();

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        if (args.length == 0)
            return createHelpMessage();
        commandPlayer(args, parsedCommandInvocation);
        return null;
    }

    private void commandPlayer(String[] args, CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = SafeMessage.sendMessageBlocking(parsedCommandInvocation.getTextChannel(), new MessageBuilder().setEmbed(EmbedUtil.info(parsedCommandInvocation.translate("command.minecraft.fetching.title"), parsedCommandInvocation.translate("command.minecraft.fetching.description")).build()).build());
        String uuid = mojang.fetchUUID(args[0]);
        if (uuid == null) {
            message.delete().queue();
            SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), EmbedUtil.error(parsedCommandInvocation.translate("command.minecraft.fetcherror.title"), parsedCommandInvocation.translate("command.minecraft.fetcherror.description")).build(), 6);
            return;
        }
        MinecraftUtil.MinecraftPlayer player = mojang.fromUUID(uuid);
        message.editMessage(createUserEmbed(player)).queue();
    }

    private MessageEmbed createUserEmbed(MinecraftUtil.MinecraftPlayer player) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Info of user " + player.getName());
        builder.addField("UUID", player.getUuid(), false);
        if (!player.getNamechanges().isEmpty()) {
            builder.addField("Firstname", player.getFirstName(), false);
            builder.addField("Name history", buildNameHistory(player), false);
        } else
            builder.addField("Name", player.getName(), true);
        /*WHYYYYYY?!??!?!?!?!*/
        builder.setImage("https://crafatar.com/avatars/" + player.getUuid());
        builder.setFooter("Minecraft command by RubiconBot", null);
        builder.setColor(Colors.COLOR_SECONDARY);
        return builder.build();
    }


    private String buildNameHistory(MinecraftUtil.MinecraftPlayer player) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/mm/yyyy HH:mm");
        StringBuilder names = new StringBuilder();
        player.getNamechanges().forEach((n, t) -> names.append(sfd.format(t)).append(" -> ").append("`").append(n).append("`").append("\n"));
        return names.toString();
    }

}