package fun.rubicon.features;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PunishmentManager {

    private List<PunishmentHandler> punishmentHandlers = new ArrayList<>();

    private HashMap<Member, Long> muteCache = new HashMap<>();

    private boolean running = false;

    private Thread t;

    public void registerPunishmentHandler(PunishmentHandler handler){
        RubiconBot.getCommandManager().registerCommandHandler((CommandHandler) handler);
        punishmentHandlers.add(handler);
    }

    public void registerPunishmentHandlers(PunishmentHandler... handlers){
        Collections.addAll(punishmentHandlers, handlers);
        for(PunishmentHandler handler : handlers){
            RubiconBot.getCommandManager().registerCommandHandler((CommandHandler) handler);
        }
    }

    public synchronized void loadPunishments(){
        if(!running){
            Logger.info("Loading punishments in thread \"PunishmentLoader\"");
            t = new Thread(() -> {
                punishmentHandlers.forEach(PunishmentHandler::loadPunishments);
                Thread.currentThread().interrupt();
            }, "PunishmentLoader");
            t.start();
        }
    }

    public HashMap<Member, Long> getMuteCache() {
        return muteCache;
    }

}
