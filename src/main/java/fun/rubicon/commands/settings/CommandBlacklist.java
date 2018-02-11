package fun.rubicon.commands.settings;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.GuildSQL;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;

import java.util.Arrays;
import java.util.List;

public class CommandBlacklist extends CommandHandler{
    public CommandBlacklist() {
        super(new String[] {"blacklist", "bl"}, CommandCategory.SETTINGS, new PermissionRequirements("command.blacklist", false, false), "Easily blacklist channels from command usage", " <add/remove/list> <#Channel>", false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        Member member = parsedCommandInvocation.getMember();
        Guild guild = parsedCommandInvocation.getGuild();
        TextChannel channel = parsedCommandInvocation.getTextChannel();
        Message message = parsedCommandInvocation.getMessage();

        if(args.length == 0)
            return createHelpMessage();
        GuildSQL guildSQL = GuildSQL.fromGuild(guild);
        if(guildSQL.enabledWhitelist()){
            return new MessageBuilder().setEmbed(EmbedUtil.error("Already using whitelist", "You can't use black- and whitelist on the same server").build()).build();
        }
        switch (args[0]){
            case "list":
                executeList(args, member, guild, channel);
                break;
            case "add":
            case "blacklist":
                executeAdd(args, member, guild, channel, message);
                break;
            case "remove":
            case "whitelist":
                executeRemove(args, member, guild, channel, message);
                break;
            default:
                SafeMessage.sendMessage(channel, createHelpMessage(), 5);
                break;
        }
        return null;
    }

    private void executeRemove(String[] args, Member member, Guild guild, TextChannel textChannel, Message message) {
        GuildSQL sql = GuildSQL.fromGuild(guild);
        if(message.getMentionedChannels().isEmpty()){ SafeMessage.sendMessage(textChannel, EmbedUtil.error("Unknown usage", "Please use `rc!blacklist add <#Channel>`").build(), 7); return; }

        TextChannel channel = message.getMentionedChannels().get(0);
        if(!sql.isBlacklisted(channel)){ SafeMessage.sendMessage(textChannel, EmbedUtil.info("Not blacklisted", "This channel is not blacklisted").build()); return; }
        String oldEntry = RubiconBot.getMySQL().getGuildValue(guild, "blacklist");
        String newEntry;
        if(oldEntry.equals(channel.getId()))
            newEntry = "";
        else
            newEntry = oldEntry.replace(channel.getId(), "");

        if(newEntry.contains(","))
            newEntry = new StringBuilder(newEntry).replace(newEntry.lastIndexOf(","), newEntry.lastIndexOf(",") + 1 , "").toString();

        RubiconBot.getMySQL().updateGuildValue(guild, "blacklist", newEntry);
        SafeMessage.sendMessage(textChannel, EmbedUtil.success("Successfully removed channel from blacklist", "Successfully removed channel `" + channel.getName() + "` from blacklist!").build(), 5);
    }

    private void executeAdd(String[] args, Member member, Guild guild, TextChannel textChannel, Message message) {
        GuildSQL guildSQL = GuildSQL.fromGuild(guild);
        if(message.getMentionedChannels().isEmpty()){ SafeMessage.sendMessage(textChannel, EmbedUtil.error("Unknown usage", "Please use `rc!whitelist add <#Channel>`").build(), 7); return; }
        TextChannel channel = message.getMentionedChannels().get(0);
        if(guildSQL.isBlacklisted(channel)){ SafeMessage.sendMessage(textChannel, EmbedUtil.info("Already blacklisted", "This channel is already whitelisted").build()); return; }
        String oldEntry = RubiconBot.getMySQL().getGuildValue(guild, "blacklist");
        String newEntry;
        if(oldEntry.equals(""))
            newEntry = channel.getId();
        else
            newEntry = oldEntry + "," + channel.getId();

        RubiconBot.getMySQL().updateGuildValue(guild, "blacklist", newEntry);
        SafeMessage.sendMessage(textChannel, EmbedUtil.success("Successfully blacklisted channel", "Successfully whitelisted channel `" + channel.getName() + "` !").build(), 5);
    }

    private void executeList(String[] args, Member member, Guild guild, TextChannel textChannel) {
        GuildSQL guildSQL = GuildSQL.fromGuild(guild);
        if(guildSQL.enabledBlacklist()) {
            List<String> channelIDs = Arrays.asList(RubiconBot.getMySQL().getGuildValue(guild, "blacklist").split(","));
            StringBuilder channels = new StringBuilder();
            channelIDs.forEach(id -> {
                try{
                    TextChannel channel = guild.getTextChannelById(id);
                    channels.append(channel.getName()).append(", ");
                } catch (NullPointerException ignored){
                    String oldEntry = RubiconBot.getMySQL().getGuildValue(guild, "blacklist");
                    String newEntry = oldEntry.replace(id, "");
                    if(newEntry.contains(","))
                        newEntry = new StringBuilder(newEntry).replace(newEntry.lastIndexOf(","), newEntry.lastIndexOf(",") + 1, "").toString();
                    RubiconBot.getMySQL().updateGuildValue(guild, "blacklist", newEntry);
                    if(channelIDs.size() == 0){
                        SafeMessage.sendMessage(textChannel, EmbedUtil.info("Blacklisted Channels", "Blacklist mode: `" + String.valueOf(guildSQL.enabledBlacklist()).replace("true", "enabled").replace("false", "disabled") + "`").build());
                        return;
                    }
                }
            });
            channels.replace(channels.lastIndexOf(","), channels.lastIndexOf(",") + 1, "");
            SafeMessage.sendMessage(textChannel, EmbedUtil.info("Blacklisted Channels", "Blacklist mode: `" + String.valueOf(guildSQL.enabledBlacklist()).replace("true", "enabled").replace("false", "disabled") + "`\nChannels: `" + channels.toString() + "`").build());
        }else
            SafeMessage.sendMessage(textChannel, EmbedUtil.info("Blacklisted Channels", "Blacklist mode: `" + String.valueOf(guildSQL.enabledBlacklist()).replace("true", "enabled").replace("false", "disabled") + "`").build());
    }

    public static void handleTextChannelDeletion(TextChannelDeleteEvent event){
        GuildSQL sql = GuildSQL.fromGuild(event.getGuild());
        if(sql.isBlacklisted(event.getChannel())){
            String oldEntry = RubiconBot.getMySQL().getGuildValue(event.getGuild(), "blacklist");
            String newEntry = oldEntry.replace(event.getChannel().getId(), "");
            if(newEntry.contains(","))
                newEntry = new StringBuilder(newEntry).replace(newEntry.lastIndexOf(","), newEntry.lastIndexOf(",") + 1, "").toString();
            RubiconBot.getMySQL().updateGuildValue(event.getGuild(), "blacklist", newEntry);
        }

    }
}