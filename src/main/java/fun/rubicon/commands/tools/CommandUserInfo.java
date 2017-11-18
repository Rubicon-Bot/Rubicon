package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandUserInfo extends Command{
    public CommandUserInfo(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        Message message = e.getMessage();
        User info;
        if(args.length > 0){
            if(message.getMentionedUsers().size() > 0)
                info = message.getMentionedUsers().get(0);
            else {
                sendUsageMessage();
                return;
            }
        } else {
            info = message.getAuthor();
        }

        Member user = e.getGuild().getMember(info);
        StringBuilder rolesraw = new StringBuilder();
        user.getRoles().forEach(r ->{
            rolesraw.append(r.getName()).append(", ");
        });
        StringBuilder roles = new StringBuilder(rolesraw.toString());
        roles.replace(rolesraw.lastIndexOf(","), roles.lastIndexOf(",") + 1, "" );
        EmbedBuilder userinfo = new EmbedBuilder();
        userinfo.setColor(Colors.COLOR_PRIMARY);
        userinfo.setTitle("User information of " + user.getUser().getName());
        userinfo.setFooter(Info.EMBED_FOOTER, Info.ICON_URL);
        userinfo.setThumbnail(info.getAvatarUrl());
        userinfo.addField("Nickname", user.getEffectiveName(), false);
        userinfo.addField("User id", info.getId(), false);
        userinfo.addField("Status", user.getOnlineStatus().toString().replace("_", ""), false);
        userinfo.addField("Game", user.getGame().toString().replaceFirst("null", "-/-"), false);
        userinfo.addField("Guild join date", user.getJoinDate().toString(), false);
        userinfo.addField("Roles", "`" + roles.toString() + "`", false);
        userinfo.addField("Discord join date", info.getCreationTime().toString(), false);
        userinfo.addField("Avatar url", info.getAvatarUrl(), false);
        e.getChannel().sendMessage(userinfo.build()).queue();
    }

    @Override
    public String getDescription() {
        return "Returns some information about the specified user";
    }

    @Override
    public String getUsage() {
        return "::userinfo [@User]";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
