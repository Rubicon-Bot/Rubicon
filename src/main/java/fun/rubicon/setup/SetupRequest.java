package fun.rubicon.setup;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.translation.TranslationUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.concurrent.TimeUnit;


/**
 * @author Schlaubi / Michael Rittmeister
 */

public abstract class SetupRequest {

    public Member author;
    public Message infoMessage;
    public int step = 0;
    public TextChannel setupChannel;
    public Guild guild;

    public abstract void next(Message invokeMsg);

    public abstract void abort();

    public static void register(SetupRequest req){
        RubiconBot.getSetupManager().requestStorage.put(req.author.getUser().getId(), req);
    }

    public void unregister(){
        abort(); RubiconBot.getSetupManager().requestStorage.remove(author.getUser().getId(), this); infoMessage.delete().queueAfter(5, TimeUnit.SECONDS);
    }

    public void update() { this.step++; RubiconBot.getSetupManager().requestStorage.replace(infoMessage.getId(), this); }

    public String translate(String key) {
        return TranslationUtil.translate(author, key);
    }

    public EmbedBuilder setupMessage(String title, String desc, Color color){ return new EmbedBuilder().setTitle(title).setDescription(desc).setColor(color).setFooter(translate("verification.setup.footer"), author.getUser().getAvatarUrl()); }

}
