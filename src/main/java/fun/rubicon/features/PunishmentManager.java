package fun.rubicon.features;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandHandler;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PunishmentManager {

    private List<PunishmentHandler> punishmentHandlers = new ArrayList<>();



    private List<Member> muteCache = new ArrayList<>();

    public void registerPunishmentHandler(PunishmentHandler handler){
        RubiconBot.getCommandManager().registerCommandHandler((CommandHandler) handler);
        punishmentHandlers.add(handler);
    }

    public void registerPunishmentHandlers(PunishmentHandler... handlers){
        for(PunishmentHandler handler : handlers){
            RubiconBot.getCommandManager().registerCommandHandler((CommandHandler) handler);
        }
        Collections.addAll(punishmentHandlers, handlers);
    }

    public void loadPunishments(){
        punishmentHandlers.forEach(PunishmentHandler::loadPunishments);
    }

    public List<Member> getMuteCache() {
        return muteCache;
    }



}
