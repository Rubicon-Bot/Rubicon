package fun.rubicon.features;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandHandler;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PunishmentManager {

    private List<PunishmentHandler> punishmentHandlers = new ArrayList<>();



    private HashMap<Member, Long> muteCache = new HashMap<>();

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

    public HashMap<Member, Long> getMuteCache() {
        return muteCache;
    }



}
