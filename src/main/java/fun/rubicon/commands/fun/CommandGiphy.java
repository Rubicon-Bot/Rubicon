package fun.rubicon.commands.fun;

import at.mukprojects.giphy4j.Giphy;
import at.mukprojects.giphy4j.entity.search.SearchFeed;
import at.mukprojects.giphy4j.exception.GiphyException;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2018
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandGiphy extends CommandHandler{
    public CommandGiphy() {
        super(new String[]{"giphy","gif",}, CommandCategory.FUN,new PermissionRequirements(0,"command.giphy"),"Search a Gif on Giphy and post it to the Channel","<SearchQuery> [ofset]",false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.invocationMessage;
        MessageChannel channel = message.getChannel();
        String[] args = parsedCommandInvocation.args;
        Message gifmessage = channel.sendMessage(new EmbedBuilder().setDescription("Collecting Gif´s ...").setColor(Colors.COLOR_SECONDARY).build()).complete();
        String query ="";
        Giphy giphy =  new Giphy(Info.GIPHY_TOKEN);
        int ofset = 0;
        if(StringUtil.isNumeric(args[args.length-1])){
            ofset = Integer.parseInt(args[args.length-1]);
            for (int i = 0;i< args.length-1;i++){
                query += args[i] + " ";
            }
        }else {
            for (int i = 0;i< args.length;i++){
                query += args[i] + " ";
            }
        }
        try {
            SearchFeed feed =  giphy.search(query,1,ofset);
            try {
                String gifurl = feed.getDataList().get(0).getImages().getOriginal().getUrl();
                gifmessage.delete().queue();
                channel.sendMessage(gifurl).queue();
            }catch (IndexOutOfBoundsException e){
                gifmessage.editMessage(new EmbedBuilder().setDescription("No Gif Found .").setColor(Colors.COLOR_ERROR).build()).queue();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        gifmessage.delete().queue();
                    }
                }, 5000);
            }
        }catch (GiphyException e){
            gifmessage.editMessage(new EmbedBuilder().setDescription("No Gif Found .").setColor(Colors.COLOR_ERROR).build()).queue();
            e.printStackTrace();
        }
        return null;
    }
}
